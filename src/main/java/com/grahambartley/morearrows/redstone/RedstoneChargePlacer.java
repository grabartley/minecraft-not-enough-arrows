package com.grahambartley.morearrows.redstone;

import com.grahambartley.morearrows.ModBlocks;
import com.grahambartley.morearrows.block.RedstoneChargeBlock;
import com.grahambartley.morearrows.world.BlockPlacement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class RedstoneChargePlacer {

  private RedstoneChargePlacer() {}

  public static boolean place(
      final ServerWorld world,
      final BlockPos pos,
      final int power,
      @Nullable final PlayerEntity shooter) {
    if (world == null || pos == null || power <= 0) {
      return false;
    }

    final BlockState state = RedstoneChargeBlock.stateWith(ModBlocks.REDSTONE_CHARGE, power);
    if (!BlockPlacement.canPlace(world, pos, state, shooter)) {
      return false;
    }
    return world.setBlockState(pos, state, Block.NOTIFY_ALL);
  }

  public static boolean clear(final ServerWorld world, final BlockPos pos) {
    if (world == null || pos == null || !isCharge(world, pos)) {
      return false;
    }
    return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
  }

  public static boolean isCharge(final ServerWorld world, final BlockPos pos) {
    return world != null
        && pos != null
        && world.getBlockState(pos).getBlock() instanceof RedstoneChargeBlock;
  }
}
