package com.grahambartley.morearrows.config;

public final class ClientConfigHolder {
  private static volatile MoreArrowsConfig current = MoreArrowsConfig.defaults();
  private static volatile boolean synced;

  private ClientConfigHolder() {}

  public static MoreArrowsConfig get() {
    return current;
  }

  public static boolean isSynced() {
    return synced;
  }

  public static void accept(final MoreArrowsConfig config) {
    if (config == null) {
      return;
    }
    current = config;
    synced = true;
  }

  public static void clear() {
    current = MoreArrowsConfig.defaults();
    synced = false;
  }
}
