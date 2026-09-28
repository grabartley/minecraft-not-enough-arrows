package com.grahambartley.notenougharrows.updraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class UpdraftColumnTest {
  private static final Vec3d BASE = new Vec3d(0.5, 64.0, 0.5);
  private static final UpdraftColumn COLUMN = new UpdraftColumn(BASE, 1.5, 12, 0.4f, 200L);

  @Test
  void itHoldsWhatStandsWithinItsRadiusAndBelowItsTop() {
    assertTrue(COLUMN.contains(BASE));
    assertTrue(COLUMN.contains(BASE.add(1.0, 5.0, 1.0)));
    assertTrue(COLUMN.contains(BASE.add(0.0, 11.9, 0.0)));
  }

  @Test
  void itGrantsNothingOutsideItsRadius() {
    assertFalse(COLUMN.contains(BASE.add(1.2, 2.0, 1.2)));
  }

  @Test
  void itStopsLiftingAtItsConfiguredHeight() {
    assertFalse(COLUMN.contains(BASE.add(0.0, 12.0, 0.0)));
  }

  @Test
  void itDoesNotReachDownBelowWhereItOpened() {
    assertTrue(COLUMN.contains(BASE.subtract(0.0, 0.4, 0.0)));
    assertFalse(COLUMN.contains(BASE.subtract(0.0, 0.6, 0.0)));
    assertFalse(COLUMN.contains(null));
  }

  @Test
  void itsBoundsEncloseTheWholeColumnAndNoMore() {
    assertEquals(new Box(-1.0, 63.5, -1.0, 2.0, 76.0, 2.0), COLUMN.bounds());
  }

  @Test
  void itLiftsAtItsStrengthRatherThanTeleporting() {
    final Vec3d lifted = COLUMN.lift(new Vec3d(0.2, -0.5, -0.1));

    assertEquals(new Vec3d(0.2, 0.4f, -0.1), lifted);
  }

  @Test
  void itNeverSlowsSomethingAlreadyRisingFaster() {
    assertEquals(new Vec3d(0.0, 1.0, 0.0), COLUMN.lift(new Vec3d(0.0, 1.0, 0.0)));
  }

  @Test
  void itExpiresOnItsExpiryTick() {
    assertFalse(COLUMN.hasExpired(199L));
    assertTrue(COLUMN.hasExpired(200L));
  }

  @Test
  void itsTopIsItsHeightAboveTheBase() {
    assertEquals(76.0, COLUMN.top());
  }

  @Test
  void negativeShapesAreClampedToNothing() {
    final UpdraftColumn nothing = new UpdraftColumn(BASE, -1.0, -3, -0.2f, 0L);

    assertEquals(0.0, nothing.radius());
    assertEquals(0, nothing.heightBlocks());
    assertEquals(0.0f, nothing.strength());
  }
}
