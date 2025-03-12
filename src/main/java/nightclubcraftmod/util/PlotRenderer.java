package nightclubcraftmod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.item.DevelopmentStickItem;
import nightclubcraftmod.plot.Plot;
import nightclubcraftmod.plot.PlotManager;
import nightclubcraftmod.plot.PlotSelection;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Renderiza el feedback visual para la selección de plots.
 */
public class PlotRenderer {
    
    // Colores para el renderizado
    private static final float[] FRAME_COLOR = {1.0f, 0.0f, 0.0f, 1.0f}; // Rojo brillante
    private static final float[] CORNER_COLOR = {1.0f, 1.0f, 0.0f, 1.0f}; // Amarillo brillante
    
    // Tamaño de los elementos
    private static final float LINE_WIDTH = 5.0f; // Líneas mucho más gruesas
    private static final float CORNER_SIZE = 0.3f; // Tamaño de las esferas en las esquinas
    private static final int SPHERE_SEGMENTS = 8; // Número de segmentos para las esferas
    
    /**
     * Renderiza la selección actual del plot y los plots con contorno visible.
     * 
     * @param matrixStack La matriz de transformación
     * @param player El jugador
     * @param partialTicks El tiempo parcial para animaciones
     */
    public static void renderPlotSelection(MatrixStack matrixStack, PlayerEntity player, float partialTicks) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Renderizar la selección actual si el jugador tiene el Development Stick
        renderCurrentSelection(matrixStack, player);
        
        // Renderizar los plots con contorno visible
        renderVisiblePlots(matrixStack, player.getWorld());
        
