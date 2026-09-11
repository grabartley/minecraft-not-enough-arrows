package com.grahambartley.morearrows.ender;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class EnderTeleport {
  private static final int PARTICLE_COUNT = 32;
  private static final double PARTICLE_SPREAD = 0.5;
  private static final double PARTICLE_SPEED = 0.1;
  private static final float SOUND_VOLUME = 1.0f;
  private static final float SOUND_PITCH = 1.0f;

  private EnderTeleport() {}

  public static boolean move(
      @Nullable final ServerWorld world,
      @Nullable final Entity subject,
      @Nullable final Vec3d destination) {
    if (world == null || subject == null || destination == null || subject.isRemoved()) {
      return false;
    }

    final Vec3d departure = subject.getPos();
    if (subject.hasVehicle()) {
      subject.requestTeleportAndDismount(
          destination.getX(), destination.getY(), destination.getZ());
    } else {
      subject.requestTeleport(destination.getX(), destination.getY(), destination.getZ());
    }
    subject.setVelocity(Vec3d.ZERO);
    subject.velocityModified = true;
    subject.onLanding();

    final SoundCategory channel =
        subject instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.NEUTRAL;
    announce(world, departure, channel);
    announce(world, destination, channel);
    return true;
  }

  private static void announce(
      final ServerWorld world, final Vec3d at, final SoundCategory channel) {
    world.spawnParticles(
        ParticleTypes.PORTAL,
        at.getX(),
        at.getY(),
        at.getZ(),
        PARTICLE_COUNT,
        PARTICLE_SPREAD,
        PARTICLE_SPREAD,
        PARTICLE_SPREAD,
        PARTICLE_SPEED);
    world.playSound(
        null,
        at.getX(),
        at.getY(),
        at.getZ(),
        SoundEvents.ENTITY_ENDERMAN_TELEPORT,
        channel,
        SOUND_VOLUME,
        SOUND_PITCH);
  }
}
