package com.grahambartley.notenougharrows.tow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.grapple.GrapplePull;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class TowPullTest {
  private static final double EPSILON = 1.0E-9;
  private static final Vec3d SHOOTER = new Vec3d(0.0, 64.0, 0.0);

  @Test
  void distanceIsMeasuredAcrossTheGroundIgnoringHeight() {
    assertEquals(5.0, TowPull.horizontalDistance(new Vec3d(3.0, 90.0, 4.0), SHOOTER), EPSILON);
  }

  @Test
  void aTargetHasArrivedWithinTheGrapplesArrivalDistance() {
    assertTrue(
        TowPull.hasArrived(new Vec3d(GrapplePull.ARRIVAL_DISTANCE - 0.1, 70.0, 0.0), SHOOTER));
    assertFalse(
        TowPull.hasArrived(new Vec3d(GrapplePull.ARRIVAL_DISTANCE + 0.1, 64.0, 0.0), SHOOTER));
  }

  @Test
  void theDragIsHorizontalAtTheGivenSpeedTowardTheShooter() {
    final Vec3d velocity =
        TowPull.velocity(new Vec3d(10.0, 64.0, 0.0), SHOOTER, new Vec3d(0.3, -0.2, 0.9), 0.5);

    assertEquals(-0.5, velocity.getX(), EPSILON);
    assertEquals(0.0, velocity.getZ(), EPSILON);
  }

  @Test
  void theDragLeavesGravityToVanillaSoTheGroundBetweenMatters() {
    final Vec3d falling = new Vec3d(0.0, -0.6, 0.0);

    assertEquals(
        -0.6, TowPull.velocity(new Vec3d(10.0, 80.0, 0.0), SHOOTER, falling, 0.5).getY(), EPSILON);
  }

  @Test
  void aDiagonalDragStillMovesAtTheGivenSpeed() {
    final Vec3d velocity = TowPull.velocity(new Vec3d(6.0, 64.0, 8.0), SHOOTER, Vec3d.ZERO, 0.5);

    assertEquals(0.5, Math.hypot(velocity.getX(), velocity.getZ()), EPSILON);
  }

  @Test
  void anArrivedOrStalledTowDragsNothing() {
    final Vec3d current = new Vec3d(0.4, -0.1, 0.4);

    assertEquals(
        new Vec3d(0.0, -0.1, 0.0),
        TowPull.velocity(new Vec3d(1.0, 64.0, 0.0), SHOOTER, current, 0.5));
    assertEquals(
        new Vec3d(0.0, -0.1, 0.0),
        TowPull.velocity(new Vec3d(9.0, 64.0, 0.0), SHOOTER, current, 0.0));
  }

  @Test
  void stoppingKeepsOnlyTheFall() {
    assertEquals(new Vec3d(0.0, -0.3, 0.0), TowPull.stopped(new Vec3d(0.7, -0.3, -0.2)));
  }

  @Test
  void theDragBuildsUpToItsTopSpeed() {
    assertEquals(0.1, TowPull.speedAt(0, 0.5), EPSILON);
    assertEquals(0.5, TowPull.speedAt(4, 0.5), EPSILON);
    assertEquals(0.5, TowPull.speedAt(50, 0.5), EPSILON);
  }
}
