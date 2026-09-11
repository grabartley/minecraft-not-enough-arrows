package com.grahambartley.notenougharrows.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class BlockPlacement {

  private BlockPlacement() {}

  public static boolean canPlace(
      @Nullable final ServerWorld world,
      @Nullable final BlockPos pos,
      @Nullable final BlockState state,
      @Nullable final PlayerEntity placer) {
    if (world == null || pos == null || state == null || !world.isInBuildLimit(pos)) {
      return false;
    }
    if (!BlockEditPermission.allows(world, pos, placer)) {
      return false;
    }
    if (!world.getBlockState(pos).isAir()) {
      return false;
    }
    return state.canPlaceAt(world, pos);
  }
}
