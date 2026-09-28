package com.grahambartley.notenougharrows.audio;

import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public final class ModSoundPlayer {

  private ModSoundPlayer() {}

  public static void play(
      final ServerWorld world,
      final Vec3d at,
      final SoundEvent sound,
      final SoundCategory category,
      final float volume,
      final float pitch) {
    final float scaled = SoundVolume.scale(volume, ServerConfigService.get().sound().volume());
    if (scaled <= 0.0f) {
      return;
    }
    world.playSound(null, at.getX(), at.getY(), at.getZ(), sound, category, scaled, pitch);
  }

  public static void playFrom(
      final Entity source, final SoundEvent sound, final float volume, final float pitch) {
    if (source.isSilent() || !(source.getWorld() instanceof ServerWorld world)) {
      return;
    }
    play(world, source.getPos(), sound, source.getSoundCategory(), volume, pitch);
  }
}
