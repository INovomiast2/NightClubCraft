package nightclubcraftmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.network.ServerNetworking;
import nightclubcraftmod.plot.Plot;
import nightclubcraftmod.plot.PlotManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Comando para funciones de desarrollador.
 * Permite a los desarrolladores autorizados realizar acciones administrativas.
 */
public class DevCommand {
    
    // Contraseña para autenticación de desarrolladores
    private static final String DEV_PASSWORD = "nightclub123";
    
    // Mapa para almacenar los desarrolladores autenticados
    private static final Map<UUID, Boolean> authenticatedDevs = new HashMap<>();
    
    /**
     * Registra el comando de desarrollador.
     *
     * @param dispatcher El dispatcher de comandos
     * @param registryAccess El acceso al registro de comandos
     * @param environment El entorno del comando
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("dev")
            .then(CommandManager.literal("login")
                .requires(source -> source.isExecutedByPlayer())
                .executes(DevCommand::showLoginScreen)
                .then(CommandManager.argument("password", StringArgumentType.word())
                    .executes(DevCommand::login)))
            .then(CommandManager.literal("logout")
                .requires(source -> source.isExecutedByPlayer())
                .executes(DevCommand::logout))
            .then(CommandManager.literal("plot")
                .requires(source -> source.isExecutedByPlayer() && isAuthenticated(source))
                .then(CommandManager.literal("list")
                    .executes(DevCommand::listPlots))
                .then(CommandManager.literal("delete")
                    .then(CommandManager.argument("plotId", StringArgumentType.word())
                        .executes(DevCommand::deletePlot))))
            .then(CommandManager.literal("status")
                .requires(source -> source.isExecutedByPlayer())
                .executes(DevCommand::status)));
    }
    
    /**
     * Verifica si un jugador está autenticado como desarrollador.
     *
     * @param source La fuente del comando
     * @return true si está autenticado, false en caso contrario
     */
    private static boolean isAuthenticated(ServerCommandSource source) {
        if (!source.isExecutedByPlayer()) {
            return false;
        }
        
        // Ya verificamos que es un jugador con isExecutedByPlayer(), así que no necesitamos try-catch
        UUID playerId = null;
        try {
            // Aunque sabemos que esto no debería lanzar una excepción, mantenemos el try-catch
            // por si acaso hay algún comportamiento inesperado
            playerId = source.getPlayer().getUuid();
        } catch (Exception e) {
            // Capturamos Exception genérica en lugar de CommandSyntaxException
            NightClubCraftMod.LOGGER.error("Error inesperado al obtener el jugador", e);
            return false;
        }
        
        return authenticatedDevs.getOrDefault(playerId, false);
    }
    
    /**
     * Autentica a un jugador como desarrollador usando la contraseña proporcionada.
     * Este método puede ser llamado desde la interfaz gráfica o desde la red.
     *
     * @param player El jugador a autenticar
     * @param password La contraseña proporcionada
     * @return true si la autenticación fue exitosa, false en caso contrario
     */
    public static boolean authenticatePlayer(ServerPlayerEntity player, String password) {
        if (DEV_PASSWORD.equals(password)) {
            authenticatedDevs.put(player.getUuid(), true);
            NightClubCraftMod.LOGGER.info("Jugador autenticado como desarrollador: " + player.getName().getString());
            return true;
        }
        return false;
    }
    
    /**
     * Muestra la pantalla de inicio de sesión para desarrolladores.
     * Este método se ejecuta cuando el jugador usa el comando /dev login sin argumentos.
     *
     * @param context El contexto del comando
     * @return El resultado del comando
     */
    private static int showLoginScreen(CommandContext<ServerCommandSource> context) {
        // Ya verificamos que es un jugador con requires(source -> source.isExecutedByPlayer())
        // así que no necesitamos try-catch
        ServerPlayerEntity player = null;
        try {
            player = context.getSource().getPlayer();
        } catch (Exception e) {
            // Usamos Exception genérica en lugar de CommandSyntaxException
            context.getSource().sendFeedback(() -> 
                Text.literal("Este comando solo puede ser ejecutado por un jugador.").formatted(Formatting.RED), 
                false
            );
            return 0;
        }
        
        // Enviar un paquete al cliente para abrir la pantalla de inicio de sesión
        ServerNetworking.sendOpenDevLoginScreen(player);
        
        // Enviar mensaje de feedback
        context.getSource().sendFeedback(() -> 
            Text.literal("Abriendo pantalla de inicio de sesión...").formatted(Formatting.YELLOW), 
            false
        );
        
        return 1;
    }
    
