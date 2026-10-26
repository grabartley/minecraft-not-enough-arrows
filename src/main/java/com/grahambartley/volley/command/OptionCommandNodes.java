package com.grahambartley.volley.command;

import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.config.option.BooleanOption;
import com.grahambartley.volley.config.option.ConfigOption;
import com.grahambartley.volley.config.option.ConfigSection;
import com.grahambartley.volley.config.option.FloatOption;
import com.grahambartley.volley.config.option.IdentifierListOption;
import com.grahambartley.volley.config.option.IntOption;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class OptionCommandNodes {
  private OptionCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> section(
      final ConfigSection<VolleyConfig> section) {
    final LiteralArgumentBuilder<ServerCommandSource> group = ConfigOptionNodes.group(section.id());
    section.options().forEach(option -> group.then(nested(section.id(), option)));
    return group;
  }

  private static LiteralArgumentBuilder<ServerCommandSource> nested(
      final String sectionId, final ConfigOption<VolleyConfig> option) {
    final String prefix = sectionId + ".";
    if (!option.id().startsWith(prefix)) {
      throw new IllegalArgumentException(
          "Option " + option.id() + " does not belong to section " + sectionId);
    }
    final String[] segments = option.id().substring(prefix.length()).split("\\.");
    LiteralArgumentBuilder<ServerCommandSource> node = option(option);
    for (int i = segments.length - 2; i >= 0; i--) {
      node = ConfigOptionNodes.group(segments[i]).then(node);
    }
    return node;
  }

  private static LiteralArgumentBuilder<ServerCommandSource> option(
      final ConfigOption<VolleyConfig> option) {
    return switch (option) {
      case IntOption<VolleyConfig> intOption ->
          ConfigOptionNodes.intOption(
              intOption.id(), intOption.min(), intOption.max(), intOption::write);
      case FloatOption<VolleyConfig> floatOption ->
          ConfigOptionNodes.floatOption(
              floatOption.id(), floatOption.min(), floatOption.max(), floatOption::write);
      case BooleanOption<VolleyConfig> booleanOption ->
          ConfigOptionNodes.booleanOption(booleanOption.id(), booleanOption::write);
      case IdentifierListOption<VolleyConfig> listOption -> IdentifierListNodes.build(listOption);
    };
  }
}
