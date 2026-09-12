package com.grahambartley.notenougharrows.incendiary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class IncendiaryBurnTest {
  private static final Vec3d CENTER = new Vec3d(0.0, 64.0, 0.0);
  private static final int RADIUS = 3;

  @ParameterizedTest
  @CsvSource({"0.0, true", "2.9, true", "3.0, true", "3.1, false", "30.0, false"})
  void onlyEntitiesInsideTheRadiusCatchFire(final double distance, final boolean reached) {
    assertEquals(reached, IncendiaryBurn.reaches(CENTER, CENTER.add(distance, 0.0, 0.0), RADIUS));
  }

  @Test
  void theRadiusIsMeasuredInEveryDirectionRatherThanOnOneAxis() {
    assertTrue(IncendiaryBurn.reaches(CENTER, CENTER.add(1.0, 1.0, 1.0), RADIUS));
    assertFalse(IncendiaryBurn.reaches(CENTER, CENTER.add(2.0, 2.0, 2.0), RADIUS));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -8})
  void aRadiusOfZeroOrLessBurnsNothing(final int radius) {
    assertFalse(IncendiaryBurn.reaches(CENTER, CENTER, radius));
  }

  @ParameterizedTest
  @NullSource
  void aMissingPositionBurnsNothing(final Vec3d missing) {
    assertFalse(IncendiaryBurn.reaches(missing, CENTER, RADIUS));
    assertFalse(IncendiaryBurn.reaches(CENTER, missing, RADIUS));
  }

  @ParameterizedTest
  @CsvSource({"0, 0", "1, 20", "5, 100", "60, 1200"})
  void igniteSecondsBecomeTicks(final int seconds, final int ticks) {
    assertEquals(ticks, IncendiaryBurn.igniteTicks(seconds));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -60})
  void aNegativeIgniteTimeNeverBurns(final int seconds) {
    assertEquals(0, IncendiaryBurn.igniteTicks(seconds));
  }
}
