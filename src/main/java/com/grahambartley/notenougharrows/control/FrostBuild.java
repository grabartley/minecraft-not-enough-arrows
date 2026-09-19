package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.LivingEntity;

public final class FrostBuild {

  private FrostBuild() {}

  public static int builtTicks(final int current, final int perHit, final int threshold) {
    if (perHit <= 0) {
      return Math.max(0, current);
    }
    return Math.min(Math.max(0, current) + perHit, Math.max(0, threshold) + perHit);
  }

  public static boolean build(final LivingEntity target, final int perHit) {
    if (target == null || perHit <= 0 || !target.canFreeze()) {
      return false;
    }
    final int built = builtTicks(target.getFrozenTicks(), perHit, target.getMinFreezeDamageTicks());
    if (built <= target.getFrozenTicks()) {
      return false;
    }
    target.setFrozenTicks(built);
    return true;
  }
}
