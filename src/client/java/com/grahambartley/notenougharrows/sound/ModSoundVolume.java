package com.grahambartley.notenougharrows.sound;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.config.ConfigValues;
import com.grahambartley.notenougharrows.config.SoundConfig;
import net.minecraft.util.Identifier;

public final class ModSoundVolume {

  private ModSoundVolume() {}

  public static float adjust(
      final Identifier soundId,
      final float adjusted,
      final float serverVolume,
      final float clientVolume) {
    if (!NotEnoughArrows.MOD_ID.equals(soundId.getNamespace())) {
      return adjusted;
    }
    return adjusted * bounded(serverVolume) * bounded(clientVolume);
  }

  public static float cap(final Identifier soundId, final float volume) {
    return NotEnoughArrows.MOD_ID.equals(soundId.getNamespace()) ? Math.min(volume, 1.0f) : volume;
  }

  private static float bounded(final float volume) {
    return ConfigValues.clampFloat(volume, SoundConfig.VOLUME_MIN, SoundConfig.VOLUME_MAX);
  }
}
