package com.grahambartley.notenougharrows.audio;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public final class ModExplosion {
  private ModExplosion() {}

  public static void create(
      final ServerWorld world,
      @Nullable final Entity source,
      final ExplosionBehavior behavior,
      final Vec3d at,
      final float power,
      final World.ExplosionSourceType sourceType,
      final ParticleEffect particle,
      final ParticleEffect emitterParticle,
      final SoundEvent sound) {
    world.createExplosion(
        source,
        null,
        behavior,
        at.getX(),
        at.getY(),
        at.getZ(),
        power,
        false,
        sourceType,
        particle,
        emitterParticle,
        Registries.SOUND_EVENT.getEntry(SoundEvents.INTENTIONALLY_EMPTY));
    ModSoundPlayer.play(world, at, sound, SoundCategory.BLOCKS, 1.0f, pitch(world.getRandom()));
  }

  static float pitch(final Random random) {
    return (1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f) * 0.7f;
  }
}
