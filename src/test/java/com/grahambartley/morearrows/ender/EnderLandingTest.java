package com.grahambartley.morearrows.ender;

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
