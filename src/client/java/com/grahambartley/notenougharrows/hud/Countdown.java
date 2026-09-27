package com.grahambartley.notenougharrows.hud;

import com.grahambartley.notenougharrows.countdown.CountdownKind;

public record Countdown(int carrierId, int delayTicks, int remainingTicks, CountdownKind kind) {

  public Countdown {
    delayTicks = Math.max(0, delayTicks);
    remainingTicks = Math.max(0, Math.min(remainingTicks, delayTicks));
    kind = kind == null ? CountdownKind.FUSE : kind;
  }

  public Countdown(final int carrierId, final int delayTicks, final int remainingTicks) {
    this(carrierId, delayTicks, remainingTicks, CountdownKind.FUSE);
  }

  public boolean isBurning() {
    return delayTicks > 0 && remainingTicks > 0;
  }

  public Countdown burned() {
    return new Countdown(carrierId, delayTicks, remainingTicks - 1, kind);
  }
}
