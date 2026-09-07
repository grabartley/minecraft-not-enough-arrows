package com.grahambartley.morearrows.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class RopeTestSupport {
  static final String TEMPLATE = "more-arrows:rope_shaft";
  static final BlockPos CEILING = new BlockPos(2, 13, 2);
  static final BlockPos ROPE_HEAD = CEILING.down();
  static final int SHAFT_FLOOR_Y = 3;

  private RopeTestSupport() {}

  static void raiseCeiling(final TestContext context) {
    context.setBlockState(CEILING, Blocks.STONE);
  }
}
