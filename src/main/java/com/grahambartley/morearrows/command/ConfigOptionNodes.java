package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigValueFormat;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class ConfigOptionNodes {
  public static final String VALUE_ARGUMENT = "value";

  private ConfigOptionNodes() {}

  @FunctionalInterface
  public interface IntChange {
    MoreArrowsConfig apply(MoreArrowsConfig current, int value);
  }

  @FunctionalInterface
  public interface FloatChange {
    MoreArrowsConfig apply(MoreArrowsConfig current, float value);
  }

  @FunctionalInterface
  public interface BooleanChange {
    MoreArrowsConfig apply(MoreArrowsConfig current, boolean value);
  }

  public static String literalFor(final String setting) {
    return setting.substring(setting.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
  }

  public static LiteralArgumentBuilder<ServerCommandSource> group(final String setting) {
    return CommandManager.literal(literalFor(setting));
  }

  public static LiteralArgumentBuilder<ServerCommandSource> intOption(
      final String setting, final int min, final int max, final IntChange change) {
    return group(setting)
        .then(
            CommandManager.argument(VALUE_ARGUMENT, IntegerArgumentType.integer(min, max))
                .executes(
                    context -> {
                      final int value = IntegerArgumentType.getInteger(context, VALUE_ARGUMENT);
                      return ConfigUpdates.apply(
                          context,
                          change.apply(ServerConfigService.get(), value),
                          setting,
                          ConfigValueFormat.of(value));
                    }));
  }

  public static LiteralArgumentBuilder<ServerCommandSource> floatOption(
      final String setting, final float min, final float max, final FloatChange change) {
    return group(setting)
        .then(
            CommandManager.argument(VALUE_ARGUMENT, FloatArgumentType.floatArg(min, max))
                .executes(
                    context -> {
                      final float value = FloatArgumentType.getFloat(context, VALUE_ARGUMENT);
                      return ConfigUpdates.apply(
                          context,
                          change.apply(ServerConfigService.get(), value),
                          setting,
                          ConfigValueFormat.of(value));
                    }));
  }

  public static LiteralArgumentBuilder<ServerCommandSource> booleanOption(
      final String setting, final BooleanChange change) {
    return group(setting)
        .then(
            CommandManager.argument(VALUE_ARGUMENT, BoolArgumentType.bool())
                .executes(
                    context -> {
                      final boolean value = BoolArgumentType.getBool(context, VALUE_ARGUMENT);
                      return ConfigUpdates.apply(
                          context,
                          change.apply(ServerConfigService.get(), value),
                          setting,
                          ConfigValueFormat.of(value));
                    }));
  }
}
