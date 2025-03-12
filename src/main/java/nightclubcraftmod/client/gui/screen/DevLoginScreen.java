package nightclubcraftmod.client.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.network.ClientNetworking;

/**
 * Pantalla de inicio de sesión para desarrolladores.
 * Permite a los desarrolladores autenticarse con una contraseña.
 */
public class DevLoginScreen extends Screen {
    
    private final Screen parent;
    private TextFieldWidget passwordField;
    private ButtonWidget loginButton;
    private ButtonWidget cancelButton;
    
    private String errorMessage = null;
    private boolean isLoggingIn = false;
    private int animationTicks = 0;
    
    // Constantes para la UI
    private static final int FIELD_WIDTH = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    
    // Textura de fondo
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("minecraft", "textures/block/dark_oak_planks.png");
    
    public DevLoginScreen(Screen parent) {
        super(Text.literal("Developer Login"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Campo de contraseña
        this.passwordField = new TextFieldWidget(
            this.textRenderer,
            centerX - FIELD_WIDTH / 2,
            centerY - 20,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            Text.literal("Password")
        );
        this.passwordField.setMaxLength(32);
        this.passwordField.setText("");
        this.passwordField.setVisible(true);
        this.passwordField.setEditable(true);
        this.passwordField.setDrawsBackground(true);
        this.passwordField.setFocused(true);
        
        // Hacer que el campo de contraseña oculte el texto
        this.passwordField.setRenderTextProvider((text, index) -> {
            return Text.literal("*".repeat(text.length())).asOrderedText();
        });
        
        this.addSelectableChild(this.passwordField);
        
        // Botón de inicio de sesión
        this.loginButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Login"),
            button -> this.tryLogin()
        ).dimensions(centerX - BUTTON_WIDTH - 5, centerY + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        
        // Botón de cancelar
        this.cancelButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Cancel"),
            button -> this.close()
        ).dimensions(centerX + 5, centerY + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Renderizar fondo con textura
        renderBackground(context);
        
        // Dibujar un panel oscuro para el formulario
        int panelWidth = FIELD_WIDTH + 40;
        int panelHeight = 120;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;
        
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA000000);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFFFFFFF);
        
        // Renderizar título
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            panelY + 15,
            0xFFFFFF
        );
        
        // Renderizar etiqueta del campo de contraseña
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Password:"),
            this.passwordField.getX(),
            this.passwordField.getY() - 15,
            0xFFFFFF
        );
        
        // Renderizar campo de contraseña
        this.passwordField.render(context, mouseX, mouseY, delta);
        
        // Renderizar mensaje de error si existe
        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(errorMessage).formatted(Formatting.RED),
                this.width / 2,
                panelY + panelHeight - 30,
                0xFFFFFF
            );
        }
        
        // Renderizar animación de carga si está iniciando sesión
        if (isLoggingIn) {
            animationTicks++;
            int dotCount = (animationTicks / 10) % 4;
            String dots = ".".repeat(dotCount);
            
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Logging in" + dots).formatted(Formatting.YELLOW),
                this.width / 2,
                panelY + panelHeight - 30,
                0xFFFFFF
            );
            
            // Deshabilitar botones durante el inicio de sesión
            this.loginButton.active = false;
            this.cancelButton.active = false;
        } else {
            // Habilitar botones
            this.loginButton.active = true;
            this.cancelButton.active = true;
        }
        
        // Renderizar botones
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        
        // Renderizar textura de fondo
        int tileSize = 32;
        for (int x = 0; x < this.width; x += tileSize) {
            for (int y = 0; y < this.height; y += tileSize) {
                context.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, tileSize, tileSize, tileSize, tileSize);
            }
        }
        
        // Oscurecer el fondo
        context.fill(0, 0, this.width, this.height, 0x99000000);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Si se presiona Enter, intentar iniciar sesión
        if (keyCode == 257 || keyCode == 335) { // Enter o NumpadEnter
            this.tryLogin();
            return true;
        }
        
        // Si se presiona Escape, cerrar la pantalla
        if (keyCode == 256) { // Escape
            this.close();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    /**
     * Intenta iniciar sesión con la contraseña proporcionada.
     */
    private void tryLogin() {
        String password = this.passwordField.getText();
        
        if (password.isEmpty()) {
            this.errorMessage = "Please enter a password";
            return;
        }
        
        // Iniciar animación de carga
        this.isLoggingIn = true;
        this.errorMessage = null;
        
        // Enviar solicitud de inicio de sesión al servidor
        ClientNetworking.sendDevLoginRequest(password, success -> {
            // Este callback se ejecutará cuando se reciba la respuesta del servidor
            this.isLoggingIn = false;
            
            if (success) {
                // Inicio de sesión exitoso
                MinecraftClient.getInstance().execute(() -> {
                    this.close();
                    // Mostrar mensaje de éxito
                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("Developer authentication successful!").formatted(Formatting.GREEN),
                            false
                        );
                    }
                });
            } else {
                // Inicio de sesión fallido
                this.errorMessage = "Invalid password";
                this.passwordField.setText("");
            }
        });
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
} 