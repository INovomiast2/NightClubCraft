package nightclubcraftmod.client.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.client.gui.widget.CustomButtonWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla para cargar un juego existente con slots seleccionables.
 */
public class LoadGameScreen extends Screen {
    
    private static final int SLOT_WIDTH = 400;
    private static final int SLOT_HEIGHT = 40;
    private static final int SLOT_SPACING = 8;
    private static final int VISIBLE_SLOTS = 3; // Número de slots visibles a la vez
    private static final int TOTAL_SLOTS = 5; // Número total de slots
    
    private final Screen parent;
    private int selectedSlot = -1;
    private ButtonWidget loadButton;
    
    // Variables para el scroll
    private int scrollOffset = 0;
    private boolean isDragging = false;
    private int lastMouseY;
    private int scrollbarPosition;
    private int scrollbarHeight;
    private int contentHeight;
    private int viewportHeight;
    
    // Lista de slots
    private final List<SlotInfo> slots = new ArrayList<>();
    
    public LoadGameScreen(Screen parent) {
        super(Text.literal("Load Game"));
        this.parent = parent;
        
        // Inicializar slots
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            slots.add(new SlotInfo(i, "No saved game"));
        }
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        
        // Calcular dimensiones del área de scroll
        viewportHeight = VISIBLE_SLOTS * (SLOT_HEIGHT + SLOT_SPACING) - SLOT_SPACING;
        contentHeight = TOTAL_SLOTS * (SLOT_HEIGHT + SLOT_SPACING) - SLOT_SPACING;
        
        // Calcular dimensiones de la scrollbar
        scrollbarHeight = Math.max(20, viewportHeight * viewportHeight / contentHeight);
        
        // Añadir botones de acción con la nueva clase CustomButtonWidget
        loadButton = this.addDrawableChild(new CustomButtonWidget(
            centerX - 210, this.height - 30, 200, 20,
            Text.literal("Load Game"),
            button -> {
                if (selectedSlot >= 0) {
                    // Aquí iría la lógica para cargar un juego existente
                    NightClubCraftMod.LOGGER.info("Cargando juego desde el slot {}", selectedSlot + 1);
                    this.client.setScreen(null); // Volver al juego
                }
            }
        ));
        
        loadButton.active = false; // Deshabilitado hasta que se seleccione un slot
        
        this.addDrawableChild(new CustomButtonWidget(
            centerX + 10, this.height - 30, 200, 20,
            Text.literal("Cancel"),
            button -> this.close()
        ));
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Renderizar fondo
        this.renderBackground(context);
        
        // Renderizar título
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        
        // Renderizar subtítulo
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Select a Saved Game").styled(style -> style.withBold(true)), this.width / 2, 30, 0xCCCCCC);
        
        // Calcular área de slots
        int centerX = this.width / 2;
        int startY = 50;
        int endY = startY + viewportHeight;
        
        // Dibujar fondo del área de slots
        context.fill(centerX - SLOT_WIDTH / 2 - 10, startY - 5, 
                    centerX + SLOT_WIDTH / 2 + 10, endY + 5, 0x80000000);
        
        // Aplicar scissor para limitar el área de renderizado
        context.enableScissor(
            centerX - SLOT_WIDTH / 2 - 5, 
            startY - 2, 
            centerX + SLOT_WIDTH / 2 + 5, 
            endY + 2
        );
        
        // Renderizar slots
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int slotY = startY + i * (SLOT_HEIGHT + SLOT_SPACING) - scrollOffset;
            
            // Solo renderizar slots visibles
            if (slotY + SLOT_HEIGHT >= startY && slotY <= endY) {
                renderSlot(context, i, centerX, slotY);
            }
        }
        
        // Desactivar scissor
        context.disableScissor();
        
        // Renderizar scrollbar
        if (contentHeight > viewportHeight) {
            scrollbarPosition = startY + (int)((float)scrollOffset / (contentHeight - viewportHeight) * (viewportHeight - scrollbarHeight));
            
            // Fondo de la scrollbar
            context.fill(
                centerX + SLOT_WIDTH / 2 + 15, 
                startY, 
                centerX + SLOT_WIDTH / 2 + 25, 
                endY, 
                0x80000000
            );
            
            // Scrollbar
            context.fill(
                centerX + SLOT_WIDTH / 2 + 15, 
                scrollbarPosition, 
                centerX + SLOT_WIDTH / 2 + 25, 
                scrollbarPosition + scrollbarHeight, 
                0xFFCCCCCC
            );
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderSlot(DrawContext context, int index, int centerX, int slotY) {
        SlotInfo slot = slots.get(index);
        boolean isSelected = selectedSlot == index;
        
        // Fondo del slot
        int backgroundColor = isSelected ? 0xFF666666 : 0xFF444444;
        context.fill(centerX - SLOT_WIDTH / 2, slotY, centerX + SLOT_WIDTH / 2, slotY + SLOT_HEIGHT, backgroundColor);
        
        // Borde del slot
        int borderColor = isSelected ? 0xFFFFFF00 : 0xFF888888;
        context.drawBorder(
            centerX - SLOT_WIDTH / 2, 
            slotY, 
            SLOT_WIDTH, 
            SLOT_HEIGHT, 
            borderColor
        );
        
        // Texto del slot - Ahora en una sola línea para slots más pequeños
        String slotText = "Slot " + (index + 1) + " - " + slot.status;
        context.drawTextWithShadow(this.textRenderer, slotText, 
            centerX - SLOT_WIDTH / 2 + 10, slotY + (SLOT_HEIGHT - 8) / 2, 
            isSelected ? 0xFFFF00 : 0xFFFFFF);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int startY = 50;
        int endY = startY + viewportHeight;
        
        // Comprobar si se ha hecho clic en la scrollbar
        if (mouseX >= centerX + SLOT_WIDTH / 2 + 15 && mouseX <= centerX + SLOT_WIDTH / 2 + 25 &&
            mouseY >= startY && mouseY <= endY) {
            isDragging = true;
            lastMouseY = (int) mouseY;
            return true;
        }
        
        // Comprobar si se ha hecho clic en un slot
        if (mouseX >= centerX - SLOT_WIDTH / 2 && mouseX <= centerX + SLOT_WIDTH / 2 &&
            mouseY >= startY && mouseY <= endY) {
            
            // Calcular qué slot se ha seleccionado
            int clickedSlotIndex = (int) ((mouseY - startY + scrollOffset) / (SLOT_HEIGHT + SLOT_SPACING));
            
            if (clickedSlotIndex >= 0 && clickedSlotIndex < TOTAL_SLOTS) {
                selectedSlot = clickedSlotIndex;
                loadButton.active = true;
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            int diff = (int) mouseY - lastMouseY;
            lastMouseY = (int) mouseY;
            
            // Calcular nuevo offset de scroll
            float scrollFactor = (float) (contentHeight - viewportHeight) / (viewportHeight - scrollbarHeight);
            scrollOffset = MathHelper.clamp(scrollOffset + (int) (diff * scrollFactor), 0, contentHeight - viewportHeight);
            
            return true;
        }
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // Ajustar el scroll con la rueda del ratón
        scrollOffset = MathHelper.clamp(
            scrollOffset - (int) (amount * 20),
            0,
            Math.max(0, contentHeight - viewportHeight)
        );
        
        return true;
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    /**
     * Clase para almacenar información de un slot.
     */
    private static class SlotInfo {
        final int index;
        final String status;
        
        SlotInfo(int index, String status) {
            this.index = index;
            this.status = status;
        }
    }
} 