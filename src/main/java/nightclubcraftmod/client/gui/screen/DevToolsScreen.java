package nightclubcraftmod.client.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.client.gui.widget.CustomButtonWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de herramientas de desarrollo para pruebas rápidas.
 * Esta pantalla solo está disponible en entornos de desarrollo.
 */
public class DevToolsScreen extends Screen {
    
    private final Screen parent;
    
    // Lista de elementos de la interfaz para el scroll
    private final List<UIElement> uiElements = new ArrayList<>();
    
    // Widgets para la configuración
    private TextFieldWidget seedField;
    private ButtonWidget backButton;
    
    // Constantes para la UI
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_WIDTH = 300;
    private static final int FIELD_HEIGHT = 20;
    private static final int SPACING = 24;
    private static final int SECTION_SPACING = 40;
    
    // Variables para el scroll
    private float scrollPosition = 0;
    private int contentHeight = 0;
    private boolean isDragging = false;
    private static final int SCROLL_BAR_WIDTH = 6;
    private static final int SCROLL_SPEED = 10;
    
    // Identificadores de recursos
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("minecraft", "textures/block/dark_oak_planks.png");
    
    public DevToolsScreen(Screen parent) {
        super(Text.literal("Developer Tools"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 60;
        int contentY = 0;
        
        // Limpiar elementos anteriores
        this.uiElements.clear();
        
        // ===== SECCIÓN: CREACIÓN RÁPIDA DE MUNDOS =====
        
        // Añadir título de sección
        this.uiElements.add(new SectionTitleElement("Quick World Creation", contentY, 0xFFFF55));
        contentY += 20;
        
        // Botón para crear un mundo de prueba básico
        ButtonWidget testWorldButton = ButtonWidget.builder(
            Text.literal("Create Test World (Normal)"),
            button -> createTestWorld(false, "test_world")
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(testWorldButton, contentY));
        contentY += SPACING;
        
        // Botón para crear un mundo de prueba en modo creativo
        ButtonWidget sandboxTestButton = ButtonWidget.builder(
            Text.literal("Create Creative Test World"),
            button -> createTestWorld(true, "creative_test")
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(sandboxTestButton, contentY));
        contentY += SPACING;
        
        // Campo para la semilla
        this.seedField = new TextFieldWidget(
            this.textRenderer, 
            centerX - FIELD_WIDTH / 2, 
            contentY, 
            FIELD_WIDTH, 
            FIELD_HEIGHT, 
            Text.literal("Seed")
        );
        this.seedField.setMaxLength(32);
        this.seedField.setText("dev_test_seed");
        this.uiElements.add(new TextFieldElement(this.seedField, contentY));
        contentY += SPACING;
        
        // Botón para crear un mundo de prueba con la semilla especificada
        ButtonWidget customTestButton = ButtonWidget.builder(
            Text.literal("Create World with Custom Seed"),
            button -> createTestWorld(true, this.seedField.getText())
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(customTestButton, contentY));
        contentY += SECTION_SPACING;
        
        // ===== SECCIÓN: MUNDOS EXISTENTES =====
        
        // Añadir título de sección
        this.uiElements.add(new SectionTitleElement("Existing Dev Worlds", contentY, 0xFFFF55));
        contentY += 20;
        
        // Botón para abrir la pantalla de selección de mundos
        ButtonWidget worldSelectionButton = ButtonWidget.builder(
            Text.literal("Open World Selection Screen"),
            button -> this.client.setScreen(new SelectWorldScreen(this))
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(worldSelectionButton, contentY));
        contentY += SPACING;
        
        // Añadir texto informativo
        this.uiElements.add(new TextElement(
            "Click 'Open World Selection Screen' to access your dev worlds",
            centerX,
            contentY,
            0xCCCCCC,
            true
        ));
        contentY += 15;
        
        this.uiElements.add(new TextElement(
            "Dev worlds are prefixed with 'Dev_' for easy identification",
            centerX,
            contentY,
            0xCCCCCC,
            true
        ));
        contentY += SECTION_SPACING;
        
        // ===== SECCIÓN: PRUEBA DE CARACTERÍSTICAS =====
        
        // Añadir título de sección
        this.uiElements.add(new SectionTitleElement("Feature Testing", contentY, 0xFFFF55));
        contentY += 20;
        
        // Botón para probar componentes de UI
        ButtonWidget testUIButton = ButtonWidget.builder(
            Text.literal("Test UI Components"),
            button -> testUIComponents()
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(testUIButton, contentY));
        contentY += SPACING;
        
        // ===== SECCIÓN: HERRAMIENTAS ADICIONALES =====
        
        // Añadir título de sección
        this.uiElements.add(new SectionTitleElement("Additional Tools", contentY, 0xFFFF55));
        contentY += 20;
        
        // Botón para probar generación de estructuras
        ButtonWidget structureButton = ButtonWidget.builder(
            Text.literal("Test Structure Generation"),
            button -> NightClubCraftMod.LOGGER.info("Structure generation test - Not implemented yet")
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(structureButton, contentY));
        contentY += SPACING;
        
        // Botón para probar efectos visuales
        ButtonWidget visualEffectsButton = ButtonWidget.builder(
            Text.literal("Test Visual Effects"),
            button -> NightClubCraftMod.LOGGER.info("Visual effects test - Not implemented yet")
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(visualEffectsButton, contentY));
        contentY += SPACING;
        
        // Botón para probar sistema de audio
        ButtonWidget audioButton = ButtonWidget.builder(
            Text.literal("Test Audio System"),
            button -> NightClubCraftMod.LOGGER.info("Audio system test - Not implemented yet")
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(audioButton, contentY));
        contentY += SPACING;
        
        // Botón para probar sistema de NPCs
        ButtonWidget npcButton = ButtonWidget.builder(
            Text.literal("Test NPC System"),
            button -> NightClubCraftMod.LOGGER.info("NPC system test - Not implemented yet")
        ).dimensions(centerX - BUTTON_WIDTH / 2, contentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.uiElements.add(new ButtonElement(npcButton, contentY));
        contentY += SECTION_SPACING;
        
        // Actualizar la altura total del contenido
        this.contentHeight = contentY;
        
        // ===== BOTÓN DE REGRESO =====
        
        // Botón para volver a la pantalla de título (siempre visible, fuera del scroll)
        this.backButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Back to Title Screen"),
            button -> this.close()
        ).dimensions(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Renderizar fondo con textura
        drawCustomBackground(context);
        
        // Renderizar título principal
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            this.title, 
            this.width / 2, 
            20, 
            0xFF5555
        );
        
        // Renderizar subtítulo
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            Text.literal("Development Tools - Not available in release builds"),
            this.width / 2, 
            35, 
            0xAAAAAA
        );
        
        // Definir el área de contenido
        int contentX = (this.width - BUTTON_WIDTH) / 2 - 20;
        int contentY = 60;
        int contentWidth = BUTTON_WIDTH + 40;
        int contentAreaHeight = this.height - 110;
        
        // Guardar y configurar el área de recorte
        context.enableScissor(
            contentX, 
            contentY, 
            contentX + contentWidth, 
            contentY + contentAreaHeight
        );
        
        // Renderizar elementos de UI con desplazamiento
        int yOffset = (int) -scrollPosition;
        for (UIElement element : uiElements) {
            element.render(context, mouseX, mouseY, delta, yOffset);
        }
        
        // Desactivar el área de recorte
        context.disableScissor();
        
        // Renderizar barra de desplazamiento si es necesario
        if (this.contentHeight > contentAreaHeight) {
            renderScrollbar(context, contentX + contentWidth - SCROLL_BAR_WIDTH - 2, contentY, 
                    SCROLL_BAR_WIDTH, contentAreaHeight, contentAreaHeight);
        }
        
        // Renderizar información de versión
        String versionInfo = "NightClubCraft " + NightClubCraftMod.MOD_VERSION + " (Dev Build)";
        context.drawTextWithShadow(
            this.textRenderer, 
            versionInfo, 
            5, 
            this.height - 15, 
            0xAAAAAA
        );
        
        // Renderizar widgets que están fuera del área de scroll
        super.render(context, mouseX, mouseY, delta);
    }
    
    /**
     * Renderiza la barra de desplazamiento.
     */
    private void renderScrollbar(DrawContext context, int x, int y, int width, int height, int viewportHeight) {
        // Calcular la proporción visible
        float contentRatio = (float) viewportHeight / this.contentHeight;
        float scrollRatio = this.scrollPosition / (this.contentHeight - viewportHeight);
        
        // Calcular dimensiones de la barra
        int barHeight = Math.max(20, (int) (height * contentRatio));
        int barY = y + (int) ((height - barHeight) * scrollRatio);
        
        // Dibujar fondo de la barra
        context.fill(x, y, x + width, y + height, 0x40000000);
        
        // Dibujar la barra de desplazamiento
        context.fill(x, barY, x + width, barY + barHeight, 0x80FFFFFF);
    }
    
    /**
     * Renderiza un fondo personalizado para la pantalla.
     */
    private void drawCustomBackground(DrawContext context) {
        // Oscurecer el fondo
        context.fill(0, 0, this.width, this.height, 0xC0000000);
        
        // Dibujar un panel central semitransparente
        int panelWidth = BUTTON_WIDTH + 40;
        int panelHeight = this.height - 50;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = 40;
        
        // Fondo del panel
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x80000000);
        
        // Borde del panel
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0x40FFFFFF);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // Calcular el área de contenido
        int contentAreaHeight = this.height - 110;
        
