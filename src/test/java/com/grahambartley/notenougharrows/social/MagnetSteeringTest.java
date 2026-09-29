package com.grahambartley.notenougharrows.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class MagnetSteeringTest {
  private static final double EPSILON = 1.0E-9;

  @Test
  void headsStraightForTheDestination() {
    final Vec3d velocity = MagnetSteering.velocityToward(Vec3d.ZERO, new Vec3d(10.0, 0.0, 0.0));

    assertTrue(velocity.x > 0.0);
    assertEquals(0.0, velocity.y, EPSILON);
    assertEquals(0.0, velocity.z, EPSILON);
  }

  @Test
  void neverMovesFasterThanTopSpeed() {
    final Vec3d velocity = MagnetSteering.velocityToward(Vec3d.ZERO, new Vec3d(30.0, 40.0, 0.0));

    assertEquals(MagnetSteering.TOP_SPEED, velocity.length(), EPSILON);
  }

  @Test
  void slowsDownCloseInSoItDoesNotOvershoot() {
    final Vec3d velocity = MagnetSteering.velocityToward(Vec3d.ZERO, new Vec3d(0.4, 0.0, 0.0));

    assertTrue(velocity.length() < 0.4);
  }

  @Test
  void standsStillOnceThere() {
    assertEquals(Vec3d.ZERO, MagnetSteering.velocityToward(Vec3d.ZERO, Vec3d.ZERO));
  }

  @Test
  void arrivesWithinTheArrivalDistance() {
    assertTrue(
        MagnetSteering.hasArrived(Vec3d.ZERO, new Vec3d(MagnetSteering.ARRIVAL_DISTANCE, 0, 0)));
    assertFalse(
        MagnetSteering.hasArrived(
            Vec3d.ZERO, new Vec3d(MagnetSteering.ARRIVAL_DISTANCE + 0.1, 0, 0)));
  }

  @Test
  void reachesOnlyWithinTheRadius() {
    assertTrue(MagnetSteering.isWithinReach(new Vec3d(3.0, 0.0, 0.0), Vec3d.ZERO, 3));
    assertFalse(MagnetSteering.isWithinReach(new Vec3d(3.1, 0.0, 0.0), Vec3d.ZERO, 3));
  }

  @Test
  void aZeroRadiusReachesNothing() {
    assertFalse(MagnetSteering.isWithinReach(Vec3d.ZERO, Vec3d.ZERO, 0));
  }
}
