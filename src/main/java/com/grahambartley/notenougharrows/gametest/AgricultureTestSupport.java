package com.grahambartley.notenougharrows.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class AgricultureTestSupport {

  private AgricultureTestSupport() {}

  static void plantWheat(final TestContext context, final BlockPos crop, final int age) {
    context.setBlockState(
        crop.down(), Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7));
    context.setBlockState(crop, ((CropBlock) Blocks.WHEAT).withAge(age));
  }

  static void fillInventory(final PlayerEntity player) {
    final PlayerInventory inventory = player.getInventory();
    for (int slot = 0; slot < inventory.main.size(); slot++) {
      inventory.main.set(slot, new ItemStack(Items.STONE, Items.STONE.getMaxCount()));
    }
  }
}
