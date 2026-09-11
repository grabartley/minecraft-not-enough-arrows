package com.grahambartley.notenougharrows.client.state;

public final class ClientStateHolder {
  private static volatile ClientState current = ClientState.defaults();

  private ClientStateHolder() {}

  public static ClientState get() {
    return current;
  }

  public static void set(final ClientState state) {
    current = state == null ? ClientState.defaults() : state;
  }

  public static void reset() {
    current = ClientState.defaults();
  }
}
