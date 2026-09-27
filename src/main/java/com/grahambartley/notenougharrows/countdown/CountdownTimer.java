package com.grahambartley.notenougharrows.countdown;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record CountdownTimer(
    UUID hostId,
    int delayTicks,
    int remainingTicks,
    long version,
    CountdownKind kind,
    Optional<UUID> onlyFor) {

  public CountdownTimer {
    Objects.requireNonNull(hostId, "hostId");
    Objects.requireNonNull(kind, "kind");
    Objects.requireNonNull(onlyFor, "onlyFor");
    delayTicks = Math.max(0, delayTicks);
    remainingTicks = Math.max(0, Math.min(remainingTicks, delayTicks));
  }

  public boolean isShownTo(final UUID viewer) {
    return onlyFor.map(owner -> owner.equals(viewer)).orElse(true);
  }

  public boolean isRunning() {
    return remainingTicks > 0;
  }
}
