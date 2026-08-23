package com.grahambartley.morearrows.command;

import java.util.List;
import java.util.Locale;

public final class ConfigValueFormat {
  public static final String EMPTY_LIST = "(none)";

  private ConfigValueFormat() {}

  public static String of(final int value) {
    return Integer.toString(value);
  }

  public static String of(final boolean value) {
    return Boolean.toString(value);
  }

  public static String of(final float value) {
    return String.format(Locale.ROOT, "%.2f", value);
  }

  public static String of(final List<String> values) {
    return values == null || values.isEmpty() ? EMPTY_LIST : String.join(", ", values);
  }
}
