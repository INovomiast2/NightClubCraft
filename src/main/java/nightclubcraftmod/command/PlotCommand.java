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
import net.minecraft.util.math.BlockPos;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.item.DevelopmentStickItem;
import nightclubcraftmod.plot.Plot;
import nightclubcraftmod.plot.PlotManager;
import nightclubcraftmod.plot.PlotSelection;

import java.util.List;
import java.util.UUID;

/**
 * Comando para gestionar plots.
 */
public class PlotCommand {
    
    /**
     * Registra el comando en el dispatcher.
     *
     * @param dispatcher El dispatcher de comandos
     * @param registryAccess El acceso al registro de comandos
     * @param environment El entorno de comandos
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("plot")
            // Subcomando: /plot create
            .then(CommandManager.literal("create")
                .executes(PlotCommand::executeCreate))
            
            // Subcomando: /plot list
            .then(CommandManager.literal("list")
                .executes(PlotCommand::executeList))
            
            // Subcomando: /plot info
            .then(CommandManager.literal("info")
                .executes(PlotCommand::executeInfo))
            
            // Subcomando: /plot borders
            .then(CommandManager.literal("borders")
                .then(CommandManager.literal("show")
                    .executes(PlotCommand::executeShowBorders))
                .then(CommandManager.literal("hide")
                    .executes(PlotCommand::executeHideBorders)))
            
            // Subcomando: /plot clear
            .then(CommandManager.literal("clear")
                .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de operador
                .then(CommandManager.literal("all")
                    .executes(context -> executeClearAll(context, false))
                    .then(CommandManager.argument("confirm", StringArgumentType.word())
                        .executes(context -> {
                            String confirmStr = StringArgumentType.getString(context, "confirm");
                            boolean confirm = "true".equalsIgnoreCase(confirmStr) || "yes".equalsIgnoreCase(confirmStr);
                            return executeClearAll(context, confirm);
                        }))))
            
            // Comando base: /plot
            .executes(PlotCommand::executeHelp)
        );
    }
    
    /**
     * Ejecuta el comando /plot create.
     * Crea un nuevo plot en la selección actual del jugador.
     */
    private static int executeCreate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        // Obtener la selección actual del jugador desde el Development Stick
        PlotSelection selection = null;
        if (player.getMainHandStack().getItem() instanceof DevelopmentStickItem) {
            selection = DevelopmentStickItem.getCurrentSelection(player.getMainHandStack(), player);
        } else if (player.getOffHandStack().getItem() instanceof DevelopmentStickItem) {
            selection = DevelopmentStickItem.getCurrentSelection(player.getOffHandStack(), player);
        }
        
        if (selection == null) {
            context.getSource().sendFeedback(() -> Text.literal("Primero debes seleccionar dos esquinas con el Development Stick.").formatted(Formatting.RED), false);
            return 0;
        }
        
        // Crear el plot
        boolean success = PlotManager.getInstance().createPlot(selection, player);
        
        if (success) {
            context.getSource().sendFeedback(() -> Text.literal("¡Plot creado con éxito!").formatted(Formatting.GREEN), true);
            
            // Limpiar la selección
            if (player.getMainHandStack().getItem() instanceof DevelopmentStickItem) {
                player.getMainHandStack().removeSubNbt("firstPosX");
                player.getMainHandStack().removeSubNbt("firstPosY");
                player.getMainHandStack().removeSubNbt("firstPosZ");
                player.getMainHandStack().removeSubNbt("heightAdjustment");
            } else if (player.getOffHandStack().getItem() instanceof DevelopmentStickItem) {
                player.getOffHandStack().removeSubNbt("firstPosX");
                player.getOffHandStack().removeSubNbt("firstPosY");
                player.getOffHandStack().removeSubNbt("firstPosZ");
                player.getOffHandStack().removeSubNbt("heightAdjustment");
            }
        } else {
            context.getSource().sendFeedback(() -> Text.literal("No se pudo crear el plot. Puede que se superponga con otro existente.").formatted(Formatting.RED), false);
        }
        
