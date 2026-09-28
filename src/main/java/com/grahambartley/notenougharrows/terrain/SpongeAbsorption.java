package com.grahambartley.notenougharrows.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class SpongeAbsorption {

  private SpongeAbsorption() {}

  public static boolean absorbAt(final ServerWorld world, final BlockPos pos) {
    final BlockState state = world.getBlockState(pos);
    if (!world.getFluidState(pos).isIn(FluidTags.WATER)) {
      return false;
    }
    if (state.getBlock() instanceof FluidDrainable drainable
        && !drainable.tryDrainFluid(null, world, pos, state).isEmpty()) {
      return true;
    }
    if (state.getBlock() instanceof FluidBlock) {
      return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
    }
    if (!isAbsorbedPlant(state)) {
      return false;
    }
    final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
    Block.dropStacks(state, world, pos, blockEntity);
    return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
  }

  private static boolean isAbsorbedPlant(final BlockState state) {
    return state.isOf(Blocks.KELP)
        || state.isOf(Blocks.KELP_PLANT)
        || state.isOf(Blocks.SEAGRASS)
        || state.isOf(Blocks.TALL_SEAGRASS);
  }
}
