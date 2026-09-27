package com.grahambartley.notenougharrows.countdown;

import java.util.Objects;
import java.util.UUID;

public record CountdownTimer(
    UUID hostId, int delayTicks, int remainingTicks, long version, CountdownKind kind) {

  public CountdownTimer {
    Objects.requireNonNull(hostId, "hostId");
    Objects.requireNonNull(kind, "kind");
    delayTicks = Math.max(0, delayTicks);
    remainingTicks = Math.max(0, Math.min(remainingTicks, delayTicks));
  }

  public boolean isRunning() {
    return remainingTicks > 0;
  }
}
