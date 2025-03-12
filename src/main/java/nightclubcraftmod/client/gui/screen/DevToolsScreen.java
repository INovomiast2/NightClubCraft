package nightclubcraftmod.client.gui.screen;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Color;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import nightclubcraftmod.NightClubCraftMod;

/**
 * Pantalla de herramientas de desarrollo con estilo cyberpunk/neón.
 * Esta pantalla solo está disponible en entornos de desarrollo.
 */
public class DevToolsScreen extends CottonClientScreen {
    
    // Identificadores de recursos para texturas
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("minecraft", "textures/block/black_concrete.png");
    private static final Identifier NEON_BORDER = new Identifier("minecraft", "textures/block/magenta_concrete.png");
    private static final Identifier PANEL_TEXTURE = new Identifier("minecraft", "textures/block/gray_concrete.png");
    
    // Colores neón vibrantes
    private static final int NEON_PINK = 0xFF00FF;
    private static final int NEON_BLUE = 0x00FFFF;
    private static final int NEON_GREEN = 0x00FF66;
    private static final int NEON_PURPLE = 0xAA00FF;
    private static final int NEON_YELLOW = 0xFFFF00;
    
    // Referencia a la pantalla padre
    private final Screen parent;
    
    /**
     * Constructor de la pantalla de herramientas de desarrollo.
     * 
     * @param parent La pantalla padre a la que volver al cerrar
     */
    public DevToolsScreen(Screen parent) {
        super(new CyberpunkGuiDescription(parent));
        this.parent = parent;
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
    
    /**
     * Descripción de la GUI para la pantalla de herramientas de desarrollo con estilo cyberpunk.
     */
    private static class CyberpunkGuiDescription extends LightweightGuiDescription {
        private final Screen parent;
        private WTextField seedField;
        
        // Contador para efectos de animación
        private int animationTick = 0;
        
        public CyberpunkGuiDescription(Screen parent) {
            this.parent = parent;
            
            // Panel principal con diseño personalizado
            WPlainPanel rootPanel = new WPlainPanel();
            setRootPanel(rootPanel);
            rootPanel.setSize(380, 240);
            
            // Fondo oscuro con efecto de "grid" cyberpunk
            rootPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                // Fondo negro base
                BackgroundPainter.VANILLA.paintBackground(matrices, left, top, panel);
                
                // Dibujar líneas de grid con efecto neón
                for (int x = 0; x < panel.getWidth(); x += 20) {
                    for (int y = 0; y < panel.getHeight(); y += 20) {
                        // Líneas horizontales
                        matrices.fill(left + x, top + y, left + x + 20, top + y + 1, 0x22FF00FF);
                        // Líneas verticales
                        matrices.fill(left + x, top + y, left + x + 1, top + y + 20, 0x2200FFFF);
                    }
                }
                
                // Borde neón alrededor del panel
                int borderWidth = 2;
                // Borde superior
                matrices.fill(left, top, left + panel.getWidth(), top + borderWidth, NEON_PINK);
                // Borde inferior
                matrices.fill(left, top + panel.getHeight() - borderWidth, left + panel.getWidth(), top + panel.getHeight(), NEON_BLUE);
                // Borde izquierdo
                matrices.fill(left, top + borderWidth, left + borderWidth, top + panel.getHeight() - borderWidth, NEON_PURPLE);
                // Borde derecho
                matrices.fill(left + panel.getWidth() - borderWidth, top + borderWidth, left + panel.getWidth(), top + panel.getHeight() - borderWidth, NEON_GREEN);
            });
            
            // Título principal con estilo neón
            WLabel titleLabel = new WLabel(Text.literal("NIGHTCLUB DEV TOOLS").styled(style -> style.withBold(true)), NEON_PINK);
            titleLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
            rootPanel.add(titleLabel, 0, 10, 380, 20);
            
            // Subtítulo con efecto de "glitch"
            WLabel subtitleLabel = new WLabel(Text.literal(">> SYSTEM ACCESS GRANTED <<"), NEON_BLUE);
            subtitleLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
            rootPanel.add(subtitleLabel, 0, 25, 380, 20);
            
            // Panel de contenido con scroll y borde neón
            WPlainPanel contentPanel = new WPlainPanel();
            contentPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                // Fondo semitransparente
                matrices.fill(left, top, left + panel.getWidth(), top + panel.getHeight(), 0x66000000);
                
                // Borde neón pulsante (cambia de color con el tiempo)
                int borderWidth = 1;
                int pulseColor = getPulsingColor();
                
                // Borde superior
                matrices.fill(left, top, left + panel.getWidth(), top + borderWidth, pulseColor);
                // Borde inferior
                matrices.fill(left, top + panel.getHeight() - borderWidth, left + panel.getWidth(), top + panel.getHeight(), pulseColor);
                // Borde izquierdo
                matrices.fill(left, top + borderWidth, left + borderWidth, top + panel.getHeight() - borderWidth, pulseColor);
                // Borde derecho
                matrices.fill(left + panel.getWidth() - borderWidth, top + borderWidth, left + panel.getWidth(), top + panel.getHeight() - borderWidth, pulseColor);
            });
            
            WScrollPanel scrollPanel = new WScrollPanel(contentPanel);
            scrollPanel.setBackgroundPainter(BackgroundPainter.VANILLA); // Fondo transparente para el scroll
            rootPanel.add(scrollPanel, 20, 50, 340, 140);
            
            // Configurar el panel de contenido
            int yPosition = 10;
            
            // ===== SECCIÓN: CREACIÓN RÁPIDA DE MUNDOS =====
            
            // Título de sección con estilo neón
            addSectionTitle(contentPanel, "WORLD CREATION", NEON_GREEN, yPosition);
            yPosition += 20;
            
            // Campo para la semilla con estilo cyberpunk
            seedField = new WTextField(Text.literal("Enter seed..."));
            seedField.setText("dev_test_seed");
            seedField.setMaxLength(32);
            contentPanel.add(seedField, 20, yPosition, 300, 20);
            yPosition += 30;
            
            // Botones con estilo neón
            WButton testWorldButton = createNeonButton("CREATE TEST WORLD", NEON_BLUE, () -> createTestWorld(false, seedField.getText()));
            contentPanel.add(testWorldButton, 20, yPosition, 300, 20);
            yPosition += 30;
            
            WButton creativeWorldButton = createNeonButton("CREATE CREATIVE WORLD", NEON_PINK, () -> createTestWorld(true, seedField.getText()));
            contentPanel.add(creativeWorldButton, 20, yPosition, 300, 20);
            yPosition += 40;
            
            // ===== SECCIÓN: MUNDOS EXISTENTES =====
            
            addSectionTitle(contentPanel, "EXISTING WORLDS", NEON_YELLOW, yPosition);
            yPosition += 20;
            
            WButton worldSelectionButton = createNeonButton("OPEN WORLD SELECTION", NEON_PURPLE, () -> MinecraftClient.getInstance().setScreen(new SelectWorldScreen(parent)));
            contentPanel.add(worldSelectionButton, 20, yPosition, 300, 20);
            yPosition += 40;
            
            // ===== SECCIÓN: PRUEBA DE CARACTERÍSTICAS =====
            
            addSectionTitle(contentPanel, "FEATURE TESTING", NEON_GREEN, yPosition);
            yPosition += 20;
            
            WButton testUIButton = createNeonButton("TEST UI COMPONENTS", NEON_BLUE, this::testUIComponents);
            contentPanel.add(testUIButton, 20, yPosition, 300, 20);
            yPosition += 30;
            
            WButton visualEffectsButton = createNeonButton("TEST VISUAL EFFECTS", NEON_PINK, () -> NightClubCraftMod.LOGGER.info("Visual effects test - Not implemented yet"));
            contentPanel.add(visualEffectsButton, 20, yPosition, 300, 20);
            yPosition += 30;
            
            WButton audioButton = createNeonButton("TEST AUDIO SYSTEM", NEON_PURPLE, () -> NightClubCraftMod.LOGGER.info("Audio system test - Not implemented yet"));
            contentPanel.add(audioButton, 20, yPosition, 300, 20);
            yPosition += 30;
            
            WButton npcButton = createNeonButton("TEST NPC SYSTEM", NEON_YELLOW, () -> NightClubCraftMod.LOGGER.info("NPC system test - Not implemented yet"));
            contentPanel.add(npcButton, 20, yPosition, 300, 20);
            yPosition += 40;
            
            // ===== BOTÓN DE REGRESO =====
            
            // Botón para volver a la pantalla de título con estilo neón
            WButton backButton = createNeonButton("EXIT SYSTEM", 0xFF3333, () -> MinecraftClient.getInstance().setScreen(parent));
            rootPanel.add(backButton, 20, 200, 340, 20);
            
            // Versión del mod con estilo terminal
            WLabel versionLabel = new WLabel(Text.literal("SYS: " + NightClubCraftMod.MOD_VERSION + " [DEV]"), NEON_GREEN);
            versionLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            rootPanel.add(versionLabel, 200, 225, 160, 10);
        }
        
        /**
         * Crea un botón con estilo neón cyberpunk.
         */
        private WButton createNeonButton(String text, int color, Runnable onClick) {
            // Crear un botón con texto en negrita y color personalizado
            WButton button = new WButton(Text.literal(text).styled(style -> 
                style.withBold(true).withColor(color)
            ));
            button.setOnClick(onClick);
            
            return button;
        }
        
        /**
         * Añade un título de sección con estilo cyberpunk.
         */
        private void addSectionTitle(WPlainPanel panel, String title, int color, int y) {
            WLabel titleLabel = new WLabel(Text.literal("[ " + title + " ]").styled(style -> style.withBold(true)), color);
            titleLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
            panel.add(titleLabel, 10, y, 320, 20);
            
            // Línea decorativa - usamos WPlainPanel en lugar de WPanel
            WPlainPanel line = new WPlainPanel();
            line.setBackgroundPainter((matrices, left, top, linePanel) -> {
                matrices.fill(left, top + 2, left + linePanel.getWidth(), top + 3, color);
            });
            panel.add(line, 10, y + 15, 320, 5);
        }
        
        /**
         * Obtiene un color pulsante que cambia con el tiempo.
         */
        private int getPulsingColor() {
            // Incrementar el contador de animación
            animationTick = (animationTick + 1) % 120;
            
            // Calcular el color basado en el tiempo
            if (animationTick < 40) {
                return NEON_PINK;
            } else if (animationTick < 80) {
                return NEON_BLUE;
            } else {
                return NEON_GREEN;
            }
        }
        
        /**
         * Crea un mundo de prueba.
         */
        private void createTestWorld(boolean creative, String seed) {
            // Simplificamos la creación de mundos para evitar problemas de compatibilidad
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Crear un nombre de mundo único
            String worldName = "Dev_" + (creative ? "Creative" : "Normal") + "_" + System.currentTimeMillis();
            
            // Abrir la pantalla de creación de mundo estándar
            // El usuario tendrá que configurar manualmente algunas opciones
            client.execute(() -> {
                NightClubCraftMod.LOGGER.info("Opening world creation screen for: " + worldName);
                NightClubCraftMod.LOGGER.info("Suggested seed: " + seed);
                NightClubCraftMod.LOGGER.info("Please configure the world manually with these settings");
                
                // Abrir la pantalla de creación de mundo estándar
                client.setScreen(new SelectWorldScreen(parent));
            });
        }
        
        /**
         * Prueba los componentes de UI.
         */
        private void testUIComponents() {
            // Implementar pruebas de componentes de UI
            NightClubCraftMod.LOGGER.info("UI components test - Not implemented yet");
        }
    }
    
    /**
     * Método tick para actualizar animaciones.
     */
    @Override
    public void tick() {
        super.tick();
        // Actualizar animaciones si es necesario
    }
} 