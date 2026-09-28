package com.grahambartley.notenougharrows.audio;

import com.grahambartley.notenougharrows.config.ConfigValues;

public final class SoundVolume {

  private SoundVolume() {}

  public static float scale(final float volume, final float modVolume) {
    return Math.max(0.0f, volume) * ConfigValues.clampFloat(modVolume, 0.0f, 1.0f);
  }
}
