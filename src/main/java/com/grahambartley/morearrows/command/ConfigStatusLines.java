package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.option.ServerConfigOptions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;

public final class ConfigStatusLines {
  public static final String HEADER_KEY = "command.more-arrows.status.header";
  public static final String ENTRY_KEY = "command.more-arrows.status.entry";

  private ConfigStatusLines() {}

  public record StatusEntry(String setting, String value) {}

  public static List<Text> lines(final MoreArrowsConfig config) {
    final List<Text> lines = new ArrayList<>();
    lines.add(Text.translatable(HEADER_KEY));
    for (final StatusEntry entry : entries(config)) {
      lines.add(Text.translatable(ENTRY_KEY, entry.setting(), entry.value()));
    }
    return List.copyOf(lines);
  }

  public static List<StatusEntry> entries(final MoreArrowsConfig config) {
    return ServerConfigOptions.all().stream()
        .map(option -> new StatusEntry(option.id(), option.displayValue(config)))
        .toList();
  }
}
