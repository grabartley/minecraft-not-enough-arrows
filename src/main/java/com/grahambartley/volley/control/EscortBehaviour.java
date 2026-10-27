package com.grahambartley.volley.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

final class EscortBehaviour {
  private static final double ESCORT_SPEED = 1.25;

  private EscortBehaviour() {}

  static boolean defendSubject(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    final Entity subject = hold.subjectId().map(world::getEntity).orElse(null);
    if (subject == null) {
      return true;
    }
    if (!(subject instanceof LivingEntity defended) || !defended.isAlive()) {
      return false;
    }
    WitherHeads.spare(mob, defended);
    final LivingEntity threat =
        DefenderTargets.threatTo(world, defended, mob, hold.reach()).orElse(null);
    MobAggression.aim(mob, threat);
    if (threat == null) {
      escort(world, mob, defended);
    }
    return true;
  }

  private static void escort(
      final ServerWorld world, final MobEntity mob, final LivingEntity defended) {
    final double squaredDistance = mob.squaredDistanceTo(defended);
    if (mob instanceof EndermanEntity && Escort.hasLostTrack(squaredDistance)) {
      mob.teleport(defended.getX(), defended.getY(), defended.getZ(), true);
      return;
    }
    if (Escort.isCloseEnough(squaredDistance, mob.getWidth())) {
      MobSteering.halt(mob);
    } else if (Escort.shouldCloseIn(squaredDistance, mob.getWidth())
        || MobSteering.fliesAtRandom(mob)) {
      MobSteering.rouse(mob);
      if (MobSteering.needsNewCourse(mob, defended.getPos())) {
        MobSteering.moveTo(mob, defended.getPos(), ESCORT_SPEED);
      }
    }
    MobSteering.keepPace(mob, ESCORT_SPEED);
  }
}
