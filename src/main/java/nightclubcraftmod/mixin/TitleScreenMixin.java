package nightclubcraftmod.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.client.gui.screen.DevToolsScreen;
import nightclubcraftmod.client.gui.screen.LoadGameScreen;
import nightclubcraftmod.client.gui.screen.SlotSelectionScreen;
import nightclubcraftmod.client.gui.widget.CustomButtonWidget;
import nightclubcraftmod.util.DevEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin para personalizar completamente la pantalla de título.
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    // Constantes para posicionamiento de botones
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 24;
    private static final int FIRST_BUTTON_Y = 100;

    // Textos personalizados para los botones
    private static final Text NEW_GAME_TEXT = Text.literal("NEW GAME...").styled(style -> style.withBold(true));
    private static final Text LOAD_GAME_TEXT = Text.literal("LOAD GAME").styled(style -> style.withBold(true));
    private static final Text MULTIPLAYER_TEXT = Text.literal("MULTIPLAYER").styled(style -> style.withBold(true));
    private static final Text SANDBOX_TEXT = Text.literal("SANDBOX").styled(style -> style.withBold(true));
    private static final Text OPTIONS_TEXT = Text.literal("OPTIONS").styled(style -> style.withBold(true));
    private static final Text EXIT_TEXT = Text.literal("QUIT GAME").styled(style -> style.withBold(true));
    private static final Text DEV_TOOLS_TEXT = Text.literal("DEV TOOLS").styled(style -> style.withBold(true).withColor(0xFF5555));

    // Identificadores de recursos
    private static final Identifier CUSTOM_LOGO = new Identifier(NightClubCraftMod.MOD_ID, "textures/gui/title/minecraft.png");
    private static final Identifier BACKGROUND_IMAGE = new Identifier(NightClubCraftMod.MOD_ID, "textures/gui/title/background.png");

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    /**
     * Reemplaza la inicialización de la pantalla de título.
     */
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void initCustomMenu(CallbackInfo ci) {
        try {
            // Registrar información de depuración
            NightClubCraftMod.LOGGER.info("Inicializando menú personalizado");

            int centerX = this.width / 2;
            int buttonY = FIRST_BUTTON_Y;

            // Botón "New Game" con la nueva clase CustomButtonWidget
            this.addDrawableChild(new CustomButtonWidget(
                centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                NEW_GAME_TEXT,
                button -> this.client.setScreen(new SlotSelectionScreen(this))
            ));
            buttonY += BUTTON_SPACING;

            // Botón "Load Game" con la nueva clase CustomButtonWidget
            this.addDrawableChild(new CustomButtonWidget(
                centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                LOAD_GAME_TEXT,
                button -> this.client.setScreen(new LoadGameScreen(this))
            ));
            buttonY += BUTTON_SPACING;
            
            // Botón "Sandbox" deshabilitado con tooltip
            CustomButtonWidget sandboxButton = new CustomButtonWidget(
                centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                SANDBOX_TEXT,
                button -> {}
            );
            
            // Configurar el botón como deshabilitado y añadir tooltip
            sandboxButton.active = false;
            sandboxButton.setTooltip(Tooltip.of(Text.literal("Soon")));
            
            this.addDrawableChild(sandboxButton);
            buttonY += BUTTON_SPACING;

            // Botón "Multiplayer" con la nueva clase CustomButtonWidget
            this.addDrawableChild(new CustomButtonWidget(
                centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                MULTIPLAYER_TEXT,
                button -> this.client.setScreen(new MultiplayerScreen(this))
            ));
            buttonY += BUTTON_SPACING;

            // Botón "Options" con la nueva clase CustomButtonWidget
            this.addDrawableChild(new CustomButtonWidget(
                centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                OPTIONS_TEXT,
                button -> this.client.setScreen(new OptionsScreen(this, this.client.options))
            ));
            buttonY += BUTTON_SPACING;

            // Botón "Exit" con la nueva clase CustomButtonWidget
            this.addDrawableChild(new CustomButtonWidget(
                centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                EXIT_TEXT,
                button -> this.client.scheduleStop()
            ));
            
            // Añadir botón de herramientas de desarrollo solo en entorno de desarrollo
            if (DevEnvironment.isDevelopmentEnvironment()) {
                // Botón "Dev Tools" con estilo especial
                ButtonWidget devToolsButton = ButtonWidget.builder(
                    DEV_TOOLS_TEXT,
                    button -> this.client.setScreen(new DevToolsScreen(this))
                ).dimensions(5, 5, 100, 20).build();
                
                // Añadir tooltip explicativo
                devToolsButton.setTooltip(Tooltip.of(Text.literal("Development Tools - Only visible in dev environment")));
                
                this.addDrawableChild(devToolsButton);
                
                // Añadir indicador de entorno de desarrollo
                NightClubCraftMod.LOGGER.info("Development environment detected - Dev Tools enabled");
            }

            // Cancelar la inicialización original
            ci.cancel();
        } catch (Exception e) {
            NightClubCraftMod.LOGGER.error("Error al inicializar menú personalizado: " + e.getMessage(), e);
        }
    }

    /**
     * Reemplaza el método de renderizado de fondo para usar nuestro propio fondo.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderCustomBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        try {
            // Renderizar fondo personalizado con la imagen de fondo
            try {
                // Dibujar la imagen de fondo a pantalla completa
                context.drawTexture(BACKGROUND_IMAGE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            } catch (Exception e) {
                NightClubCraftMod.LOGGER.error("Error al cargar la imagen de fondo: " + e.getMessage());
                // Si falla, usar el fondo dividido como respaldo
                context.fill(0, 0, this.width / 2, this.height, 0xFF000000);
                context.fill(this.width / 2, 0, this.width, this.height, 0xFFFF00FF);
            }

            // Dibujar el logo personalizado
            int logoWidth = 256;
            int logoHeight = 44;
            int logoX = (this.width - logoWidth) / 2;
            int logoY = 30;

            try {
                context.drawTexture(CUSTOM_LOGO, logoX, logoY, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);
            } catch (Exception e) {
                NightClubCraftMod.LOGGER.error("Error al cargar el logo personalizado: " + e.getMessage());
                // Si falla, usar el logo de Minecraft como respaldo
                Identifier minecraftLogo = new Identifier("minecraft", "textures/gui/title/minecraft.png");
                context.drawTexture(minecraftLogo, logoX, logoY, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);
            }

            // Renderizar los widgets (botones)
            super.render(context, mouseX, mouseY, delta);

            // Añadir texto de versión
            String version = "NightClubCraft " + NightClubCraftMod.MOD_VERSION;
            int versionX = 2;
            int versionY = this.height - 10;
            context.drawTextWithShadow(this.textRenderer, version, versionX, versionY, 0xFFFFFF);
            
            // Añadir indicador de entorno de desarrollo
            if (DevEnvironment.isDevelopmentEnvironment()) {
                String devEnvText = "DEVELOPMENT BUILD";
                int devTextWidth = this.textRenderer.getWidth(devEnvText);
                context.drawTextWithShadow(this.textRenderer, devEnvText, this.width - devTextWidth - 2, this.height - 10, 0xFF5555);
            }

            // Cancelar el renderizado original
            ci.cancel();
        } catch (Exception e) {
            NightClubCraftMod.LOGGER.error("Error al renderizar fondo personalizado: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}