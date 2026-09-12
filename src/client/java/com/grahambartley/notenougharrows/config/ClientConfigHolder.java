package com.grahambartley.notenougharrows.config;

public final class ClientConfigHolder {
  private static volatile NotEnoughArrowsConfig current = NotEnoughArrowsConfig.defaults();
  private static volatile boolean synced;

  private ClientConfigHolder() {}

  public static NotEnoughArrowsConfig get() {
    return current;
  }

  public static boolean isSynced() {
    return synced;
  }

  public static void accept(final NotEnoughArrowsConfig config) {
    if (config == null) {
      return;
    }
    current = config;
    synced = true;
  }

  public static void clear() {
    current = NotEnoughArrowsConfig.defaults();
    synced = false;
  }
}
