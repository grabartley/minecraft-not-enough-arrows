package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class NotEnoughArrowsCommand {
  public static final String ROOT = "notenougharrows";
  public static final String ALIAS = "nea";
  public static final String STATUS = "status";
  public static final String CONFIG = "config";
  public static final String RESET = "reset";
  public static final String HELP_HEADER_KEY = "command.not-enough-arrows.help.header";
  public static final String HELP_ENTRY_KEY = "command.not-enough-arrows.help.entry";
  public static final String RESET_VALUE = "defaults";

  private NotEnoughArrowsCommand() {}

  public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
    final LiteralCommandNode<ServerCommandSource> root =
        dispatcher.register(
            CommandManager.literal(ROOT)
                .executes(NotEnoughArrowsCommand::help)
                .then(CommandManager.literal(STATUS).executes(NotEnoughArrowsCommand::status))
                .then(
                    CommandManager.literal(CONFIG)
                        .requires(NotEnoughArrowsCommand::isOperator)
                        .then(CommandManager.literal(RESET).executes(NotEnoughArrowsCommand::reset))
                        .then(ExplosiveCommandNodes.build())
                        .then(GrappleCommandNodes.build())
                        .then(UtilityCommandNodes.build())
                        .then(PhysicsCommandNodes.build())
                        .then(EnderCommandNodes.build())
                        .then(CombatCommandNodes.build())
                        .then(ControlCommandNodes.build())
                        .then(FletchingCommandNodes.build())));

    dispatcher.register(
        CommandManager.literal(ALIAS).redirect(root).executes(NotEnoughArrowsCommand::help));
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
        context, NotEnoughArrowsConfig.defaults(), ConfigSettings.ALL, RESET_VALUE);
  }
}
