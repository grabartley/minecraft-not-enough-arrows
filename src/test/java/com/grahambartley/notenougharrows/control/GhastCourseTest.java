package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class GhastCourseTest {
  private static final double GHAST_HEIGHT = 4.0;
  private static final Predicate<Vec3d> OPEN_SKY = position -> true;

  @Test
  void fliesStraightAtAnOpenDestination() {
    final Vec3d destination = new Vec3d(0.0, 6.0, 0.0);
    assertEquals(
        Optional.of(destination),
        GhastCourse.plan(new Vec3d(15.0, 5.0, 0.0), destination, GHAST_HEIGHT, OPEN_SKY));
  }

  @Test
  void climbsOverTheDefendedMobWhenItsFeetAreBlocked() {
    final Predicate<Vec3d> pillarUnderTheCow = position -> position.x > 3.0 || position.y >= 6.0;
    final Vec3d cow = new Vec3d(0.0, 6.0, 0.0);
    assertEquals(
        Optional.of(new Vec3d(0.0, 10.0, 0.0)),
        GhastCourse.plan(new Vec3d(15.0, 5.0, 0.0), cow, GHAST_HEIGHT, pillarUnderTheCow));
  }

  @Test
  void risesFirstWhenBothStraightLinesClipAWall() {
    final Predicate<Vec3d> wallBelowTen =
        position -> position.y >= 10.0 || position.x > 8.0 || position.x < 6.0;
    final Vec3d from = new Vec3d(15.0, 5.0, 0.0);
    assertEquals(
        Optional.of(new Vec3d(15.0, 10.0, 0.0)),
        GhastCourse.plan(from, new Vec3d(0.0, 6.0, 0.0), GHAST_HEIGHT, wallBelowTen));
  }

  @Test
  void crossesFirstWhenTheSkyAboveIsBlocked() {
    final Predicate<Vec3d> lowCeilingThenOpen = position -> position.y < 6.0 || position.x < 5.0;
    final Vec3d from = new Vec3d(15.0, 5.0, 0.0);
    assertEquals(
        Optional.of(new Vec3d(0.0, 5.0, 0.0)),
        GhastCourse.plan(from, new Vec3d(0.0, 7.0, 0.0), GHAST_HEIGHT, lowCeilingThenOpen));
  }

  @Test
  void fliesAsFarAsItCanWhenNoWaypointIsClear() {
    final Predicate<Vec3d> boxedIn = position -> position.x > 11.5 && position.y < 7.0;
    final Optional<Vec3d> course =
        GhastCourse.plan(
            new Vec3d(15.0, 5.0, 0.0), new Vec3d(0.0, 6.0, 0.0), GHAST_HEIGHT, boxedIn);
    assertTrue(course.isPresent());
    assertTrue(course.get().x < 15.0 && course.get().x > 11.5);
  }

  @Test
  void holdsWhenEvenTheFirstStepIsBlocked() {
    assertEquals(
        Optional.empty(),
        GhastCourse.plan(
            new Vec3d(15.0, 5.0, 0.0), new Vec3d(0.0, 6.0, 0.0), GHAST_HEIGHT, position -> false));
  }

  @Test
  void staysPutWhenItHasAlreadyArrived() {
    final Vec3d destination = new Vec3d(0.0, 6.0, 0.0);
    assertEquals(
        Optional.of(destination),
        GhastCourse.plan(new Vec3d(0.3, 6.0, 0.0), destination, GHAST_HEIGHT, OPEN_SKY));
  }

  @Test
  void neverPicksAWaypointItIsAlreadyAt() {
    final Vec3d from = new Vec3d(0.0, 10.0, 0.0);
    final Predicate<Vec3d> onlyHere = position -> position.equals(from);
    assertEquals(
        Optional.empty(), GhastCourse.plan(from, new Vec3d(0.0, 6.0, 0.0), GHAST_HEIGHT, onlyHere));
  }

  @Test
  void sweepsTheSameStepsAsVanillaGhastMovement() {
    final Vec3d from = Vec3d.ZERO;
    final Vec3d to = new Vec3d(3.5, 0.0, 0.0);
    assertTrue(GhastCourse.isClear(from, to, position -> position.x < 3.5));
    assertFalse(GhastCourse.isClear(from, to, position -> position.x < 2.5));
  }
}
