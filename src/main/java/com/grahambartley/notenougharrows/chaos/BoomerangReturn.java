package com.grahambartley.notenougharrows.chaos;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class BoomerangReturn {

  public enum Outcome {
    GRANTED,
    DROPPED_AT_FEET,
    DROPPED_WHERE_IT_FELL,
    NOTHING_TO_RETURN
  }

  private BoomerangReturn() {}

  public static Outcome resolve(
      final ServerWorld world,
      final ItemStack arrow,
      @Nullable final LivingEntity shooter,
      final Vec3d whereItIs,
      final boolean recoverable) {
    if (!recoverable || arrow.isEmpty()) {
      return Outcome.NOTHING_TO_RETURN;
    }
    final ItemStack returning = arrow.copy();
    if (!isHome(world, shooter)) {
      drop(world, whereItIs, returning);
      return Outcome.DROPPED_WHERE_IT_FELL;
    }
    if (shooter instanceof PlayerEntity player) {
      player.getInventory().insertStack(returning);
      if (returning.isEmpty()) {
        return Outcome.GRANTED;
      }
    }
    drop(world, shooter.getPos(), returning);
    return Outcome.DROPPED_AT_FEET;
  }

  public static boolean isHome(final ServerWorld world, @Nullable final LivingEntity shooter) {
    return shooter != null
        && shooter.isAlive()
        && !shooter.isRemoved()
        && shooter.getWorld() == world;
  }

  private static void drop(final ServerWorld world, final Vec3d at, final ItemStack stack) {
    final ItemEntity dropped = new ItemEntity(world, at.x, at.y, at.z, stack, 0.0, 0.0, 0.0);
    dropped.setToDefaultPickupDelay();
    world.spawnEntity(dropped);
  }
}
