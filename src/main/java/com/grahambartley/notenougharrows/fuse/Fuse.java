package com.grahambartley.notenougharrows.fuse;

import java.util.Objects;
import java.util.UUID;

public record Fuse(UUID hostId, int delayTicks, int remainingTicks, int lostTicks) {
  public static final int LOST_HOST_GRACE_TICKS = 20 * 60;

  public Fuse {
    Objects.requireNonNull(hostId, "A fuse needs the entity that carries it");
    delayTicks = Math.max(0, delayTicks);
    remainingTicks = Math.max(0, Math.min(remainingTicks, delayTicks));
    lostTicks = Math.max(0, lostTicks);
  }

  public static Fuse lit(final UUID hostId, final int delayTicks) {
    final int delay = Math.max(0, delayTicks);
    return new Fuse(hostId, delay, delay, 0);
  }

  public boolean isInstant() {
    return delayTicks == 0;
  }

  public boolean hasExpired() {
    return remainingTicks == 0;
  }

  public boolean beepsNow() {
    return FuseCadence.beepsAt(remainingTicks, delayTicks);
  }

  public boolean isAbandoned() {
    return lostTicks >= LOST_HOST_GRACE_TICKS;
  }

  public Fuse burned() {
    return new Fuse(hostId, delayTicks, remainingTicks - 1, 0);
  }

  public Fuse lost() {
    return new Fuse(hostId, delayTicks, remainingTicks, lostTicks + 1);
  }
}
