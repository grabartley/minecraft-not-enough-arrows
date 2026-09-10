package com.grahambartley.morearrows.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CountdownGazeTest {
  private static final Vec3d EYE = Vec3d.ZERO;
  private static final Vec3d LOOKING_NORTH = new Vec3d(0.0, 0.0, -1.0);

  @Test
  void somethingStraightAheadIsBeingLookedAt() {
    assertTrue(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(0.0, 0.0, -10.0)));
  }

  @Test
  void somethingBehindIsNot() {
    assertFalse(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(0.0, 0.0, 10.0)));
  }

  @Test
  void somethingOffToTheSideIsNot() {
    assertFalse(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(10.0, 0.0, -10.0)));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.5, 1.0, 1.3})
  void aTargetJustInsideTheToleranceStillCounts(final double sidewaysAtTenBlocks) {
    assertTrue(
        CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(sidewaysAtTenBlocks, 0.0, -10.0)));
  }

  @ParameterizedTest
  @ValueSource(doubles = {2.0, 4.0, 8.0})
  void aTargetOutsideItDoesNot(final double sidewaysAtTenBlocks) {
    assertFalse(
        CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(sidewaysAtTenBlocks, 0.0, -10.0)));
  }

  @Test
  void theToleranceIsAngularSoItNarrowsInWorldTermsWithDistance() {
    assertTrue(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(0.5, 0.0, -10.0)));
    assertFalse(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, new Vec3d(0.5, 0.0, -2.0)));
  }

  @Test
  void somethingBeyondTheMaximumDistanceIsNotShownEvenDeadAhead() {
    assertFalse(
        CountdownGaze.isLookingAt(
            EYE, LOOKING_NORTH, new Vec3d(0.0, 0.0, -(CountdownGaze.MAX_DISTANCE + 1.0))));
  }

  @Test
  void somethingAtTheEyeItselfIsNotShown() {
    assertFalse(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, EYE));
  }

  @Test
  void aMissingEyeLookOrTargetIsNotShown() {
    assertFalse(CountdownGaze.isLookingAt(null, LOOKING_NORTH, new Vec3d(0.0, 0.0, -10.0)));
    assertFalse(CountdownGaze.isLookingAt(EYE, null, new Vec3d(0.0, 0.0, -10.0)));
    assertFalse(CountdownGaze.isLookingAt(EYE, LOOKING_NORTH, null));
  }

  @Test
  void aLookVectorOfNoLengthIsNotShown() {
    assertFalse(CountdownGaze.isLookingAt(EYE, Vec3d.ZERO, new Vec3d(0.0, 0.0, -10.0)));
  }
}
