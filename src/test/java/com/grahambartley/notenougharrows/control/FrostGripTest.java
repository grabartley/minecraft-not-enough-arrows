package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FrostGripTest {

  private static final int VANILLA_DAMAGE_THRESHOLD = 140;

  @Test
  void pinsAboveTheThresholdVanillaStartsDealingFreezeDamageAt() {
    assertTrue(FrostGrip.pinnedTicks(VANILLA_DAMAGE_THRESHOLD) > VANILLA_DAMAGE_THRESHOLD);
  }

  @Test
  void leavesEnoughHeadroomToOutrunVanillasTwoTicksPerTickThaw() {
    assertTrue(FrostGrip.pinnedTicks(VANILLA_DAMAGE_THRESHOLD) - VANILLA_DAMAGE_THRESHOLD >= 2);
  }

  @ParameterizedTest
  @CsvSource({"0, 4", "140, 144", "300, 304"})
  void pinsRelativeToWhateverThresholdTheTargetHas(final int threshold, final int expected) {
    assertEquals(expected, FrostGrip.pinnedTicks(threshold));
  }

  @Test
  void treatsANegativeThresholdAsNone() {
    assertEquals(FrostGrip.HEADROOM_TICKS, FrostGrip.pinnedTicks(-50));
  }

  @Test
  void holdsNothingWithoutADuration() {
    assertFalse(FrostGrip.holds(0));
    assertFalse(FrostGrip.holds(-1));
    assertTrue(FrostGrip.holds(1));
  }

  @ParameterizedTest
  @CsvSource({"0, true", "1, false", "4, false", "5, true", "10, true"})
  void shimmersOnceEveryInterval(final long tick, final boolean expected) {
    assertEquals(expected, FrostGrip.shimmersOn(tick));
  }

  @ParameterizedTest
  @CsvSource({"0, true", "1, false", "39, false", "40, true", "80, true"})
  void bitesOnVanillasFreezeDamageCadence(final int age, final boolean expected) {
    assertEquals(expected, FrostGrip.bitesOn(age));
  }
}
