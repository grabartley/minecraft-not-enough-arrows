package com.grahambartley.morearrows.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NockPlacementTest {
  private static final int BOW_MAX_USE_TIME = 72000;
  private static final float PIXEL = 1.0F / 16.0F;
  private static final float TOLERANCE = 1.0E-4F;

  private static final int SPRITE_TIP_X = 13;
  private static final int SPRITE_TIP_Y = 2;
  private static final int SPRITE_FLETCHING_X = 2;
  private static final int SPRITE_FLETCHING_Y = 13;

  @ParameterizedTest
  @CsvSource({
    "0, -1, 1",
    "12, -1, 1",
    "13, 0, 0",
    "17, 0, 0",
    "18, 1, -1",
    "20, 1, -1",
    "200, 1, -1",
  })
  void slidesTheArrowOnePixelAtEachPullFractionTheVanillaBowModelBranchesOn(
      final int heldTicks, final float expectedPixelsX, final float expectedPixelsY) {
    final NockPlacement placement = afterHolding(heldTicks);

    assertEquals(expectedPixelsX * PIXEL, placement.offsetX(), TOLERANCE, "offset x");
    assertEquals(expectedPixelsY * PIXEL, placement.offsetY(), TOLERANCE, "offset y");
  }

  @Test
  void readsThePullFromHowLongTheBowHasBeenHeldRatherThanFromItsMaximumUseTime() {
    final NockPlacement shortMax = NockPlacement.forBowPull(40, 40 - 18);
    final NockPlacement bowMax = NockPlacement.forBowPull(BOW_MAX_USE_TIME, BOW_MAX_USE_TIME - 18);

    assertEquals(bowMax, shortMax);
  }

  @Test
  void holdsAChargedCrossbowArrowWhereABarelyDrawnBowHoldsItsOwn() {
    assertEquals(afterHolding(0), NockPlacement.forChargedCrossbow());
  }

  @ParameterizedTest
  @CsvSource({
    "0, 1, 1",
    "13, 2, 2",
    "18, 3, 3",
  })
  void landsTheArrowTipOnTheWeaponPixelTheVanillaNockedArrowTipOccupies(
      final int heldTicks, final float expectedX, final float expectedY) {
    final float[] tip = weaponPixelOf(afterHolding(heldTicks), SPRITE_TIP_X, SPRITE_TIP_Y);

    assertEquals(expectedX, tip[0], TOLERANCE, "tip x");
    assertEquals(expectedY, tip[1], TOLERANCE, "tip y");
  }

  @Test
  void landsTheCrossbowArrowTipOnTheVanillaLoadedArrowTip() {
    final float[] tip =
        weaponPixelOf(NockPlacement.forChargedCrossbow(), SPRITE_TIP_X, SPRITE_TIP_Y);

    assertEquals(1.0F, tip[0], TOLERANCE, "tip x");
    assertEquals(1.0F, tip[1], TOLERANCE, "tip y");
  }

  @Test
  void turnsTheSpriteAQuarterTurnSoItsShaftRunsAlongTheDrawnArrowRatherThanAcrossIt() {
    final NockPlacement placement = afterHolding(13);

    final float[] tip = weaponPixelOf(placement, SPRITE_TIP_X, SPRITE_TIP_Y);
    final float[] fletching = weaponPixelOf(placement, SPRITE_FLETCHING_X, SPRITE_FLETCHING_Y);

    assertEquals(fletching[0] - tip[0], fletching[1] - tip[1], TOLERANCE, "runs at 45 degrees");
    assertEquals(11.0F, fletching[0] - tip[0], TOLERANCE, "fletching sits down and right of tip");
  }

  @ParameterizedTest
  @CsvSource({
    "0.53125, 0.046875",
    "0.46875, -0.046875",
  })
  void standsProudOfTheWeaponOnBothFacesSoNeitherSideCanFightItForDepth(
      final float spriteFace, final float expectedDepth) {
    final float weaponFace = spriteFace - 0.5F;

    final Vector3f drawn =
        drawnAt(afterHolding(13))
            .peek()
            .getPositionMatrix()
            .transformPosition(new Vector3f(0.25F, 0.25F, spriteFace));

    assertEquals(expectedDepth, drawn.z(), TOLERANCE, "arrow face");
    assertTrue(
        Math.abs(drawn.z()) > Math.abs(weaponFace),
        "arrow face " + drawn.z() + " must clear weapon face " + weaponFace);
  }

  private static NockPlacement afterHolding(final int ticks) {
    return NockPlacement.forBowPull(BOW_MAX_USE_TIME, BOW_MAX_USE_TIME - ticks);
  }

  private static float[] weaponPixelOf(
      final NockPlacement placement, final int spriteX, final int spriteY) {
    final Vector3f drawn =
        drawnAt(placement)
            .peek()
            .getPositionMatrix()
            .transformPosition(new Vector3f(pixelCentre(spriteX), pixelCentre(15 - spriteY), 0.5F));

    return new float[] {(drawn.x() + 0.5F) * 16.0F - 0.5F, 15.5F - (drawn.y() + 0.5F) * 16.0F};
  }

  private static MatrixStack drawnAt(final NockPlacement placement) {
    final MatrixStack matrices = new MatrixStack();
    placement.applyTo(matrices);
    matrices.translate(-0.5F, -0.5F, -0.5F);
    return matrices;
  }

  private static float pixelCentre(final int pixel) {
    return (pixel + 0.5F) * PIXEL;
  }
}
