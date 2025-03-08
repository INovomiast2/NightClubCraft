package nightclubcraftmod.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;

/**
 * Botón personalizado que utiliza texturas personalizadas para el mod NightClubCraft.
 */
public class CustomButtonWidget extends ButtonWidget {
    
    private static final Identifier BUTTON_TEXTURE = new Identifier(NightClubCraftMod.MOD_ID, "textures/gui/widgets/button/button.png");
    private static final Identifier BUTTON_HIGHLIGHTED_TEXTURE = new Identifier(NightClubCraftMod.MOD_ID, "textures/gui/widgets/button/button_highlighted.png");
    
    // Dimensiones de la textura
    private static final int TEXTURE_WIDTH = 200;
    private static final int TEXTURE_HEIGHT = 20;
    
    public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }
    
    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        // Determinar qué textura usar basado en el estado del botón
        Identifier texture = this.isHovered() || this.isFocused() ? BUTTON_HIGHLIGHTED_TEXTURE : BUTTON_TEXTURE;
        
        // Calcular las coordenadas UV para el renderizado de la textura
        int u = 0;
        int v = 0;
        
        // Renderizar la textura del botón usando el método de 9-slice para permitir diferentes tamaños
        // Esquinas (4 píxeles cada una)
        int cornerSize = 4;
        
        // Parte superior izquierda
        context.drawTexture(texture, this.getX(), this.getY(), u, v, cornerSize, cornerSize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // Parte superior derecha
        context.drawTexture(texture, this.getX() + this.width - cornerSize, this.getY(), u + TEXTURE_WIDTH - cornerSize, v, cornerSize, cornerSize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // Parte inferior izquierda
        context.drawTexture(texture, this.getX(), this.getY() + this.height - cornerSize, u, v + TEXTURE_HEIGHT - cornerSize, cornerSize, cornerSize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // Parte inferior derecha
        context.drawTexture(texture, this.getX() + this.width - cornerSize, this.getY() + this.height - cornerSize, u + TEXTURE_WIDTH - cornerSize, v + TEXTURE_HEIGHT - cornerSize, cornerSize, cornerSize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        
        // Bordes
        // Superior
        context.drawTexture(texture, this.getX() + cornerSize, this.getY(), u + cornerSize, v, this.width - cornerSize * 2, cornerSize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // Inferior
        context.drawTexture(texture, this.getX() + cornerSize, this.getY() + this.height - cornerSize, u + cornerSize, v + TEXTURE_HEIGHT - cornerSize, this.width - cornerSize * 2, cornerSize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // Izquierdo
        context.drawTexture(texture, this.getX(), this.getY() + cornerSize, u, v + cornerSize, cornerSize, this.height - cornerSize * 2, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // Derecho
        context.drawTexture(texture, this.getX() + this.width - cornerSize, this.getY() + cornerSize, u + TEXTURE_WIDTH - cornerSize, v + cornerSize, cornerSize, this.height - cornerSize * 2, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        
        // Centro
        context.drawTexture(texture, this.getX() + cornerSize, this.getY() + cornerSize, u + cornerSize, v + cornerSize, this.width - cornerSize * 2, this.height - cornerSize * 2, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        
        // Renderizar el texto del botón
        int textColor = this.active ? 0xFFFFFF : 0xAAAAAA;
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, textColor);
    }
    
    /**
     * Método de fábrica para crear un botón personalizado.
     */
    public static Builder builder(Text message, PressAction onPress) {
        return ButtonWidget.builder(message, onPress);
    }
} 