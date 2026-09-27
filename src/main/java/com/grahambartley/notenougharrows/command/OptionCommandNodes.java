package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.option.BooleanOption;
import com.grahambartley.notenougharrows.config.option.ConfigOption;
import com.grahambartley.notenougharrows.config.option.ConfigSection;
import com.grahambartley.notenougharrows.config.option.FloatOption;
import com.grahambartley.notenougharrows.config.option.IdentifierListOption;
import com.grahambartley.notenougharrows.config.option.IntOption;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class OptionCommandNodes {
  private OptionCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> section(
      final ConfigSection<NotEnoughArrowsConfig> section) {
    final LiteralArgumentBuilder<ServerCommandSource> group = ConfigOptionNodes.group(section.id());
    section.options().forEach(option -> group.then(nested(section.id(), option)));
    return group;
  }

  private static LiteralArgumentBuilder<ServerCommandSource> nested(
      final String sectionId, final ConfigOption<NotEnoughArrowsConfig> option) {
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
      final ConfigOption<NotEnoughArrowsConfig> option) {
    return switch (option) {
      case IntOption<NotEnoughArrowsConfig> intOption ->
          ConfigOptionNodes.intOption(
              intOption.id(), intOption.min(), intOption.max(), intOption::write);
      case FloatOption<NotEnoughArrowsConfig> floatOption ->
          ConfigOptionNodes.floatOption(
              floatOption.id(), floatOption.min(), floatOption.max(), floatOption::write);
      case BooleanOption<NotEnoughArrowsConfig> booleanOption ->
          ConfigOptionNodes.booleanOption(booleanOption.id(), booleanOption::write);
      case IdentifierListOption<NotEnoughArrowsConfig> listOption ->
          IdentifierListNodes.build(listOption);
    };
  }
}
