package com.grahambartley.volley.command;

import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.config.option.ConfigOption;
import com.grahambartley.volley.server.ServerConfigService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import java.util.function.BiFunction;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

final class ConfigOptionNodes {
  static final String VALUE_ARGUMENT = "value";

  private ConfigOptionNodes() {}

  static String literalFor(final String setting) {
    return setting.substring(setting.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
  }

  static LiteralArgumentBuilder<ServerCommandSource> group(final String setting) {
    return CommandManager.literal(literalFor(setting));
  }

  static <T> LiteralArgumentBuilder<ServerCommandSource> value(
      final ConfigOption<VolleyConfig> option,
      final ArgumentType<T> argument,
      final Class<T> valueType,
      final BiFunction<VolleyConfig, T, VolleyConfig> write) {
    return group(option.id())
        .then(
            CommandManager.argument(VALUE_ARGUMENT, argument)
                .executes(
                    context -> {
                      final VolleyConfig updated =
                          write.apply(
                              ServerConfigService.get(),
                              context.getArgument(VALUE_ARGUMENT, valueType));
                      return ConfigUpdates.apply(
                          context, updated, option.id(), option.displayValue(updated));
                    }));
  }
}
