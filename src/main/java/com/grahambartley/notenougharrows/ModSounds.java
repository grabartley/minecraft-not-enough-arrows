package com.grahambartley.notenougharrows;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
  public static final Identifier COUNTDOWN_BEEP_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "countdown_beep");

  public static final SoundEvent COUNTDOWN_BEEP = SoundEvent.of(COUNTDOWN_BEEP_ID);

  private ModSounds() {}

  public static void register() {
    Registry.register(Registries.SOUND_EVENT, COUNTDOWN_BEEP_ID, COUNTDOWN_BEEP);
    NotEnoughArrows.LOGGER.info("Registered sound event {}", COUNTDOWN_BEEP_ID);
  }
}
