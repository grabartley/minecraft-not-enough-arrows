package com.grahambartley.volley.world;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class ParticleBursts {

  private ParticleBursts() {}

  public static void aroundBody(
      final ServerWorld world,
      final Entity entity,
      final ParticleEffect particle,
      final int count,
      final double speed) {
    world.spawnParticles(
        particle,
        entity.getX(),
        entity.getBodyY(0.5),
        entity.getZ(),
        count,
        entity.getWidth() / 2.0,
        entity.getHeight() / 2.0,
        entity.getWidth() / 2.0,
        speed);
  }

  public static void atBlockCentre(
      final ServerWorld world,
      final BlockPos pos,
      final ParticleEffect particle,
      final int count,
      final double spread) {
    world.spawnParticles(
        particle,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        count,
        spread,
        spread,
        spread,
        0.0);
  }
}
