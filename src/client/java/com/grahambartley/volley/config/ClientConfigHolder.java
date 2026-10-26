package com.grahambartley.volley.config;

public final class ClientConfigHolder {
  private static volatile VolleyConfig current = VolleyConfig.defaults();
  private static volatile boolean synced;

  private ClientConfigHolder() {}

  public static VolleyConfig get() {
    return current;
  }

  public static boolean isSynced() {
    return synced;
  }

  public static void accept(final VolleyConfig config) {
    if (config == null) {
      return;
    }
    current = config;
    synced = true;
  }

  public static void clear() {
    current = VolleyConfig.defaults();
    synced = false;
  }
}
