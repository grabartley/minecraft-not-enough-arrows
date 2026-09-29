package com.grahambartley.notenougharrows.audio;

import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public final class ModSoundPlayer {
  private static final float BLOCKS_PER_UNIT_VOLUME = 16.0f;
  public static final int LANDING_RANGE_BLOCKS = 48;
  public static final float LANDING_VOLUME = LANDING_RANGE_BLOCKS / BLOCKS_PER_UNIT_VOLUME;

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

  public static void playLanding(final Entity source, final SoundEvent sound) {
    playFrom(source, sound, LANDING_VOLUME, 1.0f);
  }

  public static void playFrom(
      final Entity source, final SoundEvent sound, final float volume, final float pitch) {
    if (source.isSilent() || !(source.getWorld() instanceof ServerWorld world)) {
      return;
    }
    play(world, source.getPos(), sound, source.getSoundCategory(), volume, pitch);
  }

  public static void playTo(
      final ServerPlayerEntity listener,
      final SoundEvent sound,
      final float volume,
      final float pitch) {
    play(
        (position, event, channel, loudness, tone) ->
            listener.playSoundToPlayer(event, channel, loudness, tone),
        ServerConfigService.get().sound().volume(),
        listener.getPos(),
        sound,
        SoundCategory.PLAYERS,
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
    if (serverVolume <= 0.0f || volume <= 0.0f) {
      return;
    }
    sink.play(at, sound, category, volume, pitch);
  }

  @FunctionalInterface
  interface SoundSink {
    void play(Vec3d at, SoundEvent sound, SoundCategory category, float volume, float pitch);
  }
}
