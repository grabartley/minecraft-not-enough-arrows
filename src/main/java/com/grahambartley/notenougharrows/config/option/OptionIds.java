package com.grahambartley.notenougharrows.config.option;

public final class OptionIds {
  private OptionIds() {}

  public static String require(final String id) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Config option id must not be blank");
    }
    return id;
  }
}
