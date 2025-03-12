package nightclubcraftmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.util.PlotRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin para renderizar la selección del plot.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    
    private static long lastRenderTime = 0;
    
    /**
     * Renderiza la selección del plot después de renderizar el mundo.
     * Usamos RETURN para asegurarnos de que se renderiza después de todo lo demás.
     */
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, 
                          Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, 
                          Matrix4f positionMatrix, CallbackInfo ci) {
        
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        
        if (player != null) {
            // Renderizar la selección del plot
            PlotRenderer.renderPlotSelection(matrices, player, tickDelta);
            
            // Mostrar mensaje de depuración cada 5 segundos
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRenderTime > 5000) {
                lastRenderTime = currentTime;
                NightClubCraftMod.LOGGER.info("Renderizando bordes de plots");
                player.sendMessage(Text.literal("§dDebug: Renderizando bordes de plots"), false);
            }
        }
    }
} 