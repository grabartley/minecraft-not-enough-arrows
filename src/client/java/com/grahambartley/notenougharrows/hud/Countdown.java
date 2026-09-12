package com.grahambartley.notenougharrows.hud;

public record Countdown(int carrierId, int delayTicks, int remainingTicks) {

  public Countdown {
    delayTicks = Math.max(0, delayTicks);
    remainingTicks = Math.max(0, Math.min(remainingTicks, delayTicks));
  }

  public boolean isBurning() {
    return delayTicks > 0 && remainingTicks > 0;
  }

  public Countdown burned() {
    return new Countdown(carrierId, delayTicks, remainingTicks - 1);
  }
}
