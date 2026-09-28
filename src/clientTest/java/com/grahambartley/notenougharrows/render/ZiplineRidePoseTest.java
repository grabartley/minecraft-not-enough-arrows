package com.grahambartley.notenougharrows.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.MathHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ZiplineRidePoseTest {
  private static final float OVERHEAD_TOLERANCE = 0.5f;

  @ParameterizedTest
  @ValueSource(floats = {0.0f, 3.7f, 12.0f, 40.5f, 1000.0f})
  void theArmsAreRaisedOverheadGrippingTheCable(final float progress) {
    final ZiplineRidePose pose = ZiplineRidePose.at(progress);

    assertTrue(
        Math.abs(pose.armPitch() + MathHelper.PI) < OVERHEAD_TOLERANCE,
        "Arms should point up, pitch " + pose.armPitch());
    assertEquals(ZiplineRidePose.GRIP, pose.armRoll());
  }

  @Test
  void theLegsSwingAsTheRiderTravels() {
    assertNotEquals(
        ZiplineRidePose.at(0.0f).rightLegPitch(), ZiplineRidePose.at(8.0f).rightLegPitch());
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, 5.0f, 17.3f, 99.0f})
  void theLegSwingStaysAGentleDangle(final float progress) {
    final ZiplineRidePose pose = ZiplineRidePose.at(progress);
    final float reach = ZiplineRidePose.LEG_SWING + ZiplineRidePose.KICK;

    assertTrue(Math.abs(pose.rightLegPitch()) <= reach + 1.0e-6f);
    assertTrue(Math.abs(pose.leftLegPitch()) <= reach + 1.0e-6f);
  }

  @Test
  void theLegsKickOutOfStepWithEachOther() {
    final ZiplineRidePose pose = ZiplineRidePose.at(3.0f);

    assertNotEquals(pose.rightLegPitch(), pose.leftLegPitch());
  }

  @Test
  void theLegsHangSlightlyApart() {
    assertEquals(ZiplineRidePose.LEG_SPREAD, ZiplineRidePose.at(0.0f).legRoll());
  }
}
