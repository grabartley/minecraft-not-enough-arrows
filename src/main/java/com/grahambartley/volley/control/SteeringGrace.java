package com.grahambartley.volley.control;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SteeringGrace {
  public static final int STEERING_GRACE_TICKS = 2;

  public static final SteeringGrace SLIMES = new SteeringGrace();
  public static final SteeringGrace BATS = new SteeringGrace();

  private final Map<UUID, Long> steeredAt = new HashMap<>();

  public void steer(final UUID mob, final long now) {
    steeredAt.put(mob, now);
  }

  public boolean isSteered(final UUID mob, final long now) {
    final Long at = steeredAt.get(mob);
    if (at == null) {
      return false;
    }
    if (now - at > STEERING_GRACE_TICKS) {
      steeredAt.remove(mob);
      return false;
    }
    return true;
  }

  public void release(final UUID mob) {
    steeredAt.remove(mob);
  }

  public void forgetAll() {
    steeredAt.clear();
  }
}
