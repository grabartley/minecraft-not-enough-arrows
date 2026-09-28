package com.grahambartley.notenougharrows.gravity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class FallingBlocks {

  private FallingBlocks() {}

  public static FallingBlockEntity drop(
      final ServerWorld world, final BlockPos pos, final BlockState state) {
    return FallingBlockEntity.spawnFromBlock(world, pos, state);
  }
}