        // Actualizar la posición de desplazamiento
        this.scrollPosition = MathHelper.clamp(
            this.scrollPosition - (float) (amount * SCROLL_SPEED),
            0,
            Math.max(0, this.contentHeight - contentAreaHeight)
        );
        
        return true;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Calcular el área de la barra de desplazamiento
        int contentX = (this.width - BUTTON_WIDTH) / 2 - 20;
        int contentY = 60;
        int contentWidth = BUTTON_WIDTH + 40;
        int contentAreaHeight = this.height - 110;
        int scrollbarX = contentX + contentWidth - SCROLL_BAR_WIDTH - 2;
        
        // Comprobar si se ha hecho clic en la barra de desplazamiento
        if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLL_BAR_WIDTH &&
            mouseY >= contentY && mouseY <= contentY + contentAreaHeight) {
            this.isDragging = true;
            return true;
        }
        
        // Comprobar si se ha hecho clic en algún elemento
        int yOffset = (int) -scrollPosition;
        for (UIElement element : uiElements) {
            if (element.mouseClicked(mouseX, mouseY, button, yOffset)) {
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isDragging) {
            // Calcular el área de contenido
            int contentY = 60;
            int contentAreaHeight = this.height - 110;
            
            // Calcular la nueva posición de desplazamiento
            float dragRatio = (float) deltaY / contentAreaHeight;
            float scrollDelta = dragRatio * this.contentHeight;
            
            // Actualizar la posición de desplazamiento
            this.scrollPosition = MathHelper.clamp(
                this.scrollPosition + scrollDelta,
                0,
                Math.max(0, this.contentHeight - contentAreaHeight)
            );
            
            return true;
        }
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isDragging) {
            this.isDragging = false;
            return true;
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    /**
     * Crea un mundo de prueba con la configuración especificada.
     * 
     * @param creative Si es true, el mundo se crea en modo creativo.
     * @param seed La semilla para el mundo.
     */
    private void createTestWorld(boolean creative, String seed) {
        NightClubCraftMod.LOGGER.info("Creating test world with seed: {} (Creative: {})", seed, creative);
        
        // Crear un mundo de prueba utilizando la pantalla de creación de mundo estándar
        // pero con valores predefinidos
        String worldName = "Dev_" + (creative ? "Creative_" : "Survival_") + seed + "_" + System.currentTimeMillis();
        
        // Abrir la pantalla de creación de mundo estándar
        // En Minecraft 1.20.1, el método create() no devuelve la pantalla, sino que la configura directamente
        CreateWorldScreen.create(this.client, this);
        
        // Mostrar mensaje informativo
        NightClubCraftMod.LOGGER.info("Opened world creation screen with suggested name: {}", worldName);
        NightClubCraftMod.LOGGER.info("Please configure the world as needed and click 'Create New World'");
        NightClubCraftMod.LOGGER.info("Recommended settings for development:");
        NightClubCraftMod.LOGGER.info("- World name: {}", worldName);
        NightClubCraftMod.LOGGER.info("- Game mode: {}", creative ? "Creative" : "Survival");
        NightClubCraftMod.LOGGER.info("- Allow cheats: ON");
    }
    
    /**
     * Abre una pantalla para probar componentes de UI.
     */
    private void testUIComponents() {
        NightClubCraftMod.LOGGER.info("Testing UI components");
        
        // Abrir la pantalla de nuevo juego con un slot predefinido para pruebas
        this.client.setScreen(new NewGameScreen(this, 0));
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    // ===== CLASES PARA ELEMENTOS DE UI =====
    
    /**
     * Interfaz base para elementos de UI en el área de desplazamiento.
     */
    private interface UIElement {
        void render(DrawContext context, int mouseX, int mouseY, float delta, int yOffset);
        boolean mouseClicked(double mouseX, double mouseY, int button, int yOffset);
    }
    
    /**
     * Elemento de UI para botones.
     */
    private class ButtonElement implements UIElement {
        private final ButtonWidget button;
        private final int originalY;
        
        public ButtonElement(ButtonWidget button, int y) {
            this.button = button;
            this.originalY = y;
        }
        
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta, int yOffset) {
            // Actualizar la posición Y del botón
            button.setY(originalY + yOffset);
            
            // Renderizar el botón solo si está visible
            if (isVisible(yOffset)) {
                button.render(context, mouseX, mouseY, delta);
            }
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, int yOffset) {
            // Comprobar si el botón está visible
            if (isVisible(yOffset)) {
                // Actualizar la posición Y del botón
                this.button.setY(originalY + yOffset);
                
                // Comprobar si se ha hecho clic en el botón
                if (this.button.isMouseOver(mouseX, mouseY)) {
                    this.button.onClick(mouseX, mouseY);
                    return true;
                }
            }
            
            return false;
        }
        
        private boolean isVisible(int yOffset) {
            int y = originalY + yOffset;
            return y + BUTTON_HEIGHT >= 60 && y <= height - 50;
        }
    }
    
    /**
     * Elemento de UI para campos de texto.
     */
    private class TextFieldElement implements UIElement {
        private final TextFieldWidget textField;
        private final int originalY;
        
        public TextFieldElement(TextFieldWidget textField, int y) {
            this.textField = textField;
            this.originalY = y;
        }
        
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta, int yOffset) {
            // Actualizar la posición Y del campo de texto
            textField.setY(originalY + yOffset);
            
            // Renderizar el campo de texto solo si está visible
            if (isVisible(yOffset)) {
                textField.render(context, mouseX, mouseY, delta);
            }
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, int yOffset) {
            // Comprobar si el campo de texto está visible
            if (isVisible(yOffset)) {
                // Actualizar la posición Y del campo de texto
                textField.setY(originalY + yOffset);
                
                // Comprobar si se ha hecho clic en el campo de texto
                if (mouseX >= textField.getX() && mouseX < textField.getX() + textField.getWidth() &&
                    mouseY >= textField.getY() && mouseY < textField.getY() + textField.getHeight()) {
                    textField.setFocused(true);
                    if (button == 0) {
                        textField.onClick(mouseX, mouseY);
                    }
                    return true;
                } else {
                    textField.setFocused(false);
                }
            }
            
            return false;
        }
        
        private boolean isVisible(int yOffset) {
            int y = originalY + yOffset;
            return y + FIELD_HEIGHT >= 60 && y <= height - 50;
        }
    }
    
    /**
     * Elemento de UI para texto.
     */
    private class TextElement implements UIElement {
        private final String text;
        private final int x;
        private final int originalY;
        private final int color;
        private final boolean centered;
        
        public TextElement(String text, int x, int y, int color, boolean centered) {
            this.text = text;
            this.x = x;
            this.originalY = y;
            this.color = color;
            this.centered = centered;
        }
        
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta, int yOffset) {
            // Renderizar el texto solo si está visible
            if (isVisible(yOffset)) {
                if (centered) {
                    context.drawCenteredTextWithShadow(
                        textRenderer,
                        text,
                        x,
                        originalY + yOffset,
                        color
                    );
                } else {
                    context.drawTextWithShadow(
                        textRenderer,
                        text,
                        x,
                        originalY + yOffset,
                        color
                    );
                }
            }
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, int yOffset) {
            // El texto no es interactivo
            return false;
        }
        
        private boolean isVisible(int yOffset) {
            int y = originalY + yOffset;
            return y + textRenderer.fontHeight >= 60 && y <= height - 50;
        }
    }
    
    /**
     * Elemento de UI para títulos de sección.
     */
    private class SectionTitleElement implements UIElement {
        private final String title;
        private final int originalY;
        private final int color;
        
        public SectionTitleElement(String title, int y, int color) {
            this.title = title;
            this.originalY = y;
            this.color = color;
        }
        
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta, int yOffset) {
            // Renderizar el título solo si está visible
            if (isVisible(yOffset)) {
                int centerX = width / 2;
                int lineWidth = 150;
                int y = originalY + yOffset;
                
                // Dibujar líneas separadoras
                context.fill(centerX - lineWidth - 10, y + 7, centerX - 10, y + 8, color);
                context.fill(centerX + 10, y + 7, centerX + lineWidth + 10, y + 8, color);
                
                // Dibujar título
                context.drawCenteredTextWithShadow(
                    textRenderer, 
                    Text.literal(title).styled(style -> style.withBold(true)),
                    centerX, 
                    y, 
                    color
                );
            }
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, int yOffset) {
            // El título no es interactivo
            return false;
        }
        
        private boolean isVisible(int yOffset) {
            int y = originalY + yOffset;
            return y + 15 >= 60 && y <= height - 50;
        }
    }
} 