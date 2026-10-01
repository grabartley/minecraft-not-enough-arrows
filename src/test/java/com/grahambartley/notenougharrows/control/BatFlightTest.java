package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class BatFlightTest {
  private static final double TOLERANCE = 1.0e-9;

  @Test
  void pullsAlongTheHeadingRatherThanAtFortyFiveDegrees() {
    final Vec3d steered = BatFlight.steered(Vec3d.ZERO, new Vec3d(12.0, 0.0, -1.0));
    assertTrue(Math.abs(steered.z) < Math.abs(steered.x) / 10.0);
  }

  @Test
  void pullsStraightAlongAnAxis() {
    final Vec3d steered = BatFlight.steered(Vec3d.ZERO, new Vec3d(12.0, 0.0, 0.0));
    assertEquals(0.05, steered.x, TOLERANCE);
    assertEquals(0.0, steered.z, TOLERANCE);
  }

  @Test
  void keepsTheSameHorizontalPullOnADiagonal() {
    final Vec3d steered = BatFlight.steered(Vec3d.ZERO, new Vec3d(5.0, 0.0, 5.0));
    assertEquals(0.05, Math.hypot(steered.x, steered.z), TOLERANCE);
  }

  @Test
  void easesTowardThePullFromItsCurrentVelocity() {
    final Vec3d steered = BatFlight.steered(new Vec3d(0.3, 0.0, 0.0), new Vec3d(12.0, 0.0, 0.0));
    assertEquals(0.32, steered.x, TOLERANCE);
  }

  @Test
  void climbsAndDivesTowardTheTargetHeight() {
    assertEquals(0.07, BatFlight.steered(Vec3d.ZERO, new Vec3d(0.0, 3.0, 0.0)).y, TOLERANCE);
    assertEquals(-0.07, BatFlight.steered(Vec3d.ZERO, new Vec3d(0.0, -3.0, 0.0)).y, TOLERANCE);
  }

  @Test
  void slowsToAHoverOverItsTarget() {
    final Vec3d steered = BatFlight.steered(new Vec3d(0.2, 0.0, -0.2), Vec3d.ZERO);
    assertEquals(0.18, steered.x, TOLERANCE);
    assertEquals(-0.18, steered.z, TOLERANCE);
  }
}
