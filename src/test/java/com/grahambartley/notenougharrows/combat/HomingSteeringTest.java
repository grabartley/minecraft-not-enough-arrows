package com.grahambartley.notenougharrows.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HomingSteeringTest {
  private static final double TOLERANCE = 1.0e-9;
  private static final Vec3d EASTWARD = new Vec3d(1.0, 0.0, 0.0);

  @Test
  void aTargetDeadAheadIsInsideEvenTheNarrowestCone() {
    assertTrue(HomingSteering.withinCone(EASTWARD, EASTWARD, 1.0f));
  }

  @Test
  void aTargetDirectlyBehindIsOutsideEvenTheWidestCone() {
    assertFalse(HomingSteering.withinCone(EASTWARD, EASTWARD.multiply(-1.0), 180.0f));
  }

  @ParameterizedTest
  @CsvSource({"60.0, true", "90.0, true", "180.0, true", "50.0, false", "30.0, false"})
  void aTargetThirtyDegreesOffIsInsideAnySixtyDegreeOrWiderCone(
      final float coneDegrees, final boolean expected) {
    final Vec3d thirtyDegreesOff = EASTWARD.rotateY((float) Math.toRadians(30.0));

    assertEquals(expected, HomingSteering.withinCone(EASTWARD, thirtyDegreesOff, coneDegrees));
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, -1.0f})
  void aConeOfNothingSeesNothing(final float coneDegrees) {
    assertFalse(HomingSteering.withinCone(EASTWARD, EASTWARD, coneDegrees));
  }

  @Test
  void aStationaryArrowHasNoDirectionToSearchAlong() {
    assertFalse(HomingSteering.withinCone(Vec3d.ZERO, EASTWARD, 180.0f));
  }

  @Test
  void steeringKeepsTheSpeedItWasFlyingAt() {
    final Vec3d velocity = new Vec3d(3.0, 0.0, 0.0);

    final Vec3d steered = HomingSteering.steer(velocity, new Vec3d(0.0, 0.0, 4.0), 0.5f);

    assertEquals(velocity.length(), steered.length(), TOLERANCE);
  }

  @Test
  void aFullTurnRatePointsStraightAtTheTarget() {
    final Vec3d steered =
        HomingSteering.steer(new Vec3d(2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 9.0), 1.0f);

    assertEquals(0.0, steered.normalize().subtract(new Vec3d(0.0, 0.0, 1.0)).length(), TOLERANCE);
  }

  @Test
  void aPartialTurnRateBendsTowardTheTargetWithoutReachingIt() {
    final Vec3d toTarget = new Vec3d(0.0, 0.0, 1.0);

    final Vec3d steered = HomingSteering.steer(EASTWARD, toTarget, 0.25f);

    assertTrue(steered.z > 0.0, "The arrow should have started turning toward the target");
    assertTrue(steered.x > steered.z, "A quarter turn should not point it straight at the target");
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, -1.0f})
  void aTurnRateOfNothingFliesStraightOn(final float turnRate) {
    final Vec3d velocity = new Vec3d(1.0, 2.0, 3.0);

    assertEquals(velocity, HomingSteering.steer(velocity, new Vec3d(0.0, 0.0, 9.0), turnRate));
  }

  @Test
  void aTargetInExactlyTheOppositeDirectionLeavesTheArrowFlyingStraight() {
    final Vec3d velocity = new Vec3d(1.0, 0.0, 0.0);

    assertEquals(velocity, HomingSteering.steer(velocity, new Vec3d(-1.0, 0.0, 0.0), 0.5f));
  }

  @Test
  void aMissingVelocitySteersNowhere() {
    assertEquals(Vec3d.ZERO, HomingSteering.steer(null, EASTWARD, 0.5f));
  }
}
