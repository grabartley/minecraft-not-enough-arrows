package com.grahambartley.notenougharrows.sound;

import com.grahambartley.notenougharrows.ModSounds;
import net.minecraft.util.Identifier;

public final class CountdownMute {

  private CountdownMute() {}

  public static boolean silences(final Identifier soundId, final boolean playCountdownSound) {
    return !playCountdownSound && ModSounds.COUNTDOWN_BEEP_ID.equals(soundId);
  }
}
