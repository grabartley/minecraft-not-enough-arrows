package com.grahambartley.morearrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  @CsvSource({"16.0, 0.8, 40", "16.0, 1.0, 36", "5.0, 2.0, 23", "0.0, 0.8, 20"})
  void aSessionLivesLongEnoughToCoverTheDistancePlusGrace(
      final double distance, final double speed, final int expectedTicks) {
    assertEquals(
        expectedTicks, GrapplePull.lifetimeTicks(PULLER, PULLER.add(0.0, distance, 0.0), speed));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -1.0})
  void aSessionThatCanNeverCoverTheDistanceStillEnds(final double speed) {
    assertEquals(
        GrapplePull.OVERRUN_GRACE_TICKS,
        GrapplePull.lifetimeTicks(PULLER, PULLER.add(0.0, 100.0, 0.0), speed));
  }

  @ParameterizedTest
  @CsvSource({"0.08, 0.88", "0.0, 0.8", "0.5, 1.3"})
  void aPullUpwardCarriesTheGravityTheClientIsAboutToSubtract(
      final double gravity, final double expectedY) {
    final Vec3d velocity = GrapplePull.velocity(PULLER, PULLER.add(0.0, 20.0, 0.0), 0.8, gravity);

    assertEquals(expectedY, velocity.y, TOLERANCE);
  }

  @Test
  void aPullIsNeverSlowedByANegativeGravity() {
    final Vec3d velocity = GrapplePull.velocity(PULLER, PULLER.add(0.0, 20.0, 0.0), 0.8, -0.5);

    assertEquals(0.8, velocity.y, TOLERANCE);
  }

  @Test
  void aPullNeedsBothEndsOfTheLine() {
    assertThrows(NullPointerException.class, () -> GrapplePull.hasArrived(null, PULLER));
    assertThrows(NullPointerException.class, () -> GrapplePull.hasArrived(PULLER, null));
  }
}
