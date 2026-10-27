package com.grahambartley.volley.control;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.audio.ModSoundPlayer;
import com.grahambartley.volley.world.ParticleBursts;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;

public final class FrostEffects {
  private static final int BURST_PARTICLES = 40;
  private static final int SHIMMER_PARTICLES = 4;
  private static final int THAW_PARTICLES = 12;
  private static final double BURST_SPEED = 0.08;
  private static final double SHIMMER_SPEED = 0.01;
  private static final float FREEZE_PITCH = 1.0f;
  private static final float THAW_PITCH = 0.8f;

  private FrostEffects() {}

  public static void frozeOver(final ServerWorld world, final LivingEntity target) {
    snow(world, target, BURST_PARTICLES, BURST_SPEED);
    sound(world, target, ModSounds.FROST_ARROW_FREEZE_CRACK, FREEZE_PITCH);
    sound(world, target, ModSounds.FROST_ARROW_FREEZE_SETTLE, 1.0f);
  }

  public static void stillFrozen(final ServerWorld world, final LivingEntity target) {
    snow(world, target, SHIMMER_PARTICLES, SHIMMER_SPEED);
  }

  public static void thawed(final ServerWorld world, final LivingEntity target) {
    world.spawnParticles(
        ParticleTypes.FALLING_WATER,
        target.getX(),
        target.getBodyY(0.5),
        target.getZ(),
        THAW_PARTICLES,
        target.getWidth() / 2.0,
        target.getHeight() / 3.0,
        target.getWidth() / 2.0,
        0.0);
    sound(world, target, ModSounds.FROST_ARROW_THAW, THAW_PITCH);
  }

  private static void snow(
      final ServerWorld world, final LivingEntity target, final int count, final double speed) {
    ParticleBursts.aroundBody(world, target, ParticleTypes.SNOWFLAKE, count, speed);
  }

  private static void sound(
      final ServerWorld world,
      final LivingEntity target,
      final SoundEvent event,
      final float pitch) {
    ModSoundPlayer.play(world, target.getPos(), event, target.getSoundCategory(), 1.0f, pitch);
  }
}
