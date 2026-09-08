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
import net.minecraft.util.math.ChunkPos;
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

    final BlockState state = ModBlocks.REDSTONE_CHARGE.stateWith(power);
    if (!BlockPlacement.canPlace(world, pos, state, shooter)) {
      return false;
    }
    return world.setBlockState(pos, state, Block.NOTIFY_ALL);
  }

  public static boolean clear(final ServerWorld world, final BlockPos pos) {
    if (!isCharge(world, pos)) {
      return false;
    }
    return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
  }

  public static boolean isCharge(final ServerWorld world, final BlockPos pos) {
    return isLoaded(world, pos)
        && world.getBlockState(pos).getBlock() instanceof RedstoneChargeBlock;
  }

  private static boolean isLoaded(final ServerWorld world, final BlockPos pos) {
    return world != null && pos != null && world.isChunkLoaded(new ChunkPos(pos).toLong());
  }
}
