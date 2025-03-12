package nightclubcraftmod.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.command.DevCommand;

import java.util.UUID;

/**
 * Maneja la comunicación de red del lado del servidor.
 */
public class ServerNetworking {
    
    // Identificadores de canales
    public static final Identifier OPEN_DEV_LOGIN_SCREEN = new Identifier(NightClubCraftMod.MOD_ID, "open_dev_login_screen");
    
    /**
     * Inicializa los receptores de paquetes del lado del servidor.
     */
    public static void init() {
        // Registrar receptor para solicitudes de inicio de sesión de desarrollador
        ServerPlayNetworking.registerGlobalReceiver(
            ClientNetworking.DEV_LOGIN_CHANNEL,
            ServerNetworking::handleDevLoginRequest
        );
        
        NightClubCraftMod.LOGGER.info("Server networking initialized");
    }
    
    /**
     * Envía un paquete al cliente para abrir la pantalla de inicio de sesión de desarrollador.
     * 
     * @param player El jugador al que enviar el paquete
     */
    public static void sendOpenDevLoginScreen(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, OPEN_DEV_LOGIN_SCREEN, buf);
        NightClubCraftMod.LOGGER.info("Sent open dev login screen packet to {}", player.getName().getString());
    }
    
    /**
     * Maneja una solicitud de inicio de sesión de desarrollador.
     * 
     * @param server El servidor de Minecraft
     * @param player El jugador que envió la solicitud
     * @param handler El manejador de red del jugador
     * @param buf El buffer con los datos del paquete
     * @param responseSender El remitente de respuestas
     */
    private static void handleDevLoginRequest(
            MinecraftServer server,
            ServerPlayerEntity player,
            ServerPlayNetworkHandler handler,
            PacketByteBuf buf,
            PacketSender responseSender) {
        
        // Leer datos del paquete
        UUID requestId = buf.readUuid();
        String password = buf.readString();
        
        // Verificar la contraseña
        boolean success = DevCommand.authenticatePlayer(player, password);
        
        // Crear paquete de respuesta
        PacketByteBuf responseBuf = PacketByteBufs.create();
        responseBuf.writeUuid(requestId);
        responseBuf.writeBoolean(success);
        
        // Enviar respuesta al cliente
        ServerPlayNetworking.send(player, ClientNetworking.DEV_LOGIN_RESPONSE_CHANNEL, responseBuf);
        
        // Registrar el intento de inicio de sesión
        if (success) {
            NightClubCraftMod.LOGGER.info("Player {} authenticated as developer", player.getName().getString());
        } else {
            NightClubCraftMod.LOGGER.warn("Failed developer authentication attempt by {}", player.getName().getString());
        }
    }
} 