package com.grahambartley.morearrows.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.ModSounds;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class CountdownMuteTest {
  private static final Identifier ANOTHER_SOUND = Identifier.of("minecraft", "entity.arrow.hit");

  @Test
  void theBeepIsSilencedWhenThePlayerTurnedItOff() {
    assertTrue(CountdownMute.silences(ModSounds.COUNTDOWN_BEEP_ID, false));
  }

  @Test
  void theBeepPlaysWhenThePlayerLeftItOn() {
    assertFalse(CountdownMute.silences(ModSounds.COUNTDOWN_BEEP_ID, true));
  }

  @Test
  void noOtherSoundIsEverSilenced() {
    assertFalse(CountdownMute.silences(ANOTHER_SOUND, false));
    assertFalse(CountdownMute.silences(ANOTHER_SOUND, true));
  }

  @Test
  void anUnidentifiedSoundIsLeftAlone() {
    assertFalse(CountdownMute.silences(null, false));
  }
}
