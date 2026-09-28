package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.grapple.GrapplePull;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class RidePathTest {
  private static final double EPSILON = 1.0E-9;
  private static final Vec3d FROM = new Vec3d(0.5, 80.5, 0.5);
  private static final Vec3d TO = new Vec3d(20.5, 70.5, 0.5);
  private static final double GRAVITY = 0.08;

  @Test
  void theGripSitsJustAboveTheRidersHead() {
    assertEquals(new Vec3d(1.0, 65.0, 1.0), RidePath.gripOf(new Vec3d(1.0, 64.0, 1.0)));
  }

  @Test
  void progressIsHowFarAlongTheSpanTheGripHasCome() {
    assertEquals(0.0, RidePath.progress(FROM, TO, FROM), EPSILON);
    assertEquals(0.5, RidePath.progress(FROM, TO, FROM.lerp(TO, 0.5)), EPSILON);
    assertEquals(1.0, RidePath.progress(FROM, TO, TO), EPSILON);
  }

  @Test
  void progressIgnoresHowFarBelowTheSpanTheRiderHangs() {
    final Vec3d midway = FROM.lerp(TO, 0.5);

    assertEquals(
        RidePath.progress(FROM, TO, midway),
        RidePath.progress(FROM, TO, midway.add(0.0, 0.0, 3.0)),
        EPSILON);
  }

  @Test
  void progressIsHeldToTheSpanItself() {
    assertEquals(0.0, RidePath.progress(FROM, TO, FROM.subtract(5.0, 0.0, 0.0)), EPSILON);
    assertEquals(1.0, RidePath.progress(FROM, TO, TO.add(5.0, 0.0, 0.0)), EPSILON);
  }

  @Test
  void aSpanWithNoLengthCountsAsAlreadyRidden() {
    assertEquals(1.0, RidePath.progress(FROM, FROM, FROM), EPSILON);
    assertTrue(RidePath.hasArrived(FROM, FROM, FROM));
  }

  @Test
  void remainingIsTheDistanceLeftAlongTheSpan() {
    assertEquals(
        FROM.distanceTo(TO) / 2.0, RidePath.remaining(FROM, TO, FROM.lerp(TO, 0.5)), EPSILON);
  }

  @Test
  void aRiderArrivesWithinTheGrapplesArrivalDistanceOfTheFarEnd() {
    final double length = FROM.distanceTo(TO);
    final Vec3d justShort = FROM.lerp(TO, 1.0 - (GrapplePull.ARRIVAL_DISTANCE + 0.1) / length);
    final Vec3d closeEnough = FROM.lerp(TO, 1.0 - (GrapplePull.ARRIVAL_DISTANCE - 0.1) / length);

    assertFalse(RidePath.hasArrived(FROM, TO, justShort));
    assertTrue(RidePath.hasArrived(FROM, TO, closeEnough));
  }

  @Test
  void theAimLooksAheadAlongTheSpan() {
    final Vec3d aim = RidePath.aim(FROM, TO, FROM);

    assertEquals(RidePath.LOOK_AHEAD_BLOCKS, FROM.distanceTo(aim), EPSILON);
  }

  @Test
  void theAimNeverPassesTheFarEnd() {
    assertEquals(TO, RidePath.aim(FROM, TO, FROM.lerp(TO, 0.99)));
  }

  @Test
  void theVelocityCarriesTheRiderDownTheSpanAtTheGivenSpeed() {
    final Vec3d velocity = RidePath.velocity(FROM, TO, FROM, 0.6, 0.0);

    assertEquals(0.6, velocity.length(), EPSILON);
    assertTrue(velocity.getX() > 0.0, "It should head toward the far end");
    assertTrue(velocity.getY() < 0.0, "The far end is lower, so it should head down");
  }

  @Test
  void theVelocityPaysForTheGravityTheClientIsAboutToSubtract() {
    final Vec3d without = RidePath.velocity(FROM, TO, FROM, 0.6, 0.0);
    final Vec3d with = RidePath.velocity(FROM, TO, FROM, 0.6, GRAVITY);

    assertEquals(without.getY() + GRAVITY, with.getY(), EPSILON);
  }

  @Test
  void aRiderHangingBelowTheSpanIsDrawnBackUpToIt() {
    final Vec3d sagging = FROM.lerp(TO, 0.5).subtract(0.0, 3.0, 0.0);
    final Vec3d level = FROM.lerp(TO, 0.5);

    assertTrue(
        RidePath.velocity(FROM, TO, sagging, 0.6, 0.0).getY()
            > RidePath.velocity(FROM, TO, level, 0.6, 0.0).getY());
  }

  @Test
  void noSpeedOrAnArrivalMeansNoVelocity() {
    assertEquals(Vec3d.ZERO, RidePath.velocity(FROM, TO, FROM, 0.0, GRAVITY));
    assertEquals(Vec3d.ZERO, RidePath.velocity(FROM, TO, TO, 0.6, GRAVITY));
  }

  @Test
  void theSpeedBuildsFromRestToTheTopSpeed() {
    assertEquals(0.06, RidePath.speedAt(0, 0.6), EPSILON);
    assertEquals(0.6, RidePath.speedAt(9, 0.6), EPSILON);
    assertEquals(0.6, RidePath.speedAt(40, 0.6), EPSILON);
  }

  @Test
  void theTickBudgetCoversTheRideTheRampAndTheGrace() {
    final int ticks = RidePath.lifetimeTicks(FROM, TO, 0.5);

    assertEquals(
        (int) Math.ceil(FROM.distanceTo(TO) / 0.5) + 10 + GrapplePull.OVERRUN_GRACE_TICKS, ticks);
  }

  @Test
  void aRideWithNoSpeedGetsOnlyTheGrace() {
    assertEquals(GrapplePull.OVERRUN_GRACE_TICKS, RidePath.lifetimeTicks(FROM, TO, 0.0));
  }
}
