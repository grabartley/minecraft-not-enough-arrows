package com.grahambartley.morearrows.gravity;

import com.grahambartley.morearrows.world.BlockEditPermission;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class FallingBlocks {
  private static final float MIN_BREAKABLE_HARDNESS = 0.0f;

  private FallingBlocks() {}

  public static boolean canFall(
      @Nullable final ServerWorld world,
      @Nullable final BlockPos pos,
      @Nullable final BlockState state,
      @Nullable final PlayerEntity shooter) {
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
    return BlockEditPermission.allows(world, pos, shooter);
  }

  public static FallingBlockEntity drop(
      final ServerWorld world, final BlockPos pos, final BlockState state) {
    return FallingBlockEntity.spawnFromBlock(world, pos, state);
  }
}
