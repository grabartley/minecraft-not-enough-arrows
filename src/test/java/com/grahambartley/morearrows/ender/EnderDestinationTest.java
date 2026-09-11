package com.grahambartley.morearrows.ender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class EnderDestinationTest {
  private static final Vec3d ORIGIN = new Vec3d(0.5, 64.0, 0.5);
  private static final int MAX_RANGE = 32;
  private static final double BORDER_SIZE = 16.0;

  @ParameterizedTest(name = "{0} blocks away is reachable={1}")
  @CsvSource({"1.0, true", "31.0, true", "32.0, true", "32.5, false", "64.0, false"})
  void aDestinationIsReachableUpToAndIncludingTheConfiguredRange(
      final double distance, final boolean reachable) {
    assertEquals(
        reachable,
        EnderDestination.isReachable(
            wideBorder(), ORIGIN, ORIGIN.add(distance, 0.0, 0.0), MAX_RANGE));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void aRangeOfZeroOrLessPutsEveryDestinationOutOfReach(final int maxRangeBlocks) {
    assertFalse(
        EnderDestination.isReachable(
            wideBorder(), ORIGIN, ORIGIN.add(1.0, 0.0, 0.0), maxRangeBlocks));
  }

  @Test
  void aDestinationOutsideTheWorldBorderIsRefusedEvenWhenItIsInRange() {
    assertFalse(
        EnderDestination.isReachable(
            tightBorder(), ORIGIN, ORIGIN.add(BORDER_SIZE, 0.0, 0.0), MAX_RANGE));
  }

  @Test
  void aDestinationInsideTheWorldBorderIsAllowed() {
    assertTrue(EnderDestination.isReachable(tightBorder(), ORIGIN, ORIGIN, MAX_RANGE));
  }

  @Test
  void aTeleportWithNoBorderToCheckAgainstIsRefused() {
    assertFalse(EnderDestination.isReachable(null, ORIGIN, ORIGIN, MAX_RANGE));
  }

  @Test
  void aTeleportFromNowhereIsRefused() {
    assertFalse(EnderDestination.isReachable(wideBorder(), null, ORIGIN, MAX_RANGE));
  }

  @Test
  void aTeleportToNowhereIsRefused() {
    assertFalse(EnderDestination.isReachable(wideBorder(), ORIGIN, null, MAX_RANGE));
  }

  private static WorldBorder wideBorder() {
    return new WorldBorder();
  }

  private static WorldBorder tightBorder() {
    final WorldBorder border = new WorldBorder();
    border.setCenter(ORIGIN.getX(), ORIGIN.getZ());
    border.setSize(BORDER_SIZE);
    return border;
  }
}
