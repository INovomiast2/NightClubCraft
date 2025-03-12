package nightclubcraftmod.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.client.gui.screen.DevLoginScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Maneja la comunicación de red del lado del cliente.
 */
public class ClientNetworking {
    
    // Identificadores de canales
    public static final Identifier DEV_LOGIN_CHANNEL = new Identifier(NightClubCraftMod.MOD_ID, "dev_login");
    public static final Identifier DEV_LOGIN_RESPONSE_CHANNEL = new Identifier(NightClubCraftMod.MOD_ID, "dev_login_response");
    
    // Mapa para almacenar callbacks de respuestas pendientes
    private static final Map<UUID, Consumer<Boolean>> pendingLoginCallbacks = new HashMap<>();
    
    /**
     * Inicializa los receptores de paquetes del lado del cliente.
     */
    public static void init() {
        // Registrar receptor para respuestas de inicio de sesión de desarrollador
        ClientPlayNetworking.registerGlobalReceiver(DEV_LOGIN_RESPONSE_CHANNEL, (client, handler, buf, responseSender) -> {
            UUID requestId = buf.readUuid();
            boolean success = buf.readBoolean();
            
            // Ejecutar el callback correspondiente en el hilo del cliente
            client.execute(() -> {
                Consumer<Boolean> callback = pendingLoginCallbacks.remove(requestId);
                if (callback != null) {
                    callback.accept(success);
                }
            });
        });
        
        // Registrar receptor para abrir la pantalla de inicio de sesión de desarrollador
        ClientPlayNetworking.registerGlobalReceiver(ServerNetworking.OPEN_DEV_LOGIN_SCREEN, (client, handler, buf, responseSender) -> {
            // Abrir la pantalla de inicio de sesión en el hilo del cliente
            client.execute(() -> {
                MinecraftClient.getInstance().setScreen(new DevLoginScreen(MinecraftClient.getInstance().currentScreen));
            });
        });
        
        NightClubCraftMod.LOGGER.info("Client networking initialized");
    }
    
    /**
     * Envía una solicitud de inicio de sesión de desarrollador al servidor.
     * 
     * @param password La contraseña para autenticarse
     * @param callback El callback a ejecutar cuando se reciba la respuesta
     */
    public static void sendDevLoginRequest(String password, Consumer<Boolean> callback) {
        // Generar un ID único para esta solicitud
        UUID requestId = UUID.randomUUID();
        
        // Almacenar el callback para cuando llegue la respuesta
        pendingLoginCallbacks.put(requestId, callback);
        
        // Crear el paquete con el ID de solicitud y la contraseña
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(requestId);
        buf.writeString(password);
        
        // Enviar el paquete al servidor
        ClientPlayNetworking.send(DEV_LOGIN_CHANNEL, buf);
        
        NightClubCraftMod.LOGGER.info("Sent developer login request");
    }
} 