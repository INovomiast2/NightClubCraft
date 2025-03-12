package nightclubcraftmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import nightclubcraftmod.item.DevelopmentStickItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin para capturar el evento de scroll del ratón.
 */
@Mixin(Mouse.class)
public class MouseScrollMixin {
    
    @Shadow @Final private MinecraftClient client;
    
    /**
     * Captura el evento de scroll del ratón.
     */
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (client.player != null && vertical != 0) {
            // Intentar manejar el scroll con el Development Stick
            if (DevelopmentStickItem.handleScroll(client.player, vertical)) {
                // Si se manejó el scroll, cancelar el evento original
                ci.cancel();
            }
        }
    }
} 