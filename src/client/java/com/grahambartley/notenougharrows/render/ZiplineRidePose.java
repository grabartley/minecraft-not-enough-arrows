package com.grahambartley.notenougharrows.render;

import net.minecraft.util.math.MathHelper;

public record ZiplineRidePose(
    float armPitch, float armRoll, float rightLegPitch, float leftLegPitch, float legRoll) {
  public static final float ARMS_OVERHEAD = -MathHelper.PI + 0.2f;
  public static final float ARM_SWING = 0.08f;
  public static final float GRIP = 0.15f;
  public static final float SWING_RATE = 0.2f;
  public static final float LEG_SWING = 0.35f;
  public static final float KICK_RATE = 0.45f;
  public static final float KICK = 0.12f;
  public static final float LEG_SPREAD = 0.06f;

  public static ZiplineRidePose at(final float animationProgress) {
    final float swing = MathHelper.sin(animationProgress * SWING_RATE);
    final float kick = MathHelper.sin(animationProgress * KICK_RATE) * KICK;
    return new ZiplineRidePose(
        ARMS_OVERHEAD + swing * ARM_SWING,
        GRIP,
        swing * LEG_SWING + kick,
        swing * LEG_SWING - kick,
        LEG_SPREAD);
  }
}
