package com.grahambartley.volley.sound;

import com.grahambartley.volley.ModSounds;
import net.minecraft.util.Identifier;

public final class CountdownMute {

  private CountdownMute() {}

  public static boolean silences(final Identifier soundId, final boolean playCountdownSound) {
    return !playCountdownSound && ModSounds.COUNTDOWN_BEEP_ID.equals(soundId);
  }
}
