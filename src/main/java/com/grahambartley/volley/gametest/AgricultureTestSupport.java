package com.grahambartley.volley.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class AgricultureTestSupport {

  private AgricultureTestSupport() {}

  static void plantWheat(final TestContext context, final BlockPos crop, final int age) {
    context.setBlockState(
        crop.down(), Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7));
    context.setBlockState(crop, ((CropBlock) Blocks.WHEAT).withAge(age));
  }
}
