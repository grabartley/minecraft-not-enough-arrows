package com.grahambartley.notenougharrows.sound;

import com.grahambartley.notenougharrows.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ShockBoltSounds {
  private static final Identifier THUNDER = Identifier.ofVanilla("entity.lightning_bolt.thunder");
  private static final Identifier IMPACT = Identifier.ofVanilla("entity.lightning_bolt.impact");

  private ShockBoltSounds() {}

  public static SoundEvent voice(final boolean shockBolt, final SoundEvent sound) {
    if (!shockBolt) {
      return sound;
    }
    if (THUNDER.equals(sound.getId())) {
      return ModSounds.SHOCK_ARROW_THUNDER;
    }
    if (IMPACT.equals(sound.getId())) {
      return ModSounds.SHOCK_ARROW_IMPACT;
    }
    return sound;
  }
}
