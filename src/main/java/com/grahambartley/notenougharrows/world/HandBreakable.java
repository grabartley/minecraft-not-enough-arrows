package com.grahambartley.notenougharrows.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class HandBreakable {
  private static final float MIN_BREAKABLE_HARDNESS = 0.0f;

  private HandBreakable() {}

  public static boolean allows(
      @Nullable final ServerWorld world,
      @Nullable final BlockPos pos,
      @Nullable final BlockState state,
      @Nullable final PlayerEntity breaker) {
    if (world == null || pos == null || state == null || !world.isInBuildLimit(pos)) {
      return false;
    }
    if (state.isAir() || state.isReplaceable() || !state.getFluidState().isEmpty()) {
      return false;
    }
    if (state.hasBlockEntity() || state.getCollisionShape(world, pos).isEmpty()) {
      return false;
    }
    if (state.getHardness(world, pos) < MIN_BREAKABLE_HARDNESS) {
      return false;
    }
    return BlockEditPermission.allows(world, pos, breaker);
  }
}
