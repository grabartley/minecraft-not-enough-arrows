package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.world.StackHandover;
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
      StackHandover.drop(world, whereItIs, returning);
      return Outcome.DROPPED_WHERE_IT_FELL;
    }
    if (shooter instanceof PlayerEntity player) {
      player.getInventory().insertStack(returning);
      if (returning.isEmpty()) {
        return Outcome.GRANTED;
      }
    }
    StackHandover.drop(world, shooter.getPos(), returning);
    return Outcome.DROPPED_AT_FEET;
  }

  public static boolean isHome(final ServerWorld world, @Nullable final LivingEntity shooter) {
    return shooter != null
        && shooter.isAlive()
        && !shooter.isRemoved()
        && shooter.getWorld() == world;
  }
}
