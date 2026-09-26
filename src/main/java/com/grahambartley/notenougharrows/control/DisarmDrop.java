package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class DisarmDrop {
  public static final double SPEED_PER_BLOCK = 0.09;
  public static final double ARC_LIFT = 0.25;

  private static final double THROW_DROP = 0.2;

  private DisarmDrop() {}

  public static boolean reaches(final LivingEntity target, final boolean affectsPlayers) {
    return target != null
        && target.isAlive()
        && (affectsPlayers || !(target instanceof PlayerEntity));
  }

  public static Vec3d throwVelocity(final float facingYaw, final double throwDistance) {
    if (throwDistance <= 0.0) {
      return Vec3d.ZERO;
    }
    final double radians = Math.toRadians(facingYaw);
    final double speed = throwDistance * SPEED_PER_BLOCK;
    return new Vec3d(-Math.sin(radians) * speed, ARC_LIFT, Math.cos(radians) * speed);
  }

  public static boolean disarm(
      final ServerWorld world,
      final LivingEntity target,
      final boolean affectsPlayers,
      final double throwDistance) {
    if (world == null || !reaches(target, affectsPlayers)) {
      return false;
    }

    final ItemStack held = target.getStackInHand(Hand.MAIN_HAND);
    if (held.isEmpty()) {
      return false;
    }

    final ItemEntity dropped =
        new ItemEntity(
            world, target.getX(), target.getEyeY() - THROW_DROP, target.getZ(), held.copy());
    dropped.setVelocity(throwVelocity(target.getYaw(), throwDistance));
    dropped.setToDefaultPickupDelay();
    if (!world.spawnEntity(dropped)) {
      return false;
    }
    target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    letTheTargetFetchItBack(target);
    return true;
  }

  private static void letTheTargetFetchItBack(final LivingEntity target) {
    if (target instanceof MobEntity mob) {
      mob.setCanPickUpLoot(true);
    }
  }
}
