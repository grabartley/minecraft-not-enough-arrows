package com.grahambartley.notenougharrows.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class VolleySpreadTest {
  private static final double TOLERANCE = 1.0e-6;
  private static final Vec3d FIRED = new Vec3d(3.0, 0.0, 0.0);

  @ParameterizedTest
  @ValueSource(ints = {2, 3, 5, 12})
  void producesExactlyTheFragmentCountItWasAskedFor(final int count) {
    assertEquals(count, VolleySpread.fragmentVelocities(FIRED, count, 10.0f).size());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void producesNothingWhenThereAreNoFragmentsToMake(final int count) {
    assertTrue(VolleySpread.fragmentVelocities(FIRED, count, 10.0f).isEmpty());
  }

  @Test
  void everyFragmentCarriesTheSpeedTheOriginalHad() {
    for (final Vec3d fragment : VolleySpread.fragmentVelocities(FIRED, 5, 10.0f)) {
      assertEquals(FIRED.length(), fragment.length(), TOLERANCE);
    }
  }

  @ParameterizedTest
  @ValueSource(floats = {5.0f, 10.0f, 45.0f})
  void everyFragmentLeavesAtExactlyTheConfiguredSpreadAngle(final float spreadDegrees) {
    for (final Vec3d fragment : VolleySpread.fragmentVelocities(FIRED, 6, spreadDegrees)) {
      final double cosine = fragment.normalize().dotProduct(FIRED.normalize());
      assertEquals(Math.cos(Math.toRadians(spreadDegrees)), cosine, TOLERANCE);
    }
  }

  @Test
  void aSpreadOfNothingSendsEveryFragmentStraightDownTheOriginalLine() {
    for (final Vec3d fragment : VolleySpread.fragmentVelocities(FIRED, 4, 0.0f)) {
      assertEquals(0.0, fragment.subtract(FIRED).length(), TOLERANCE);
    }
  }

  @Test
  void fragmentsFanOutAroundTheOriginalRatherThanStackingOnOneSide() {
    final List<Vec3d> fragments = VolleySpread.fragmentVelocities(FIRED, 4, 20.0f);

    final Vec3d sum =
        fragments.stream().reduce(Vec3d.ZERO, Vec3d::add).multiply(1.0 / fragments.size());

    assertEquals(0.0, sum.normalize().subtract(FIRED.normalize()).length(), TOLERANCE);
  }

  @Test
  void aVerticalShotStillFansOutRatherThanCollapsing() {
    final List<Vec3d> fragments =
        VolleySpread.fragmentVelocities(new Vec3d(0.0, 3.0, 0.0), 4, 15.0f);

    assertEquals(4, fragments.size());
    for (final Vec3d fragment : fragments) {
      assertEquals(3.0, fragment.length(), TOLERANCE);
    }
  }

  @Test
  void anArrowWithNoVelocityHasNothingToSplitInto() {
    assertTrue(VolleySpread.fragmentVelocities(Vec3d.ZERO, 5, 10.0f).isEmpty());
    assertTrue(VolleySpread.fragmentVelocities(null, 5, 10.0f).isEmpty());
  }

  @ParameterizedTest
  @CsvSource({"10.0, 0.1, 5, 1.0", "10.0, 0.2, 4, 2.0", "6.0, 0.25, 4, 1.5"})
  void eachFragmentCarriesTheConfiguredShareOfTheOriginalDamage(
      final double base, final float share, final int count, final double expected) {
    assertEquals(expected, VolleySpread.fragmentDamage(base, share, count), TOLERANCE);
  }

  @ParameterizedTest
  @CsvSource({"2, 0.4", "5, 0.4", "12, 1.0", "5, 1.0", "12, 0.05"})
  void thewholeVolleyNeverHitsHarderThanTheArrowItCameFrom(final int count, final float share) {
    final double base = 10.0;

    final double whole = VolleySpread.fragmentDamage(base, share, count) * count;

    assertTrue(
        whole <= base + TOLERANCE,
        count + " fragments at a share of " + share + " would deal " + whole + " against " + base);
  }

  @ParameterizedTest
  @CsvSource({"10.0, 0.0, 5", "10.0, -1.0, 5", "0.0, 0.5, 5", "-5.0, 0.5, 5", "10.0, 0.5, 0"})
  void fragmentsNeverCarryNegativeDamage(final double base, final float share, final int count) {
    assertEquals(0.0, VolleySpread.fragmentDamage(base, share, count), TOLERANCE);
  }

  @Test
  void aShareAboveOneStillNeverBeatsTheArrowItCameFrom() {
    assertEquals(2.0, VolleySpread.fragmentDamage(10.0, 5.0f, 5), TOLERANCE);
  }
}
