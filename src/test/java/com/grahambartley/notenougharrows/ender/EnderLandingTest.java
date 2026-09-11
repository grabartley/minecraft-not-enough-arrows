package com.grahambartley.notenougharrows.ender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EnderLandingTest {
  private static final Vec3d ANCHOR = new Vec3d(10.0, 64.0, -4.0);

  private static List<Vec3d> candidates() {
    return EnderLanding.candidates(ANCHOR);
  }

  @Test
  void offersTheAnchorItselfFirst() {
    assertEquals(ANCHOR, candidates().get(0));
  }

  @Test
  void offersTheAnchorAndEveryNeighbouringColumn() {
    assertEquals(9, candidates().size());
  }

  @Test
  void offersTheNearestNeighbourBeforeADiagonalOne() {
    final List<Vec3d> candidates = candidates();
    final int orthogonal = candidates.indexOf(ANCHOR.add(1.0, 0.0, 0.0));
    final int diagonal = candidates.indexOf(ANCHOR.add(1.0, 0.0, 1.0));

    assertTrue(
        orthogonal < diagonal,
        "A neighbour one block away should be offered before one 1.41 blocks away");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("candidates")
  void offersNoCandidateNearerThanTheOneBeforeIt(final Vec3d candidate) {
    final List<Vec3d> candidates = candidates();
    final int at = candidates.indexOf(candidate);

    assertTrue(
        at == 0
            || ANCHOR.squaredDistanceTo(candidates.get(at - 1))
                <= ANCHOR.squaredDistanceTo(candidate),
        "Candidates should run nearest first");
  }

  @Test
  void offersNoPositionTwice() {
    assertEquals(candidates().size(), new HashSet<>(candidates()).size());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("candidates")
  void keepsEveryCandidateOnTheAnchorsOwnLevel(final Vec3d candidate) {
    assertEquals(ANCHOR.getY(), candidate.getY());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("candidates")
  void keepsEveryCandidateWithinOneBlockOfTheAnchor(final Vec3d candidate) {
    assertTrue(Math.abs(candidate.getX() - ANCHOR.getX()) <= EnderLanding.NEIGHBOUR_OFFSET);
    assertTrue(Math.abs(candidate.getZ() - ANCHOR.getZ()) <= EnderLanding.NEIGHBOUR_OFFSET);
  }

  @Test
  void refusesToLandAroundNowhere() {
    assertThrows(NullPointerException.class, () -> EnderLanding.candidates(null));
  }
}
