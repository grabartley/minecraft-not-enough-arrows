package com.grahambartley.notenougharrows.chaos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class BoomerangFlightTest {
  @Test
  void turningBackReversesAndSlowsTheArrow() {
    final Vec3d turned = BoomerangFlight.turnBack(new Vec3d(2.0, 0.0, 0.0));

    assertTrue(turned.x < 0.0);
    assertTrue(turned.length() < 2.0);
  }

  @Test
  void steeringPullsTheArrowTowardHomeWithoutASurface() {
    final Vec3d position = new Vec3d(10.0, 0.0, 0.0);
    Vec3d velocity = new Vec3d(1.0, 0.0, 0.0);
    for (int tick = 0; tick < 40; tick++) {
      velocity = BoomerangFlight.steer(velocity, position, Vec3d.ZERO);
    }

    assertTrue(velocity.x < 0.0, "After a while the arrow should be heading home");
  }

  @Test
  void steeringCurvesRatherThanSnappingAround() {
    final Vec3d velocity = new Vec3d(0.0, 0.0, 1.0);
    final Vec3d steered = BoomerangFlight.steer(velocity, new Vec3d(10.0, 0.0, 0.0), Vec3d.ZERO);

    assertTrue(steered.z > 0.0, "One tick of steering keeps most of the old heading");
    assertTrue(steered.x < 0.0, "and bends it toward home");
  }

  @Test
  void aFastArrowIsSlowedTowardTopSpeed() {
    final Vec3d steered =
        BoomerangFlight.steer(new Vec3d(-5.0, 0.0, 0.0), new Vec3d(10.0, 0.0, 0.0), Vec3d.ZERO);

    assertTrue(steered.length() < 5.0);
  }

  @Test
  void anArrowHeadingAwaySidewaysStillArrivesRatherThanCircling() {
    Vec3d position = new Vec3d(5.0, 0.0, 0.0);
    Vec3d velocity = new Vec3d(1.2, 0.0, 1.0);
    int ticks = 0;
    while (!BoomerangFlight.hasArrived(position, Vec3d.ZERO) && ticks < 40) {
      velocity = BoomerangFlight.steer(velocity, position, Vec3d.ZERO);
      position = position.add(velocity);
      ticks++;
    }

    assertTrue(BoomerangFlight.hasArrived(position, Vec3d.ZERO), "Still out at " + position);
  }

  @Test
  void anArrowAlreadyHomeStops() {
    assertEquals(Vec3d.ZERO, BoomerangFlight.steer(new Vec3d(1, 0, 0), Vec3d.ZERO, Vec3d.ZERO));
  }

  @Test
  void arrivesWithinArrivalDistance() {
    assertTrue(BoomerangFlight.hasArrived(new Vec3d(1.0, 0.0, 0.0), Vec3d.ZERO));
    assertFalse(BoomerangFlight.hasArrived(new Vec3d(2.0, 0.0, 0.0), Vec3d.ZERO));
  }

  @Test
  void runsOutOfTimeAtTheReturnLimit() {
    assertFalse(BoomerangFlight.hasRunOutOfTime(BoomerangFlight.MAX_RETURN_TICKS - 1));
    assertTrue(BoomerangFlight.hasRunOutOfTime(BoomerangFlight.MAX_RETURN_TICKS));
  }
}
