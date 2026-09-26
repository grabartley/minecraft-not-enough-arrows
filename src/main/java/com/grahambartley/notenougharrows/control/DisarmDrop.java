package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class DisarmDrop {
  public static final double SPEED_PER_BLOCK = 0.09;
  public static final double ARC_LIFT = 0.25;

  public static final int PICKUP_DELAY_TICKS = 60;

  private static final double THROW_DROP = 0.2;

  private DisarmDrop() {}

  public static boolean reaches(final LivingEntity target, final boolean affectsPlayers) {
    return target != null
        && target.isAlive()
        && (affectsPlayers || !(target instanceof PlayerEntity));
  }

  public static Vec3d throwVelocity(final Vec3d awayFromShooter, final double throwDistance) {
    if (throwDistance <= 0.0
        || awayFromShooter == null
        || awayFromShooter.horizontalLengthSquared() <= 0.0) {
      return Vec3d.ZERO;
    }
    final Vec3d flat =
        new Vec3d(awayFromShooter.getX(), 0.0, awayFromShooter.getZ())
            .normalize()
            .multiply(throwDistance * SPEED_PER_BLOCK);
    return new Vec3d(flat.getX(), ARC_LIFT, flat.getZ());
  }

  public static boolean disarm(
      final ServerWorld world,
      final LivingEntity target,
      @Nullable final Vec3d shooterPos,
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
    dropped.setVelocity(throwVelocity(awayFrom(target, shooterPos), throwDistance));
    dropped.setPickupDelay(PICKUP_DELAY_TICKS);
    if (!world.spawnEntity(dropped)) {
      return false;
    }
    target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    DisarmFetchService.letThemFetchItBack(world, target);
    return true;
  }

  private static Vec3d awayFrom(final LivingEntity target, @Nullable final Vec3d shooterPos) {
    if (shooterPos == null) {
      return target.getRotationVec(1.0f);
    }
    return target.getPos().subtract(shooterPos);
  }
}
