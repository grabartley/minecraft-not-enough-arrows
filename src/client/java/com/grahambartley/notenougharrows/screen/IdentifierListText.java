package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.ConfigValues;
import java.util.Arrays;
import java.util.List;

public final class IdentifierListText {
  public static final String SEPARATOR = ", ";

  private IdentifierListText() {}

  public static int maxLength(final int maxEntries) {
    return maxEntries * (ConfigValues.MAX_IDENTIFIER_LENGTH + SEPARATOR.length());
  }

  public static String join(final List<String> values) {
    return values == null ? "" : String.join(SEPARATOR, values);
  }

  public static List<String> split(final String text) {
    if (text == null || text.isBlank()) {
      return List.of();
    }
    return Arrays.stream(text.split(","))
        .map(String::trim)
        .filter(entry -> !entry.isEmpty())
        .toList();
  }
}
