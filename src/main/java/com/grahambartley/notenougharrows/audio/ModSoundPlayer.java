package com.grahambartley.notenougharrows.audio;

import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public final class ModSoundPlayer {
  public static final float EXPLOSION_VOLUME = 4.0f;

  private ModSoundPlayer() {}

  public static void play(
      final ServerWorld world,
      final Vec3d at,
      final SoundEvent sound,
      final SoundCategory category,
      final float volume,
      final float pitch) {
    play(
        (position, event, channel, loudness, tone) ->
            world.playSound(
                null,
                position.getX(),
                position.getY(),
                position.getZ(),
                event,
                channel,
                loudness,
                tone),
        ServerConfigService.get().sound().volume(),
        at,
        sound,
        category,
        volume,
        pitch);
  }

  static void play(
      final SoundSink sink,
      final float serverVolume,
      final Vec3d at,
      final SoundEvent sound,
      final SoundCategory category,
      final float volume,
      final float pitch) {
    final float scaled = SoundVolume.scale(volume, serverVolume);
    if (scaled <= 0.0f) {
      return;
    }
    sink.play(at, sound, category, scaled, pitch);
  }

  public static void playFrom(
      final Entity source, final SoundEvent sound, final float volume, final float pitch) {
    if (source.isSilent() || !(source.getWorld() instanceof ServerWorld world)) {
      return;
    }
    play(world, source.getPos(), sound, source.getSoundCategory(), volume, pitch);
  }

  public static void playExplosion(
      final ServerWorld world, final Vec3d at, final SoundEvent sound) {
    play(
        world,
        at,
        sound,
        SoundCategory.BLOCKS,
        EXPLOSION_VOLUME,
        explosionPitch(world.getRandom()));
  }

  public static RegistryEntry<SoundEvent> silentExplosion() {
    return Registries.SOUND_EVENT.getEntry(SoundEvents.INTENTIONALLY_EMPTY);
  }

  static float explosionPitch(final Random random) {
    return (1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f) * 0.7f;
  }

  @FunctionalInterface
  interface SoundSink {
    void play(Vec3d at, SoundEvent sound, SoundCategory category, float volume, float pitch);
  }
}
