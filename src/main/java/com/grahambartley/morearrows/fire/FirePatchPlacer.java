package com.grahambartley.morearrows.fire;

import com.grahambartley.morearrows.world.BlockPlacement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class FirePatchPlacer {
  private static final int SEARCH_ABOVE = 1;
  private static final int SEARCH_BELOW = 2;

  private FirePatchPlacer() {}

  public static List<BlockPos> place(
      final ServerWorld world, final List<BlockPos> columns, @Nullable final PlayerEntity igniter) {
    if (world == null || columns == null || columns.isEmpty()) {
      return List.of();
    }

    final List<BlockPos> placed = new ArrayList<>();
    for (final BlockPos column : columns) {
      final BlockPos target = surfaceIn(world, column, igniter);
      if (target != null
          && world.setBlockState(
              target, AbstractFireBlock.getState(world, target), Block.NOTIFY_ALL)) {
        placed.add(target);
      }
    }
    return List.copyOf(placed);
  }

  public static boolean canPlaceAt(
      final ServerWorld world, final BlockPos pos, @Nullable final PlayerEntity igniter) {
    if (world == null || pos == null) {
      return false;
    }
    return BlockPlacement.canPlace(world, pos, AbstractFireBlock.getState(world, pos), igniter);
  }

  public static boolean clear(final ServerWorld world, final BlockPos pos) {
    if (world == null || pos == null) {
      return false;
    }
    if (!(world.getBlockState(pos).getBlock() instanceof AbstractFireBlock)) {
      return false;
    }
    return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
  }

  @Nullable
  private static BlockPos surfaceIn(
      final ServerWorld world, final BlockPos column, @Nullable final PlayerEntity igniter) {
    for (int offsetY = SEARCH_ABOVE; offsetY >= -SEARCH_BELOW; offsetY--) {
      final BlockPos candidate = column.up(offsetY);
      if (canPlaceAt(world, candidate, igniter)) {
        return candidate;
      }
    }
    return null;
  }
}
