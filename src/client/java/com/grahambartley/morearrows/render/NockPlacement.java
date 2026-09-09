package com.grahambartley.morearrows.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public record NockPlacement(float offsetX, float offsetY) {
  private static final float ROTATION_DEGREES = 90.0F;
  private static final float DEPTH_SCALE = 1.5F;
  private static final float PULL_TICKS = 20.0F;
  private static final float SHORT_PULL = 0.65F;
  private static final float FULL_PULL = 0.9F;
  private static final float PIXEL = 1.0F / 16.0F;
  private static final int BARELY_DRAWN = 0;
  private static final NockPlacement CHARGED_CROSSBOW = forPullStage(BARELY_DRAWN);

  public static NockPlacement forBowPull(final int maxUseTime, final int useTimeLeft) {
    return forPullStage(pullStage((maxUseTime - useTimeLeft) / PULL_TICKS));
  }

  public static NockPlacement forChargedCrossbow() {
    return CHARGED_CROSSBOW;
  }

  public void applyTo(final MatrixStack matrices) {
    matrices.translate(offsetX, offsetY, 0.0F);
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(ROTATION_DEGREES));
    matrices.scale(1.0F, 1.0F, DEPTH_SCALE);
  }

  private static int pullStage(final float pull) {
    if (pull >= FULL_PULL) {
      return 2;
    }
    return pull >= SHORT_PULL ? 1 : BARELY_DRAWN;
  }

  private static NockPlacement forPullStage(final int stage) {
    return ofSpritePixels(stage - 1, stage - 1);
  }

  private static NockPlacement ofSpritePixels(final int spriteX, final int spriteY) {
    return new NockPlacement(spriteX * PIXEL, -spriteY * PIXEL);
  }
}
