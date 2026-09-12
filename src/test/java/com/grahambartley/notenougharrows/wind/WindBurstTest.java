package com.grahambartley.notenougharrows.wind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class WindBurstTest {
  private static final Vec3d CENTER = new Vec3d(0.0, 64.0, 0.0);
  private static final float RADIUS = 4.0f;
  private static final float STRENGTH = 2.0f;
  private static final double TOLERANCE = 1.0e-6;

  @Test
  void anEntityOnTheImpactPointIsPushedStraightUp() {
    final Vec3d push = WindBurst.push(CENTER, CENTER, RADIUS, STRENGTH);

    assertEquals(0.0, push.x, TOLERANCE);
    assertEquals(STRENGTH, push.y, TOLERANCE);
    assertEquals(0.0, push.z, TOLERANCE);
  }

  @Test
  void anEntityIsPushedDirectlyAwayFromTheImpactPoint() {
    final Vec3d push = WindBurst.push(CENTER, CENTER.add(3.0, 0.0, 4.0), 10.0f, 1.0f);

    assertTrue(push.x > 0.0);
    assertTrue(push.z > 0.0);
    assertEquals(push.x * 4.0, push.z * 3.0, TOLERANCE);
    assertEquals(0.0, push.y, TOLERANCE);
  }

  @ParameterizedTest
  @CsvSource({"0.0, 2.0", "1.0, 1.5", "2.0, 1.0", "3.0, 0.5"})
  void pushStrengthFallsOffLinearlyTowardTheEdge(final double distance, final double expected) {
    final Vec3d push = WindBurst.push(CENTER, CENTER.add(distance, 0.0, 0.0), RADIUS, STRENGTH);

    assertEquals(expected, push.length(), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(doubles = {4.0, 4.01, 40.0})
  void anEntityAtOrBeyondTheRadiusIsNotPushed(final double distance) {
    assertEquals(
        Vec3d.ZERO, WindBurst.push(CENTER, CENTER.add(distance, 0.0, 0.0), RADIUS, STRENGTH));
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, -1.0f})
  void aRadiusOfZeroOrLessPushesNothing(final float radius) {
    assertEquals(Vec3d.ZERO, WindBurst.push(CENTER, CENTER.add(1.0, 0.0, 0.0), radius, STRENGTH));
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, -1.0f})
  void aStrengthOfZeroOrLessPushesNothing(final float strength) {
    assertEquals(Vec3d.ZERO, WindBurst.push(CENTER, CENTER.add(1.0, 0.0, 0.0), RADIUS, strength));
  }

  @ParameterizedTest
  @NullSource
  void aMissingPositionPushesNothing(final Vec3d missing) {
    assertEquals(Vec3d.ZERO, WindBurst.push(missing, CENTER, RADIUS, STRENGTH));
    assertEquals(Vec3d.ZERO, WindBurst.push(CENTER, missing, RADIUS, STRENGTH));
  }

  @ParameterizedTest
  @CsvSource({"0.0, 1.0", "2.0, 0.5", "3.0, 0.25", "4.0, 0.0", "5.0, 0.0", "-1.0, 1.0"})
  void falloffRunsFromOneAtTheCentreToNothingAtTheEdge(
      final double distance, final double expected) {
    assertEquals(expected, WindBurst.falloff(distance, RADIUS), TOLERANCE);
  }
}
