package com.grahambartley.notenougharrows.control;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SlimeSteering {
  public static final int STEERING_GRACE_TICKS = 2;

  private static final Map<UUID, Long> STEERED_AT = new HashMap<>();

  private SlimeSteering() {}

  public static void steer(final UUID slime, final long now) {
    STEERED_AT.put(slime, now);
  }

  public static boolean isSteered(final UUID slime, final long now) {
    final Long at = STEERED_AT.get(slime);
    if (at == null) {
      return false;
    }
    if (now - at > STEERING_GRACE_TICKS) {
      STEERED_AT.remove(slime);
      return false;
    }
    return true;
  }

  public static void release(final UUID slime) {
    STEERED_AT.remove(slime);
  }

  public static void forgetAll() {
    STEERED_AT.clear();
  }
}
