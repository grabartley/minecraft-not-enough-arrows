package com.grahambartley.morearrows.gravity;

import com.grahambartley.morearrows.world.BlockEditPermission;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class FallingBlocks {
  private static final float MIN_BREAKABLE_HARDNESS = 0.0f;

  private FallingBlocks() {}

  public static boolean canFall(
      @Nullable final ServerWorld world,
      @Nullable final BlockPos pos,
      @Nullable final PlayerEntity shooter) {
    if (world == null || pos == null || !world.isInBuildLimit(pos)) {
      return false;
    }

    final BlockState state = world.getBlockState(pos);
    if (state.isAir() || state.isReplaceable() || !state.getFluidState().isEmpty()) {
      return false;
    }
    if (state.getCollisionShape(world, pos).isEmpty()) {
      return false;
    }
    if (state.getHardness(world, pos) < MIN_BREAKABLE_HARDNESS) {
      return false;
    }
    return BlockEditPermission.allows(world, pos, shooter);
  }

  public static String blockIdAt(final ServerWorld world, final BlockPos pos) {
    return Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString();
  }

  @Nullable
  public static FallingBlockEntity drop(
      @Nullable final ServerWorld world, @Nullable final BlockPos pos) {
    if (world == null || pos == null) {
      return null;
    }
    return FallingBlockEntity.spawnFromBlock(world, pos, world.getBlockState(pos));
  }
}
