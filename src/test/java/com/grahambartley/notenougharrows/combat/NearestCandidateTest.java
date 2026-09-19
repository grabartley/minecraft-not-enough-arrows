package com.grahambartley.notenougharrows.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NearestCandidateTest {
  private static final Vec3d ORIGIN = Vec3d.ZERO;
  private static final Function<Vec3d, Vec3d> ITSELF = Function.identity();

  @Test
  void picksTheNearestCandidateInsideTheRadius() {
    final Vec3d near = new Vec3d(2.0, 0.0, 0.0);
    final List<Vec3d> candidates =
        List.of(new Vec3d(5.0, 0.0, 0.0), near, new Vec3d(4.0, 0.0, 0.0));

    assertEquals(Optional.of(near), NearestCandidate.nearest(ORIGIN, candidates, ITSELF, 6.0));
  }

  @Test
  void ignoresEverythingOutsideTheRadius() {
    final List<Vec3d> candidates = List.of(new Vec3d(7.0, 0.0, 0.0), new Vec3d(0.0, 9.0, 0.0));

    assertTrue(NearestCandidate.nearest(ORIGIN, candidates, ITSELF, 6.0).isEmpty());
  }

  @Test
  void aCandidateExactlyOnTheRadiusStillCounts() {
    final Vec3d edge = new Vec3d(6.0, 0.0, 0.0);

    assertEquals(Optional.of(edge), NearestCandidate.nearest(ORIGIN, List.of(edge), ITSELF, 6.0));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -1.0, -32.0})
  void aRadiusOfNoneMeansTheBoltNeverJumps(final double radius) {
    final List<Vec3d> candidates = List.of(new Vec3d(0.1, 0.0, 0.0));

    assertTrue(NearestCandidate.nearest(ORIGIN, candidates, ITSELF, radius).isEmpty());
  }

  @Test
  void anEmptyFieldHasNothingToJumpTo() {
    assertTrue(NearestCandidate.nearest(ORIGIN, List.of(), ITSELF, 6.0).isEmpty());
  }

  @Test
  void picksAtMostOneSoTheBoltNeverChains() {
    final List<Vec3d> crowd =
        List.of(new Vec3d(1.0, 0.0, 0.0), new Vec3d(1.5, 0.0, 0.0), new Vec3d(2.0, 0.0, 0.0));

    assertEquals(
        Optional.of(new Vec3d(1.0, 0.0, 0.0)),
        NearestCandidate.nearest(ORIGIN, crowd, ITSELF, 6.0));
  }

  @Test
  void keepsTheFirstOfTwoEquallyCloseCandidates() {
    final Vec3d first = new Vec3d(3.0, 0.0, 0.0);
    final Vec3d second = new Vec3d(-3.0, 0.0, 0.0);

    assertEquals(
        Optional.of(first), NearestCandidate.nearest(ORIGIN, List.of(first, second), ITSELF, 6.0));
  }

  @Test
  void skipsACandidateWithNoPosition() {
    final Vec3d real = new Vec3d(4.0, 0.0, 0.0);
    final List<String> candidates = java.util.Arrays.asList("missing", "real");

    assertEquals(
        Optional.of("real"),
        NearestCandidate.nearest(
            ORIGIN, candidates, name -> "real".equals(name) ? real : null, 6.0));
  }

  @Test
  void aMissingOriginJumpsNowhere() {
    assertTrue(
        NearestCandidate.nearest(null, List.of(new Vec3d(1.0, 0.0, 0.0)), ITSELF, 6.0).isEmpty());
  }
}