        // Debug: Mostrar información sobre los plots
        debugPlotInfo(player);
    }
    
    /**
     * Muestra información de depuración sobre los plots.
     * 
     * @param player El jugador
     */
    private static void debugPlotInfo(PlayerEntity player) {
        if (player.age % 100 == 0) { // Solo mostrar cada 5 segundos aproximadamente
            List<Plot> playerPlots = PlotManager.getInstance().getPlayerPlots(player.getUuid());
            
            if (!playerPlots.isEmpty()) {
                NightClubCraftMod.LOGGER.info("Plots del jugador: " + playerPlots.size());
                
                for (Plot plot : playerPlots) {
                    NightClubCraftMod.LOGGER.info("Plot ID: " + plot.getId() + ", Visible: " + plot.isOutlineVisible());
                    
                    // Mostrar mensaje al jugador para confirmar
                    if (plot.isOutlineVisible()) {
                        player.sendMessage(Text.literal("§dDebug: Plot " + plot.getId().toString().substring(0, 8) + " está visible"), false);
                    }
                }
            } else {
                NightClubCraftMod.LOGGER.info("El jugador no tiene plots");
            }
        }
    }
    
    /**
     * Renderiza la selección actual si el jugador tiene el Development Stick.
     * 
     * @param matrixStack La matriz de transformación
     * @param player El jugador
     */
    private static void renderCurrentSelection(MatrixStack matrixStack, PlayerEntity player) {
        // Comprobar si el jugador tiene el Development Stick en la mano
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        
        ItemStack stack = null;
        if (mainHand.getItem() instanceof DevelopmentStickItem) {
            stack = mainHand;
        } else if (offHand.getItem() instanceof DevelopmentStickItem) {
            stack = offHand;
        }
        
        if (stack == null) {
            return;
        }
        
        // Obtener la selección actual
        PlotSelection selection = DevelopmentStickItem.getCurrentSelection(stack, player);
        if (selection == null) {
            return;
        }
        
        // Renderizar la selección
        renderSelection(matrixStack, selection.toBox());
    }
    
    /**
     * Renderiza los plots con contorno visible.
     * 
     * @param matrixStack La matriz de transformación
     * @param world El mundo
     */
    private static void renderVisiblePlots(MatrixStack matrixStack, World world) {
        if (!(world instanceof ClientWorld clientWorld)) {
            return;
        }
        
        // Obtener el jugador actual
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        
        // Obtener TODOS los plots (para pruebas)
        Collection<Plot> allPlots = PlotManager.getInstance().getAllPlots();
        
        // Renderizar todos los plots para pruebas
        for (Plot plot : allPlots) {
            // Renderizar todos los plots para depuración
            Box box = plot.getSelection().toBox();
            renderSelection(matrixStack, box);
            
            // Mostrar información en el registro
            if (client.player.age % 100 == 0) { // Solo cada 5 segundos aproximadamente
                NightClubCraftMod.LOGGER.info("Renderizando plot: " + plot.getId() + ", Visible: " + plot.isOutlineVisible());
            }
        }
    }
    
    /**
     * Renderiza una selección.
     * 
     * @param matrixStack La matriz de transformación
     * @param box La caja de la selección
     */
    private static void renderSelection(MatrixStack matrixStack, Box box) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Obtener la posición de la cámara
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        
        // Configurar el renderizado
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(LINE_WIDTH);
        
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        // Renderizar el marco
        renderFrame(matrixStack, box);
        
        // Renderizar las esferas en las esquinas
        renderCornerSpheres(matrixStack, box);
        
        matrixStack.pop();
        
        // Restaurar el estado de renderizado
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
    }
    
    /**
     * Renderiza el marco de la selección.
     * 
     * @param matrixStack La matriz de transformación
     * @param box La caja de la selección
     */
    private static void renderFrame(MatrixStack matrixStack, Box box) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        // Líneas horizontales inferiores
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        // Líneas horizontales superiores
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        // Líneas verticales
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(FRAME_COLOR[0], FRAME_COLOR[1], FRAME_COLOR[2], FRAME_COLOR[3]).next();
        
        tessellator.draw();
    }
    
    /**
     * Renderiza esferas en las esquinas de la selección.
     * 
     * @param matrixStack La matriz de transformación
     * @param box La caja de la selección
     */
    private static void renderCornerSpheres(MatrixStack matrixStack, Box box) {
        // Renderizar esferas en las 8 esquinas
        renderSphere(matrixStack, (float) box.minX, (float) box.minY, (float) box.minZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.maxX, (float) box.minY, (float) box.minZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.maxX, (float) box.minY, (float) box.maxZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.minX, (float) box.minY, (float) box.maxZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.minX, (float) box.maxY, (float) box.minZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.maxX, (float) box.maxY, (float) box.minZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.maxX, (float) box.maxY, (float) box.maxZ, CORNER_SIZE, CORNER_COLOR);
        renderSphere(matrixStack, (float) box.minX, (float) box.maxY, (float) box.maxZ, CORNER_SIZE, CORNER_COLOR);
    }
    
    /**
     * Renderiza una esfera en la posición dada.
     * 
     * @param matrixStack La matriz de transformación
     * @param x La coordenada X
     * @param y La coordenada Y
     * @param z La coordenada Z
     * @param radius El radio de la esfera
     * @param color El color de la esfera
     */
    private static void renderSphere(MatrixStack matrixStack, float x, float y, float z, float radius, float[] color) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        
        // Renderizar la esfera usando triángulos
        for (int i = 0; i <= SPHERE_SEGMENTS; i++) {
            float latitude = (float) Math.PI * (float) i / SPHERE_SEGMENTS;
            float sinLatitude = (float) Math.sin(latitude);
            float cosLatitude = (float) Math.cos(latitude);
            
            for (int j = 0; j <= SPHERE_SEGMENTS; j++) {
                float longitude = 2 * (float) Math.PI * (float) j / SPHERE_SEGMENTS;
                float sinLongitude = (float) Math.sin(longitude);
                float cosLongitude = (float) Math.cos(longitude);
                
                float xOffset = radius * sinLatitude * cosLongitude;
                float yOffset = radius * cosLatitude;
                float zOffset = radius * sinLatitude * sinLongitude;
                
                buffer.vertex(matrix, x + xOffset, y + yOffset, z + zOffset)
                      .color(color[0], color[1], color[2], color[3])
                      .next();
                
                float nextLatitude = (float) Math.PI * (float) (i + 1) / SPHERE_SEGMENTS;
                float sinNextLatitude = (float) Math.sin(nextLatitude);
                float cosNextLatitude = (float) Math.cos(nextLatitude);
                
                xOffset = radius * sinNextLatitude * cosLongitude;
                yOffset = radius * cosNextLatitude;
                zOffset = radius * sinNextLatitude * sinLongitude;
                
                buffer.vertex(matrix, x + xOffset, y + yOffset, z + zOffset)
                      .color(color[0], color[1], color[2], color[3])
                      .next();
            }
        }
        
        tessellator.draw();
    }
} 