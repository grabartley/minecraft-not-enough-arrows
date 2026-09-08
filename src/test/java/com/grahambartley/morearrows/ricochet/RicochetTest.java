package com.grahambartley.morearrows.ricochet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class RicochetTest {
  private static final double TOLERANCE = 1.0e-9;
  private static final Vec3d EASTWARD = new Vec3d(1.0, 0.0, 0.0);

  @ParameterizedTest
  @CsvSource({"0, 3, true", "2, 3, true", "3, 3, false", "4, 3, false", "0, 0, false"})
  void anArrowBouncesUntilItHasUsedItsConfiguredCount(
      final int bouncesUsed, final int bounceCount, final boolean expected) {
    assertEquals(expected, Ricochet.canBounce(bouncesUsed, bounceCount));
  }

  @Test
  void aNegativeBounceTallyNeverBounces() {
    assertFalse(Ricochet.canBounce(-1, 3));
  }

  @Test
  void aHeadOnHitComesStraightBack() {
    final Vec3d deflected = Ricochet.deflect(EASTWARD, Direction.WEST);

    assertEquals(-Ricochet.SPEED_RETENTION, deflected.x, TOLERANCE);
    assertEquals(0.0, deflected.y, TOLERANCE);
    assertEquals(0.0, deflected.z, TOLERANCE);
  }

  @Test
  void aGlancingHitKeepsTheComponentsAlongTheSurface() {
    final Vec3d deflected = Ricochet.deflect(new Vec3d(1.0, -2.0, 3.0), Direction.UP);

    assertEquals(Ricochet.SPEED_RETENTION, deflected.x, TOLERANCE);
    assertEquals(2.0 * Ricochet.SPEED_RETENTION, deflected.y, TOLERANCE);
    assertEquals(3.0 * Ricochet.SPEED_RETENTION, deflected.z, TOLERANCE);
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void everySurfaceReflectsAnArrowFlyingIntoIt(final Direction surface) {
    final Vec3d approach = Vec3d.of(surface.getVector()).multiply(-1.0);

    final Vec3d deflected = Ricochet.deflect(approach, surface);

    assertTrue(
        deflected.dotProduct(Vec3d.of(surface.getVector())) > 0.0,
        "A hit on " + surface + " should send the arrow back out of the surface");
  }

  @ParameterizedTest
  @EnumSource(Direction.class)
  void aBounceOffAnySurfaceIsPredictableEnoughToAimWith(final Direction surface) {
    final Vec3d velocity = new Vec3d(1.0, -2.0, 3.0);

    assertEquals(
        Ricochet.deflect(velocity, surface),
        Ricochet.deflect(velocity, surface),
        "surface " + surface);
  }

  @Test
  void aBounceCostsTheArrowAFifthOfItsSpeed() {
    final Vec3d velocity = new Vec3d(0.6, -0.8, 0.0);

    assertEquals(
        velocity.length() * Ricochet.SPEED_RETENTION,
        Ricochet.deflect(velocity, Direction.UP).length(),
        TOLERANCE);
  }

  @Test
  void anArrowAlreadyLeavingTheSurfaceIsNotTurnedBackIntoIt() {
    final Vec3d deflected = Ricochet.deflect(EASTWARD, Direction.EAST);

    assertTrue(deflected.x > 0.0, "An arrow already moving away should keep its direction");
  }

  @Test
  void aMissingVelocityOrSurfaceDeflectsNothing() {
    assertEquals(Vec3d.ZERO, Ricochet.deflect(null, Direction.UP));
    assertEquals(Vec3d.ZERO, Ricochet.deflect(EASTWARD, null));
  }

  @Test
  void anArrowIsMovedClearOfTheSurfaceItBouncedOff() {
    final Vec3d impact = new Vec3d(4.0, 64.0, -2.0);

    assertEquals(
        impact.add(0.0, Ricochet.SURFACE_CLEARANCE, 0.0), Ricochet.clearOf(impact, Direction.UP));
  }

  @Test
  void aMissingImpactOrSurfaceLeavesThePositionAlone() {
    final Vec3d impact = new Vec3d(4.0, 64.0, -2.0);

    assertEquals(Vec3d.ZERO, Ricochet.clearOf(null, Direction.UP));
    assertEquals(impact, Ricochet.clearOf(impact, null));
  }

  @ParameterizedTest
  @ValueSource(doubles = {2.0, 6.5})
  void damageIsUnchangedWhenBouncesRetainIt(final double damage) {
    assertEquals(damage, Ricochet.damageAfterBounce(damage, true), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(doubles = {2.0, 6.5})
  void damageFallsWithTheSpeedWhenBouncesDoNotRetainIt(final double damage) {
    assertEquals(
        damage * Ricochet.SPEED_RETENTION, Ricochet.damageAfterBounce(damage, false), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -1.0})
  void anArrowThatDealsNoDamageStaysAtNone(final double damage) {
    assertEquals(0.0, Ricochet.damageAfterBounce(damage, true), TOLERANCE);
    assertEquals(0.0, Ricochet.damageAfterBounce(damage, false), TOLERANCE);
  }
}
