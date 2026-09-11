package com.grahambartley.notenougharrows.explosive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.config.ExplosiveArrowConfig;
import com.grahambartley.notenougharrows.config.ExplosiveTierConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExplosiveTierTest {

  @ParameterizedTest
  @CsvSource({"GUNPOWDER, 60, 4.0", "TNT, 50, 6.0", "FIRE_CHARGE, 40, 8.0"})
  void eachTierReadsItsOwnSectionOfTheConfig(
      final ExplosiveTier tier, final int delayTicks, final float power) {
    final ExplosiveTierConfig selected = tier.in(ExplosiveArrowConfig.defaults());

    assertEquals(delayTicks, selected.delayTicks());
    assertEquals(power, selected.power());
  }

  @Test
  void aTierFollowsTheConfigItIsGivenRatherThanTheDefaults() {
    final ExplosiveArrowConfig config =
        ExplosiveArrowConfig.defaults().withTnt(new ExplosiveTierConfig(7, 11.0f));

    assertEquals(7, ExplosiveTier.TNT.in(config).delayTicks());
    assertEquals(11.0f, ExplosiveTier.TNT.in(config).power());
  }

  @Test
  void onlyTheTopTierLeavesFireBehind() {
    assertFalse(ExplosiveTier.GUNPOWDER.leavesFire());
    assertFalse(ExplosiveTier.TNT.leavesFire());
    assertTrue(ExplosiveTier.FIRE_CHARGE.leavesFire());
  }

  @Test
  void eachTierIsStrongerThanTheOneBelowIt() {
    final ExplosiveArrowConfig defaults = ExplosiveArrowConfig.defaults();

    assertTrue(
        ExplosiveTier.TNT.in(defaults).power() > ExplosiveTier.GUNPOWDER.in(defaults).power());
    assertTrue(
        ExplosiveTier.FIRE_CHARGE.in(defaults).power() > ExplosiveTier.TNT.in(defaults).power());
  }
}
