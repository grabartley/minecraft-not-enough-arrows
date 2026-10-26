package com.grahambartley.volley.command;

import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.config.option.ServerConfigOptions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;

public final class ConfigStatusLines {
  public static final String HEADER_KEY = "command.volley.status.header";
  public static final String ENTRY_KEY = "command.volley.status.entry";

  private ConfigStatusLines() {}

  public record StatusEntry(String setting, String value) {}

  public static List<Text> lines(final VolleyConfig config) {
    final List<Text> lines = new ArrayList<>();
    lines.add(Text.translatable(HEADER_KEY));
    for (final StatusEntry entry : entries(config)) {
      lines.add(Text.translatable(ENTRY_KEY, entry.setting(), entry.value()));
    }
    return List.copyOf(lines);
  }

  public static List<StatusEntry> entries(final VolleyConfig config) {
    return ServerConfigOptions.all().stream()
        .map(option -> new StatusEntry(option.id(), option.displayValue(config)))
        .toList();
  }
}
