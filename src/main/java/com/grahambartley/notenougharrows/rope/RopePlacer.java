package com.grahambartley.notenougharrows.rope;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.world.BlockPlacement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class RopePlacer {

  private RopePlacer() {}

  public static List<BlockPos> place(
      final ServerWorld world, final List<BlockPos> column, @Nullable final PlayerEntity placer) {
    if (world == null || column == null || column.isEmpty()) {
      return List.of();
    }

    final List<BlockPos> placed = new ArrayList<>(column.size());
    for (final BlockPos pos : column) {
      if (!canPlaceAt(world, pos, placer)
          || !world.setBlockState(pos, ropeState(), Block.NOTIFY_ALL)) {
        break;
      }
      placed.add(pos.toImmutable());
    }
    return List.copyOf(placed);
  }

  public static boolean canPlaceAt(
      final ServerWorld world, final BlockPos pos, @Nullable final PlayerEntity placer) {
    return BlockPlacement.canPlace(world, pos, ropeState(), placer);
  }

  private static BlockState ropeState() {
    return ModBlocks.ROPE.getDefaultState();
  }
}