    /**
     * Maneja el comando de login.
     *
     * @param context El contexto del comando
     * @return El resultado del comando
     * @throws CommandSyntaxException Si hay un error de sintaxis
     */
    private static int login(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String password = StringArgumentType.getString(context, "password");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (authenticatePlayer(player, password)) {
            source.sendFeedback(() -> Text.literal("¡Autenticado como desarrollador!").formatted(Formatting.GREEN), false);
            return 1;
        } else {
            source.sendFeedback(() -> Text.literal("Contraseña incorrecta.").formatted(Formatting.RED), false);
            return 0;
        }
    }
    
    /**
     * Maneja el comando de logout.
     *
     * @param context El contexto del comando
     * @return El resultado del comando
     * @throws CommandSyntaxException Si hay un error de sintaxis
     */
    private static int logout(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        UUID playerId = source.getPlayer().getUuid();
        
        authenticatedDevs.remove(playerId);
        source.sendFeedback(() -> Text.literal("Has cerrado sesión como desarrollador.").formatted(Formatting.YELLOW), false);
        return 1;
    }
    
    /**
     * Maneja el comando de estado.
     *
     * @param context El contexto del comando
     * @return El resultado del comando
     * @throws CommandSyntaxException Si hay un error de sintaxis
     */
    private static int status(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        UUID playerId = source.getPlayer().getUuid();
        
        boolean isAuthenticated = authenticatedDevs.getOrDefault(playerId, false);
        if (isAuthenticated) {
            source.sendFeedback(() -> Text.literal("Estado: Autenticado como desarrollador").formatted(Formatting.GREEN), false);
        } else {
            source.sendFeedback(() -> Text.literal("Estado: No autenticado como desarrollador").formatted(Formatting.RED), false);
        }
        
        return 1;
    }
    
    /**
     * Maneja el comando para listar plots.
     *
     * @param context El contexto del comando
     * @return El resultado del comando
     */
    private static int listPlots(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Collection<Plot> allPlots = PlotManager.getInstance().getAllPlots();
        
        source.sendFeedback(() -> Text.literal("=== Plots disponibles (" + allPlots.size() + ") ===").formatted(Formatting.GOLD), false);
        
        for (Plot plot : allPlots) {
            String plotInfo = "ID: " + plot.getId().toString().substring(0, 8) + "... | Dueño: " + 
                              (plot.getOwnerUuid() != null ? plot.getOwnerUuid().toString().substring(0, 8) + "..." : "Ninguno");
            source.sendFeedback(() -> Text.literal(plotInfo).formatted(Formatting.YELLOW), false);
        }
        
        return 1;
    }
    
    /**
     * Maneja el comando para eliminar un plot.
     *
     * @param context El contexto del comando
     * @return El resultado del comando
     */
    private static int deletePlot(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String plotIdStr = StringArgumentType.getString(context, "plotId");
        
        try {
            // Intentar encontrar el plot por el prefijo del ID
            Collection<Plot> allPlots = PlotManager.getInstance().getAllPlots();
            Plot targetPlot = null;
            
            for (Plot plot : allPlots) {
                String shortId = plot.getId().toString().substring(0, 8);
                if (shortId.startsWith(plotIdStr)) {
                    targetPlot = plot;
                    break;
                }
            }
            
            if (targetPlot != null) {
                // Eliminar el plot
                PlotManager.getInstance().deletePlot(targetPlot.getId());
                source.sendFeedback(() -> Text.literal("¡Plot eliminado con éxito!").formatted(Formatting.GREEN), true);
                NightClubCraftMod.LOGGER.info("Plot eliminado por desarrollador: " + targetPlot.getId());
                return 1;
            } else {
                source.sendFeedback(() -> Text.literal("No se encontró ningún plot con ese ID.").formatted(Formatting.RED), false);
                return 0;
            }
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("Error al eliminar el plot: " + e.getMessage()).formatted(Formatting.RED), false);
            NightClubCraftMod.LOGGER.error("Error al eliminar plot", e);
            return 0;
        }
    }
} 