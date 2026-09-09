package com.grahambartley.morearrows.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public record NockPlacement(float offsetX, float offsetY) {
  private static final float ROTATION_DEGREES = 90.0F;
  private static final float PULL_TICKS = 20.0F;
  private static final float SHORT_PULL = 0.65F;
  private static final float FULL_PULL = 0.9F;
  private static final float PIXEL = 1.0F / 16.0F;
  private static final NockPlacement CHARGED_CROSSBOW = ofPixels(-1, -1);

  public static NockPlacement forBowPull(final int maxUseTime, final int useTimeLeft) {
    final int stage = pullStage((maxUseTime - useTimeLeft) / PULL_TICKS);
    return ofPixels(stage - 1, stage - 1);
  }

  public static NockPlacement forChargedCrossbow() {
    return CHARGED_CROSSBOW;
  }

  public void applyTo(final MatrixStack matrices) {
    matrices.translate(offsetX, offsetY, 0.0F);
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(ROTATION_DEGREES));
  }

  private static int pullStage(final float pull) {
    if (pull >= FULL_PULL) {
      return 2;
    }
    return pull >= SHORT_PULL ? 1 : 0;
  }

  private static NockPlacement ofPixels(final int x, final int y) {
    return new NockPlacement(x * PIXEL, -y * PIXEL);
  }
}
