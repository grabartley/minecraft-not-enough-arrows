package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigValueFormat;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.option.IdentifierListOption;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class IdentifierListNodes {
  public static final String ID_ARGUMENT = "id";
  public static final String ADD = "add";
  public static final String REMOVE = "remove";
  public static final String CLEAR = "clear";

  private IdentifierListNodes() {}

  @FunctionalInterface
  private interface Edit {
    IdentifierListEdits.Result apply(List<String> current, String id);
  }

  public static LiteralArgumentBuilder<ServerCommandSource> build(
      final IdentifierListOption<NotEnoughArrowsConfig> option) {
    return ConfigOptionNodes.group(option.id())
        .then(
            CommandManager.literal(ADD)
                .then(
                    CommandManager.argument(ID_ARGUMENT, IdentifierArgumentType.identifier())
                        .executes(
                            context ->
                                edit(
                                    context,
                                    option,
                                    (current, id) ->
                                        IdentifierListEdits.add(
                                            current, id, option.maxEntries())))))
        .then(
            CommandManager.literal(REMOVE)
                .then(
                    CommandManager.argument(ID_ARGUMENT, IdentifierArgumentType.identifier())
                        .executes(context -> edit(context, option, IdentifierListEdits::remove))))
        .then(
            CommandManager.literal(CLEAR)
                .executes(
                    context ->
                        applyResult(
                            context,
                            option,
                            IdentifierListEdits.clear(option.read(ServerConfigService.get())))));
  }

  private static int edit(
      final CommandContext<ServerCommandSource> context,
      final IdentifierListOption<NotEnoughArrowsConfig> option,
      final Edit edit) {
    final String id = IdentifierArgumentType.getIdentifier(context, ID_ARGUMENT).toString();
    return applyResult(context, option, edit.apply(option.read(ServerConfigService.get()), id));
  }

  private static int applyResult(
      final CommandContext<ServerCommandSource> context,
      final IdentifierListOption<NotEnoughArrowsConfig> option,
      final IdentifierListEdits.Result result) {
    if (!result.outcome().succeeded()) {
      context.getSource().sendError(rejection(option.id(), result.outcome()));
      return 0;
    }
    return ConfigUpdates.apply(
        context,
        option.write(ServerConfigService.get(), result.updated()),
        option.id(),
        ConfigValueFormat.of(result.updated()));
  }

  private static Text rejection(final String setting, final IdentifierListEdits.Outcome outcome) {
    return Text.translatable(rejectionKey(outcome), setting);
  }

  static String rejectionKey(final IdentifierListEdits.Outcome outcome) {
    return switch (outcome) {
      case ALREADY_PRESENT -> "command.not-enough-arrows.list.already_present";
      case NOT_PRESENT -> "command.not-enough-arrows.list.not_present";
      case LIST_FULL -> "command.not-enough-arrows.list.full";
      default -> "command.not-enough-arrows.list.invalid";
    };
  }
}
