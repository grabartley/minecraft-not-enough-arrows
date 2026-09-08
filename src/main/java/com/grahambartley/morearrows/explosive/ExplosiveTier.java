package com.grahambartley.morearrows.explosive;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.ExplosiveTierConfig;
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
    return selector.apply(config == null ? ExplosiveArrowConfig.defaults() : config);
  }

  public boolean leavesFire() {
    return leavesFire;
  }
}
