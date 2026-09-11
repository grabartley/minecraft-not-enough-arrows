package com.grahambartley.notenougharrows.config;

public final class ServerConfigHolder {
  private static volatile NotEnoughArrowsConfig current = NotEnoughArrowsConfig.defaults();

  private ServerConfigHolder() {}

  public static NotEnoughArrowsConfig get() {
    return current;
  }

  public static void set(final NotEnoughArrowsConfig config) {
    current = config == null ? NotEnoughArrowsConfig.defaults() : config;
  }

  public static void reset() {
    current = NotEnoughArrowsConfig.defaults();
  }
}
