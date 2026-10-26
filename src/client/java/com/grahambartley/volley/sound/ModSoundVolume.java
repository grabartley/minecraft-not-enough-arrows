package com.grahambartley.volley.sound;

import com.grahambartley.volley.Volley;
import com.grahambartley.volley.config.ConfigValues;
import com.grahambartley.volley.config.SoundConfig;
import net.minecraft.util.Identifier;

public final class ModSoundVolume {

  private ModSoundVolume() {}

  public static float adjust(
      final Identifier soundId,
      final float adjusted,
      final float serverVolume,
      final float clientVolume) {
    if (!Volley.MOD_ID.equals(soundId.getNamespace())) {
      return adjusted;
    }
    return adjusted * bounded(serverVolume) * bounded(clientVolume);
  }

  public static float cap(final Identifier soundId, final float volume) {
    return Volley.MOD_ID.equals(soundId.getNamespace()) ? Math.min(volume, 1.0f) : volume;
  }

  private static float bounded(final float volume) {
    return ConfigValues.clampFloat(volume, SoundConfig.VOLUME_MIN, SoundConfig.VOLUME_MAX);
  }
}
