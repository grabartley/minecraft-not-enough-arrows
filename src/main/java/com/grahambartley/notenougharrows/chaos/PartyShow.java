package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.tint.TintChoice;
import java.util.Optional;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

public final class PartyShow {
  private static final float DISC_PITCH = 1.0f;

  private static final int SPARKS = 60;
  private static final int NOTES = 12;
  private static final double SPREAD = 0.6;
  private static final double SPARK_SPEED = 0.25;
  private static final double NOTE_SPREAD = 1.2;

  private PartyShow() {}

  public static Optional<RegistryEntry<JukeboxSong>> songOf(
      final ServerWorld world, final TintChoice disc) {
    final ItemStack stack = Registries.ITEM.get(disc.ingredient()).getDefaultStack();
    return JukeboxSong.getSongEntryFromStack(world.getRegistryManager(), stack);
  }

  public static boolean throwAt(final ServerWorld world, final Vec3d at, final TintChoice disc) {
    if (world == null || at == null || disc == null) {
      return false;
    }
    final Optional<RegistryEntry<JukeboxSong>> song = songOf(world, disc);
    if (song.isEmpty()) {
      return false;
    }
    world.spawnParticles(
        ParticleTypes.FIREWORK, at.x, at.y, at.z, SPARKS, SPREAD, SPREAD, SPREAD, SPARK_SPEED);
    world.spawnParticles(
        ParticleTypes.NOTE,
        at.x,
        at.y + SPREAD,
        at.z,
        NOTES,
        NOTE_SPREAD,
        SPREAD,
        NOTE_SPREAD,
        1.0);
    ModSoundPlayer.play(
        world,
        at,
        ModSounds.PARTY_DISCS.getOrDefault(disc.key(), song.get().value().soundEvent().value()),
        SoundCategory.RECORDS,
        1.0f,
        DISC_PITCH);
    return true;
  }
}
