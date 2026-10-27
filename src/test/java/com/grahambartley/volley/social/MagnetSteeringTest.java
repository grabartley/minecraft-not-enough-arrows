package com.grahambartley.volley.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

  @ParameterizedTest(name = "{0} blocks away arrived = {1}")
  @CsvSource({"0.0, true", "0.5, true", "1.0, true", "1.1, false", "8.0, false"})
  void arrivesWithinTheArrivalDistance(final double distance, final boolean arrived) {
    assertEquals(arrived, MagnetSteering.hasArrived(Vec3d.ZERO, new Vec3d(distance, 0, 0)));
  }
}
