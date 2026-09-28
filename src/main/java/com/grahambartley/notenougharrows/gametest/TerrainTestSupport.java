package com.grahambartley.notenougharrows.gametest;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.border.WorldBorder;

final class TerrainTestSupport {
  static final String TEMPLATE = "not-enough-arrows:fire_pad";
  static final BlockPos CENTER = new BlockPos(3, 3, 3);
  static final int TICK_LIMIT = 20;

  private static final double FAR_AWAY = 100_000.0;
  private static final double TINY_BORDER = 16.0;

  private TerrainTestSupport() {}

  static void withTheBorderElsewhere(final TestContext context, final Runnable edit) {
    final WorldBorder border = context.getWorld().getWorldBorder();
    final double centerX = border.getCenterX();
    final double centerZ = border.getCenterZ();
    final double size = border.getSize();
    final BlockPos origin = context.getAbsolutePos(BlockPos.ORIGIN);
    try {
      border.setCenter(origin.getX() + FAR_AWAY, origin.getZ() + FAR_AWAY);
      border.setSize(TINY_BORDER);
      context.assertFalse(
          border.contains(context.getAbsolutePos(CENTER)),
          "The test should stand outside the moved world border");
      edit.run();
    } finally {
      border.setSize(size);
      border.setCenter(centerX, centerZ);
    }
  }

  static void fill(final TestContext context, final BlockState state, final BlockPos... relative) {
    for (final BlockPos pos : relative) {
      context.setBlockState(pos, state);
    }
  }

  static int droppedCount(final TestContext context, final Item item) {
    return context
        .getWorld()
        .getEntitiesByClass(
            ItemEntity.class, context.getTestBox(), drop -> drop.getStack().isOf(item))
        .stream()
        .mapToInt(drop -> drop.getStack().getCount())
        .sum();
  }
}
