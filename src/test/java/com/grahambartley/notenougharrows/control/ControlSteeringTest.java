package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class ControlSteeringTest {

  private static final Vec3d ANCHOR = new Vec3d(0.0, 64.0, 0.0);

  @Test
  void drawnSendsTheMobStraightToTheAnchor() {
    assertEquals(
        Optional.of(ANCHOR), ControlSteering.DRAWN.destination(new Vec3d(5.0, 64.0, 0.0), ANCHOR));
  }

  @Test
  void fleeingSendsTheMobDirectlyAwayFromTheAnchor() {
    final Vec3d from = new Vec3d(3.0, 64.0, 0.0);
    final Vec3d destination = ControlSteering.FLEEING.destination(from, ANCHOR).orElseThrow();

    assertEquals(3.0 + ControlSteering.FLEE_DISTANCE, destination.getX(), 1.0e-6);
    assertEquals(64.0, destination.getY(), 1.0e-6);
    assertEquals(0.0, destination.getZ(), 1.0e-6);
  }

  @Test
  void fleeingMovesTheMobFurtherFromTheAnchorThanItStarted() {
    final Vec3d from = new Vec3d(1.0, 65.0, 2.0);
    final Vec3d destination = ControlSteering.FLEEING.destination(from, ANCHOR).orElseThrow();

    assertTrue(destination.squaredDistanceTo(ANCHOR) > from.squaredDistanceTo(ANCHOR));
  }

  @Test
  void fleeingFromExactlyTheAnchorPicksNoDirection() {
    assertEquals(Optional.empty(), ControlSteering.FLEEING.destination(ANCHOR, ANCHOR));
  }

  @Test
  void wanderingNamesNoDestination() {
    assertEquals(
        Optional.empty(), ControlSteering.WANDERING.destination(new Vec3d(5.0, 64.0, 0.0), ANCHOR));
  }

  @Test
  void onlyTheSteeringsThatNameADestinationDriveNavigation() {
    assertTrue(ControlSteering.DRAWN.navigates());
    assertTrue(ControlSteering.FLEEING.navigates());
    assertFalse(ControlSteering.WANDERING.navigates());
  }

  @Test
  void onlyFleeingLeavesTheMobFreeToRetaliate() {
    assertTrue(ControlSteering.DRAWN.clearsTarget());
    assertTrue(ControlSteering.WANDERING.clearsTarget());
    assertFalse(ControlSteering.FLEEING.clearsTarget());
  }
}