        return success ? 1 : 0;
    }
    
    /**
     * Ejecuta el comando /plot list.
     * Muestra una lista de los plots del jugador.
     */
    private static int executeList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        List<Plot> playerPlots = PlotManager.getInstance().getPlayerPlots(player.getUuid());
        
        if (playerPlots.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("No tienes ningún plot.").formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        context.getSource().sendFeedback(() -> Text.literal("=== Tus Plots ===").formatted(Formatting.GOLD), false);
        
        for (Plot plot : playerPlots) {
            BlockPos center = plot.getSelection().getCenter();
            context.getSource().sendFeedback(() -> Text.literal("ID: " + plot.getId() + " | Posición: " + center.getX() + ", " + center.getY() + ", " + center.getZ()).formatted(Formatting.YELLOW), false);
        }
        
        return 1;
    }
    
    /**
     * Ejecuta el comando /plot info.
     * Muestra información sobre el plot en el que está el jugador.
     */
    private static int executeInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        BlockPos playerPos = player.getBlockPos();
        
        // Buscar el plot en la posición del jugador
        final Plot[] foundPlot = {null}; // Usar un array para que sea efectivamente final
        
        for (Plot p : PlotManager.getInstance().getAllPlots()) {
            if (p.contains(playerPos)) {
                foundPlot[0] = p;
                break;
            }
        }
        
        if (foundPlot[0] == null) {
            context.getSource().sendFeedback(() -> Text.literal("No estás en ningún plot.").formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        // Mostrar información del plot
        context.getSource().sendFeedback(() -> Text.literal("=== Información del Plot ===").formatted(Formatting.GOLD), false);
        context.getSource().sendFeedback(() -> Text.literal("ID: " + foundPlot[0].getId()).formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("Propietario: " + foundPlot[0].getOwnerName()).formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("Tipo: " + foundPlot[0].getType()).formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("Estado: " + foundPlot[0].getStatus()).formatted(Formatting.YELLOW), false);
        
        BlockPos min = foundPlot[0].getSelection().getMinPos();
        BlockPos max = foundPlot[0].getSelection().getMaxPos();
        context.getSource().sendFeedback(() -> Text.literal("Tamaño: " + (max.getX() - min.getX() + 1) + "x" + (max.getY() - min.getY() + 1) + "x" + (max.getZ() - min.getZ() + 1)).formatted(Formatting.YELLOW), false);
        
        return 1;
    }
    
    /**
     * Ejecuta el comando /plot borders show.
     * Muestra los bordes de los plots del jugador.
     */
    private static int executeShowBorders(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        List<Plot> playerPlots = PlotManager.getInstance().getPlayerPlots(player.getUuid());
        
        if (playerPlots.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("No tienes ningún plot.").formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        final int[] count = {0}; // Usar un array para que sea efectivamente final
        
        for (Plot plot : playerPlots) {
            if (!plot.isOutlineVisible()) {
                plot.setOutlineVisible(true);
                count[0]++;
            }
        }
        
        if (count[0] > 0) {
            context.getSource().sendFeedback(() -> Text.literal("Se han mostrado los bordes de " + count[0] + " plots.").formatted(Formatting.GREEN), false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Los bordes de tus plots ya estaban visibles.").formatted(Formatting.YELLOW), false);
        }
        
        return 1;
    }
    
    /**
     * Ejecuta el comando /plot borders hide.
     * Oculta los bordes de los plots del jugador.
     */
    private static int executeHideBorders(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        List<Plot> playerPlots = PlotManager.getInstance().getPlayerPlots(player.getUuid());
        
        if (playerPlots.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("No tienes ningún plot.").formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        final int[] count = {0}; // Usar un array para que sea efectivamente final
        
        for (Plot plot : playerPlots) {
            if (plot.isOutlineVisible()) {
                plot.setOutlineVisible(false);
                count[0]++;
            }
        }
        
        if (count[0] > 0) {
            context.getSource().sendFeedback(() -> Text.literal("Se han ocultado los bordes de " + count[0] + " plots.").formatted(Formatting.GREEN), false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Los bordes de tus plots ya estaban ocultos.").formatted(Formatting.YELLOW), false);
        }
        
        return 1;
    }
    
    /**
     * Ejecuta el comando /plot clear all.
     * Elimina todos los plots del mundo.
     * 
     * @param context El contexto del comando
     * @param confirm Si se ha confirmado la acción
     * @return El resultado del comando
     */
    private static int executeClearAll(CommandContext<ServerCommandSource> context, boolean confirm) {
        ServerCommandSource source = context.getSource();
        
        if (!confirm) {
            source.sendFeedback(() -> Text.literal("¡ADVERTENCIA! Esta acción eliminará TODOS los plots del mundo y no se puede deshacer.")
                .formatted(Formatting.RED, Formatting.BOLD), false);
            source.sendFeedback(() -> Text.literal("Para confirmar, ejecuta: /plot clear all true")
                .formatted(Formatting.YELLOW), false);
            return 0;
        }
        
        // Eliminar todos los plots
        int count = PlotManager.getInstance().clearAllPlots();
        
        if (count > 0) {
            source.sendFeedback(() -> Text.literal("Se han eliminado " + count + " plots.")
                .formatted(Formatting.GREEN), true);
            NightClubCraftMod.LOGGER.info("Administrador " + source.getName() + " ha eliminado todos los plots (" + count + ")");
        } else {
            source.sendFeedback(() -> Text.literal("No había plots para eliminar.")
                .formatted(Formatting.YELLOW), false);
        }
        
        return count;
    }
    
    /**
     * Ejecuta el comando /plot.
     * Muestra la ayuda del comando.
     */
    private static int executeHelp(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("=== Comandos de Plot ===").formatted(Formatting.GOLD), false);
        context.getSource().sendFeedback(() -> Text.literal("/plot create - Crea un nuevo plot en la selección actual").formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("/plot list - Muestra una lista de tus plots").formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("/plot info - Muestra información sobre el plot en el que estás").formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("/plot borders show - Muestra los bordes de tus plots").formatted(Formatting.YELLOW), false);
        context.getSource().sendFeedback(() -> Text.literal("/plot borders hide - Oculta los bordes de tus plots").formatted(Formatting.YELLOW), false);
        
        // Mostrar comandos de administrador si el jugador tiene permisos
        if (context.getSource().hasPermissionLevel(2)) {
            context.getSource().sendFeedback(() -> Text.literal("=== Comandos de Administrador ===").formatted(Formatting.RED), false);
            context.getSource().sendFeedback(() -> Text.literal("/plot clear all - Elimina todos los plots del mundo").formatted(Formatting.RED), false);
        }
        
        return 1;
    }
} 