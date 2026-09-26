package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public final class FrostEffects {
  private static final int BURST_PARTICLES = 40;
  private static final int SHIMMER_PARTICLES = 4;
  private static final int THAW_PARTICLES = 12;
  private static final double BURST_SPEED = 0.08;
  private static final double SHIMMER_SPEED = 0.01;
  private static final float SOUND_VOLUME = 1.0f;
  private static final float FREEZE_PITCH = 1.2f;
  private static final float THAW_PITCH = 0.8f;

  private FrostEffects() {}

  public static void frozeOver(final ServerWorld world, final LivingEntity target) {
    snow(world, target, BURST_PARTICLES, BURST_SPEED);
    sound(world, target, SoundEvents.BLOCK_GLASS_BREAK, FREEZE_PITCH);
    sound(world, target, SoundEvents.BLOCK_POWDER_SNOW_PLACE, 1.0f);
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
    sound(world, target, SoundEvents.BLOCK_POWDER_SNOW_BREAK, THAW_PITCH);
  }

  private static void snow(
      final ServerWorld world, final LivingEntity target, final int count, final double speed) {
    final Vec3d middle = target.getBoundingBox().getCenter();
    world.spawnParticles(
        ParticleTypes.SNOWFLAKE,
        middle.getX(),
        middle.getY(),
        middle.getZ(),
        count,
        target.getWidth() / 2.0,
        target.getHeight() / 2.0,
        target.getWidth() / 2.0,
        speed);
  }

  private static void sound(
      final ServerWorld world,
      final LivingEntity target,
      final SoundEvent event,
      final float pitch) {
    world.playSound(
        null,
        target.getX(),
        target.getY(),
        target.getZ(),
        event,
        target.getSoundCategory(),
        SOUND_VOLUME,
        pitch);
  }
}
