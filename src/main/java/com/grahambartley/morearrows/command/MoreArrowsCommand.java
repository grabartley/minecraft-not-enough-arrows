package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class MoreArrowsCommand {
  public static final String ROOT = "morearrows";
  public static final String STATUS = "status";
  public static final String CONFIG = "config";
  public static final String RESET = "reset";
  public static final String HELP_HEADER_KEY = "command.more-arrows.help.header";
  public static final String HELP_ENTRY_KEY = "command.more-arrows.help.entry";
  public static final String RESET_VALUE = "defaults";

  private MoreArrowsCommand() {}

  public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
        CommandManager.literal(ROOT)
            .executes(MoreArrowsCommand::help)
            .then(CommandManager.literal(STATUS).executes(MoreArrowsCommand::status))
            .then(
                CommandManager.literal(CONFIG)
                    .requires(MoreArrowsCommand::isOperator)
                    .then(CommandManager.literal(RESET).executes(MoreArrowsCommand::reset))
                    .then(ExplosiveCommandNodes.build())
                    .then(GrappleCommandNodes.build())
                    .then(UtilityCommandNodes.build())
                    .then(PhysicsCommandNodes.build())
                    .then(EnderCommandNodes.build())
                    .then(FletchingCommandNodes.build())));
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

    final CommandDispatcher<ServerCommandSource> dispatcher =
        source.getServer().getCommandManager().getDispatcher();
    final CommandNode<ServerCommandSource> root = dispatcher.getRoot().getChild(ROOT);
    for (final String usage : dispatcher.getAllUsage(root, source, false)) {
      final String line = ROOT + " " + usage;
      source.sendFeedback(() -> Text.translatable(HELP_ENTRY_KEY, line), false);
    }
    return 1;
  }

  private static int reset(final CommandContext<ServerCommandSource> context) {
    return ConfigUpdates.apply(
        context, MoreArrowsConfig.defaults(), ConfigSettings.ALL, RESET_VALUE);
  }
}
