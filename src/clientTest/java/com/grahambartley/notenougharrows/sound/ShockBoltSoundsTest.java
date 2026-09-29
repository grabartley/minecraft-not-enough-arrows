package com.grahambartley.notenougharrows.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.grahambartley.notenougharrows.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ShockBoltSoundsTest {
  private static final SoundEvent THUNDER =
      SoundEvent.of(Identifier.ofVanilla("entity.lightning_bolt.thunder"));
  private static final SoundEvent IMPACT =
      SoundEvent.of(Identifier.ofVanilla("entity.lightning_bolt.impact"));
  private static final float VANILLA_THUNDER_VOLUME = 10000.0f;
  private static final SoundEvent OTHER = SoundEvent.of(Identifier.ofVanilla("entity.cow.ambient"));

  @Test
  void aShockBoltThundersInTheModsVoice() {
    assertSame(ModSounds.SHOCK_ARROW_THUNDER, ShockBoltSounds.voice(true, THUNDER));
  }

  @Test
  void aShockBoltStrikesInTheModsVoice() {
    assertSame(ModSounds.SHOCK_ARROW_IMPACT, ShockBoltSounds.voice(true, IMPACT));
  }

  @Test
  void ordinaryLightningKeepsVanillasThunderAndImpact() {
    assertSame(THUNDER, ShockBoltSounds.voice(false, THUNDER));
    assertSame(IMPACT, ShockBoltSounds.voice(false, IMPACT));
  }

  @Test
  void anyOtherSoundIsLeftAlone() {
    assertSame(OTHER, ShockBoltSounds.voice(true, OTHER));
    assertSame(OTHER, ShockBoltSounds.voice(false, OTHER));
  }

  @Test
  void aShockBoltNeverPlaysAboveFullVolume() {
    assertEquals(1.0f, ShockBoltSounds.loudness(true, VANILLA_THUNDER_VOLUME));
  }

  @Test
  void ordinaryLightningKeepsVanillasVolume() {
    assertEquals(VANILLA_THUNDER_VOLUME, ShockBoltSounds.loudness(false, VANILLA_THUNDER_VOLUME));
  }

  @Test
  void aShockBoltAlreadyBelowFullVolumeIsLeftAlone() {
    assertEquals(0.5f, ShockBoltSounds.loudness(true, 0.5f));
  }
}
