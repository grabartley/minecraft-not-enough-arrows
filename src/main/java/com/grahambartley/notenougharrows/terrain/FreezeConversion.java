package com.grahambartley.notenougharrows.terrain;

import java.util.Optional;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class FreezeConversion {

  private FreezeConversion() {}

  public static Optional<BlockState> frozenFormOf(final BlockState state) {
    if (state.getBlock() instanceof AbstractFireBlock) {
      return Optional.of(Blocks.AIR.getDefaultState());
    }
    final FluidState fluid = state.getFluidState();
    if (state.isOf(Blocks.WATER) && fluid.isOf(Fluids.WATER)) {
      return Optional.of(Blocks.ICE.getDefaultState());
    }
    if (state.isOf(Blocks.LAVA) && fluid.isOf(Fluids.LAVA)) {
      return Optional.of(Blocks.OBSIDIAN.getDefaultState());
    }
    return Optional.empty();
  }

  public static boolean freezeAt(final ServerWorld world, final BlockPos pos) {
    return frozenFormOf(world.getBlockState(pos))
        .filter(frozen -> frozen.isAir() || world.canPlace(frozen, pos, ShapeContext.absent()))
        .map(frozen -> world.setBlockState(pos, frozen, Block.NOTIFY_ALL))
        .orElse(false);
  }
}
