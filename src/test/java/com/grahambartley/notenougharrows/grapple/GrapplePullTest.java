package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class GrapplePullTest {
  private static final Vec3d PULLER = new Vec3d(0.0, 64.0, 0.0);
  private static final double TOLERANCE = 1.0e-6;

  @ParameterizedTest
  @CsvSource({"1.0, true", "1.75, true", "1.76, false", "8.0, false"})
  void aPullerArrivesOnceItIsWithinTheArrivalDistance(
      final double distance, final boolean arrived) {
    assertEquals(arrived, GrapplePull.hasArrived(PULLER, PULLER.add(distance, 0.0, 0.0)));
  }

  @ParameterizedTest
  @CsvSource({"31.0, 32, true", "32.0, 32, true", "32.5, 32, false"})
  void anAnchorBeyondTheConfiguredRangeIsOutOfReach(
      final double distance, final int maxRangeBlocks, final boolean inRange) {
    assertEquals(
        inRange, GrapplePull.isWithinRange(PULLER, PULLER.add(distance, 0.0, 0.0), maxRangeBlocks));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -32})
  void aRangeOfZeroOrLessPutsEveryAnchorOutOfReach(final int maxRangeBlocks) {
    assertFalse(GrapplePull.isWithinRange(PULLER, PULLER.add(1.0, 0.0, 0.0), maxRangeBlocks));
  }

  @Test
  void thePullPointsStraightAtTheAnchor() {
    final Vec3d target = PULLER.add(6.0, 8.0, 0.0);

    final Vec3d velocity = GrapplePull.velocity(PULLER, target, 0.5, 0.0);

    assertEquals(0.3, velocity.x, TOLERANCE);
    assertEquals(0.4, velocity.y, TOLERANCE);
    assertEquals(0.0, velocity.z, TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.1, 0.8, 4.0})
  void thePullCarriesExactlyTheConfiguredSpeed(final double speed) {
    final Vec3d velocity = GrapplePull.velocity(PULLER, PULLER.add(-9.0, 12.0, 20.0), speed, 0.0);

    assertEquals(speed, velocity.length(), TOLERANCE);
  }

  @ParameterizedTest
  @CsvSource({"0.08", "0.02", "0.5"})
  void aPullCarriesTheGravityTheClientIsAboutToSubtract(final double gravity) {
    final Vec3d target = PULLER.add(0.0, 20.0, 0.0);

    final Vec3d carried = GrapplePull.velocity(PULLER, target, 0.8, gravity);
    final Vec3d bare = GrapplePull.velocity(PULLER, target, 0.8, 0.0);

    assertEquals(bare.y + gravity, carried.y, TOLERANCE);
    assertEquals(bare.x, carried.x, TOLERANCE);
    assertEquals(bare.z, carried.z, TOLERANCE);
  }

  @Test
  void gravityIsCarriedOnTopOfASidewaysPullRatherThanBendingIt() {
    final Vec3d target = PULLER.add(20.0, 0.0, 0.0);

    final Vec3d carried = GrapplePull.velocity(PULLER, target, 0.8, 0.08);

    assertEquals(0.8, carried.x, TOLERANCE);
    assertEquals(0.08, carried.y, TOLERANCE);
    assertEquals(0.0, carried.z, TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(doubles = {-0.08, -1.0})
  void aPullIsNeverDraggedDownByANegativeGravity(final double gravity) {
    final Vec3d target = PULLER.add(0.0, 20.0, 0.0);

    assertEquals(
        GrapplePull.velocity(PULLER, target, 0.8, 0.0).y,
        GrapplePull.velocity(PULLER, target, 0.8, gravity).y,
        TOLERANCE);
  }

  @Test
  void aPullerThatHasArrivedIsNotEvenGivenTheGravityCarry() {
    assertEquals(Vec3d.ZERO, GrapplePull.velocity(PULLER, PULLER.add(1.0, 0.0, 0.0), 0.8, 0.08));
  }

  @Test
  void aPullerPulledDownwardIsPulledDownward() {
    final Vec3d velocity = GrapplePull.velocity(PULLER, PULLER.add(0.0, -10.0, 0.0), 0.8, 0.0);

    assertEquals(-0.8, velocity.y, TOLERANCE);
  }

  @Test
  void aPullerThatHasArrivedIsNoLongerPulled() {
    assertEquals(Vec3d.ZERO, GrapplePull.velocity(PULLER, PULLER.add(1.0, 0.0, 0.0), 0.8, 0.0));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -0.5})
  void aSpeedOfZeroOrLessPullsNothing(final double speed) {
    assertEquals(Vec3d.ZERO, GrapplePull.velocity(PULLER, PULLER.add(0.0, 20.0, 0.0), speed, 0.0));
  }

  @ParameterizedTest
  @CsvSource({"0, 0.15", "1, 0.30", "4, 0.75", "9, 1.50", "20, 1.50", "200, 1.50"})
  void aPullBuildsUpToItsTopSpeedAndHoldsThere(final int pulledTicks, final double expected) {
    assertEquals(expected, GrapplePull.speedAt(pulledTicks, 1.5, 0.15), TOLERANCE);
  }

  @Test
  void aPullIsSlowestOnTheTickItStarts() {
    final double first = GrapplePull.speedAt(0, 1.5, 0.15);
    final double later = GrapplePull.speedAt(5, 1.5, 0.15);

    assertTrue(first < later, "A grapple should be accelerating, not moving at one flat speed");
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -0.5})
  void aPullWithNoAccelerationNeverGetsMoving(final double acceleration) {
    assertEquals(0.0, GrapplePull.speedAt(10, 1.5, acceleration), TOLERANCE);
  }

  @Test
  void aSessionLivesLongEnoughToCoverTheDistanceItRampsOver() {
    final int ticks = GrapplePull.lifetimeTicks(PULLER, PULLER.add(0.0, 40.0, 0.0), 1.5, 0.15);

    assertTrue(
        ticks > GrapplePull.OVERRUN_GRACE_TICKS,
        "A budget must cover the ramp as well as the grace, was " + ticks);
    assertTrue(
        ticks
            > (int) ((40.0 - GrapplePull.ARRIVAL_DISTANCE) / 1.5) + GrapplePull.OVERRUN_GRACE_TICKS,
        "A ramped pull needs longer than one travelling flat out, was " + ticks);
  }

  @Test
  void aSessionThatAcceleratesHarderNeedsLessTime() {
    final int gentle = GrapplePull.lifetimeTicks(PULLER, PULLER.add(0.0, 40.0, 0.0), 1.5, 0.05);
    final int brisk = GrapplePull.lifetimeTicks(PULLER, PULLER.add(0.0, 40.0, 0.0), 1.5, 1.5);

    assertTrue(brisk < gentle, "Accelerating harder should reach the anchor sooner");
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -1.0})
  void aSessionThatCanNeverCoverTheDistanceStillEnds(final double speed) {
    assertEquals(
        GrapplePull.OVERRUN_GRACE_TICKS,
        GrapplePull.lifetimeTicks(PULLER, PULLER.add(0.0, 100.0, 0.0), speed, 0.15));
  }

  @Test
  void aPullNeedsBothEndsOfTheLine() {
    assertThrows(NullPointerException.class, () -> GrapplePull.hasArrived(null, PULLER));
    assertThrows(NullPointerException.class, () -> GrapplePull.hasArrived(PULLER, null));
  }
}
