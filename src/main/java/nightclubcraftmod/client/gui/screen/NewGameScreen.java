package nightclubcraftmod.client.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import nightclubcraftmod.NightClubCraftMod;

/**
 * Pantalla de configuración para crear un nuevo juego de NightClubCraft.
 * Permite al jugador configurar aspectos básicos como el nombre de la empresa y la dificultad.
 */
public class NewGameScreen extends Screen {
    
    private final Screen parent;
    private int selectedSlot = -1;
    
    // Widgets para la configuración
    private TextFieldWidget businessNameField;
    private ButtonWidget difficultyButton;
    private ButtonWidget createButton;
    private ButtonWidget cancelButton;
    
    // Valores de configuración
    private String businessName = "My NightClub";
    private Difficulty difficulty = Difficulty.NORMAL;
    
    // Constantes para la UI
    private static final int FIELD_WIDTH = 300;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 24;
    
    public NewGameScreen(Screen parent, int selectedSlot) {
        super(Text.literal("Create New Business"));
        this.parent = parent;
        this.selectedSlot = selectedSlot;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 80;
        
        // Campo para el nombre del negocio
        this.businessNameField = new TextFieldWidget(
            this.textRenderer, 
            centerX - FIELD_WIDTH / 2, 
            startY, 
            FIELD_WIDTH, 
            FIELD_HEIGHT, 
            Text.literal("Business Name")
        );
        this.businessNameField.setMaxLength(32);
        this.businessNameField.setText(businessName);
        this.businessNameField.setChangedListener(this::onBusinessNameChanged);
        this.addDrawableChild(this.businessNameField);
        
        startY += SPACING + 10;
        
        // Botón para cambiar la dificultad
        this.difficultyButton = this.addDrawableChild(ButtonWidget.builder(
            getDifficultyText(),
            button -> cycleDifficulty()
        ).dimensions(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        
        startY += SPACING * 2;
        
        // Botón para crear el mundo
        this.createButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Create Business"),
            button -> createWorld()
        ).dimensions(centerX - FIELD_WIDTH / 2, startY, FIELD_WIDTH / 2 - 5, BUTTON_HEIGHT).build());
        
        // Botón para cancelar
        this.cancelButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Cancel"),
            button -> this.close()
        ).dimensions(centerX + 5, startY, FIELD_WIDTH / 2 - 5, BUTTON_HEIGHT).build());
        
        // Establecer el foco inicial en el campo de nombre
        this.setInitialFocus(this.businessNameField);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Renderizar fondo
        this.renderBackground(context);
        
        // Renderizar título
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Renderizar subtítulo
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            Text.literal("Configure your new nightclub business"),
            this.width / 2, 
            40, 
            0xCCCCCC
        );
        
        // Renderizar etiquetas
        int centerX = this.width / 2;
        int labelY = 65;
        
        context.drawTextWithShadow(
            this.textRenderer, 
            Text.literal("Business Name:"), 
            centerX - FIELD_WIDTH / 2, 
            labelY, 
            0xFFFFFF
        );
        
        labelY = 115;
        context.drawTextWithShadow(
            this.textRenderer, 
            Text.literal("Game Difficulty:"), 
            centerX - FIELD_WIDTH / 2, 
            labelY, 
            0xFFFFFF
        );
        
        // Renderizar información del slot
        String slotInfo = "Creating business in Slot " + (selectedSlot + 1);
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            slotInfo, 
            this.width / 2, 
            this.height - 30, 
            0xAAAAAA
        );
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    /**
     * Actualiza el nombre del negocio cuando cambia el campo de texto.
     */
    private void onBusinessNameChanged(String newName) {
        this.businessName = newName;
        updateCreateButtonState();
    }
    
    /**
     * Cambia cíclicamente entre las dificultades disponibles.
     */
    private void cycleDifficulty() {
        switch (difficulty) {
            case PEACEFUL:
                difficulty = Difficulty.EASY;
                break;
            case EASY:
                difficulty = Difficulty.NORMAL;
                break;
            case NORMAL:
                difficulty = Difficulty.HARD;
                break;
            case HARD:
                difficulty = Difficulty.PEACEFUL;
                break;
        }
        
        if (difficultyButton != null) {
            difficultyButton.setMessage(getDifficultyText());
        }
    }
    
    /**
     * Obtiene el texto para el botón de dificultad basado en la dificultad actual.
     */
    private Text getDifficultyText() {
        String difficultyName;
        int color;
        
        switch (difficulty) {
            case PEACEFUL:
                difficultyName = "Peaceful";
                color = 0x55FF55; // Verde
                break;
            case EASY:
                difficultyName = "Easy";
                color = 0x55FFFF; // Cian
                break;
            case NORMAL:
                difficultyName = "Normal";
                color = 0xFFFF55; // Amarillo
                break;
            case HARD:
                difficultyName = "Hard";
                color = 0xFF5555; // Rojo
                break;
            default:
                difficultyName = "Normal";
                color = 0xFFFF55;
        }
        
        return Text.literal("Difficulty: " + difficultyName).styled(style -> style.withColor(color));
    }
    
    /**
     * Actualiza el estado del botón de creación basado en la validez de los datos.
     */
    private void updateCreateButtonState() {
        if (createButton != null) {
            createButton.active = !businessName.trim().isEmpty();
        }
    }
    
    /**
     * Crea el mundo con la configuración especificada.
     */
    private void createWorld() {
        NightClubCraftMod.LOGGER.info("Creating new business: {} in slot {} with difficulty {}", 
            businessName, selectedSlot + 1, difficulty.getName());
        
        // Aquí iría la lógica para crear el mundo con la configuración especificada
        // Por ahora, simplemente volvemos al juego
        this.client.setScreen(null);
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
} 