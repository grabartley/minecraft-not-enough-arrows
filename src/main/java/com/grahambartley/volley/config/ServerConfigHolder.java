package com.grahambartley.volley.config;

public final class ServerConfigHolder {
  private static volatile VolleyConfig current = VolleyConfig.defaults();

  private ServerConfigHolder() {}

  public static VolleyConfig get() {
    return current;
  }

  public static void set(final VolleyConfig config) {
    current = config == null ? VolleyConfig.defaults() : config;
  }

  public static void reset() {
    current = VolleyConfig.defaults();
  }
}
