package com.grahambartley.volley.command;

import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.server.ServerConfigService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class VolleyCommand {
  public static final String ROOT = "volley";
  public static final String STATUS = "status";
  public static final String CONFIG = "config";
  public static final String RESET = "reset";
  public static final String HELP_HEADER_KEY = "command.volley.help.header";
  public static final String HELP_ENTRY_KEY = "command.volley.help.entry";
  public static final String RESET_VALUE = "defaults";

  private VolleyCommand() {}

  public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
        CommandManager.literal(ROOT)
            .executes(VolleyCommand::help)
            .then(CommandManager.literal(STATUS).executes(VolleyCommand::status))
            .then(
                CommandManager.literal(CONFIG)
                    .requires(VolleyCommand::isOperator)
                    .then(CommandManager.literal(RESET).executes(VolleyCommand::reset))
                    .then(ExplosiveCommandNodes.build())
                    .then(GrappleCommandNodes.build())
                    .then(UtilityCommandNodes.build())
                    .then(PhysicsCommandNodes.build())
                    .then(EnderCommandNodes.build())
                    .then(CombatCommandNodes.build())
                    .then(ControlCommandNodes.build())
                    .then(TraversalCommandNodes.build())
                    .then(TerrainCommandNodes.build())
                    .then(AgricultureCommandNodes.build())
                    .then(DiscoveryCommandNodes.build())
                    .then(ChaosCommandNodes.build())
                    .then(SocialCommandNodes.build())
                    .then(FletchingCommandNodes.build())
                    .then(SoundCommandNodes.build())));
  }

  public static boolean isOperator(final ServerCommandSource source) {
    return source.hasPermissionLevel(ServerConfigService.OP_PERMISSION_LEVEL);
  }

  private static int status(final CommandContext<ServerCommandSource> context) {
    final ServerCommandSource source = context.getSource();
    for (final Text line : ConfigStatusLines.lines(ServerConfigService.get())) {
      source.sendFeedback(() -> line, false);
    }
    return 1;
  }

  private static int help(final CommandContext<ServerCommandSource> context) {
    final ServerCommandSource source = context.getSource();
    source.sendFeedback(() -> Text.translatable(HELP_HEADER_KEY), false);
    for (final String line : helpLines(source)) {
      source.sendFeedback(() -> Text.translatable(HELP_ENTRY_KEY, line), false);
    }
    return 1;
  }

  public static List<String> helpLines(final ServerCommandSource source) {
    final CommandDispatcher<ServerCommandSource> dispatcher =
        source.getServer().getCommandManager().getDispatcher();
    final CommandNode<ServerCommandSource> root = dispatcher.getRoot().getChild(ROOT);
    return Arrays.stream(dispatcher.getAllUsage(root, source, true))
        .map(usage -> ROOT + " " + usage)
        .toList();
  }

  private static int reset(final CommandContext<ServerCommandSource> context) {
    return ConfigUpdates.apply(context, VolleyConfig.defaults(), ConfigSettings.ALL, RESET_VALUE);
  }
}
