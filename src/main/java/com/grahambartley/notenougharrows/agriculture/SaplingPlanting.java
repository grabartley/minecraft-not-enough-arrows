package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.world.BlockEditPermission;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class SaplingPlanting {

  private SaplingPlanting() {}

  public static boolean plant(
      final ServerWorld world,
      final BlockHitResult hit,
      final Item sapling,
      @Nullable final PlayerEntity shooter) {
    if (world == null || hit == null || !(sapling instanceof BlockItem blockItem)) {
      return false;
    }
    final ItemPlacementContext placement =
        new ItemPlacementContext(world, null, Hand.MAIN_HAND, new ItemStack(sapling), hit);
    if (!world.isInBuildLimit(placement.getBlockPos())
        || !BlockEditPermission.allows(world, placement.getBlockPos(), shooter)
        || wouldDisplaceFluid(world, placement, blockItem)) {
      return false;
    }
    return blockItem.place(placement).isAccepted();
  }

  private static boolean wouldDisplaceFluid(
      final ServerWorld world, final ItemPlacementContext placement, final BlockItem sapling) {
    final FluidState there = world.getFluidState(placement.getBlockPos());
    if (there.isEmpty()) {
      return false;
    }
    final BlockState planted = sapling.getBlock().getPlacementState(placement);
    return planted == null || !planted.getFluidState().equals(there);
  }
}
