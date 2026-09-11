package com.grahambartley.notenougharrows.explosive;

import com.grahambartley.notenougharrows.config.ExplosiveArrowConfig;
import com.grahambartley.notenougharrows.config.ExplosiveTierConfig;
import java.util.function.Function;

public enum ExplosiveTier {
  GUNPOWDER(ExplosiveArrowConfig::gunpowder, false),
  TNT(ExplosiveArrowConfig::tnt, false),
  FIRE_CHARGE(ExplosiveArrowConfig::fireCharge, true);

  private final Function<ExplosiveArrowConfig, ExplosiveTierConfig> selector;
  private final boolean leavesFire;

  ExplosiveTier(
      final Function<ExplosiveArrowConfig, ExplosiveTierConfig> selector,
      final boolean leavesFire) {
    this.selector = selector;
    this.leavesFire = leavesFire;
  }

  public ExplosiveTierConfig in(final ExplosiveArrowConfig config) {
    return selector.apply(config);
  }

  public boolean leavesFire() {
    return leavesFire;
  }
}
