package com.grahambartley.morearrows.config;

public final class ServerConfigHolder {
  private static volatile MoreArrowsConfig current = MoreArrowsConfig.defaults();

  private ServerConfigHolder() {}

  public static MoreArrowsConfig get() {
    return current;
  }

  public static void set(final MoreArrowsConfig config) {
    current = config == null ? MoreArrowsConfig.defaults() : config;
  }

  public static void reset() {
    current = MoreArrowsConfig.defaults();
  }
}
