package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class ConfigUpdates {
  public static final String SUCCESS_KEY = "command.more-arrows.update.success";
  public static final String FAILED_KEY = "command.more-arrows.update.failed";

  private ConfigUpdates() {}

  public static int apply(
      final CommandContext<ServerCommandSource> context,
      final MoreArrowsConfig updated,
      final String setting,
      final String value) {
    final ServerCommandSource source = context.getSource();
    if (!ServerConfigService.update(source.getServer(), updated)) {
      source.sendError(Text.translatable(FAILED_KEY, setting));
      return 0;
    }
    source.sendFeedback(() -> Text.translatable(SUCCESS_KEY, setting, value), true);
    return 1;
  }
}
