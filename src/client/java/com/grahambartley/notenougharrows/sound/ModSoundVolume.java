package com.grahambartley.notenougharrows.sound;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.audio.SoundVolume;
import net.minecraft.util.Identifier;

public final class ModSoundVolume {

  private ModSoundVolume() {}

  public static float adjust(final Identifier soundId, final float volume, final float modVolume) {
    if (!NotEnoughArrows.MOD_ID.equals(soundId.getNamespace())) {
      return volume;
    }
    return SoundVolume.scale(volume, modVolume);
  }
}
