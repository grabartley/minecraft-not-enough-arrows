package com.grahambartley.volley.gametest;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.border.WorldBorder;

final class TerrainTestSupport {
  static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  static final BlockPos CENTER = new BlockPos(3, 3, 3);
  static final int TICK_LIMIT = 20;

  private static final double NEARBY = 4.0;
  private static final double FAR_AWAY = 100_000.0;
  private static final double TINY_BORDER = 16.0;

  private TerrainTestSupport() {}

  static void withTheBorderElsewhere(final TestContext context, final Runnable edit) {
    final BlockPos origin = context.getAbsolutePos(BlockPos.ORIGIN);
    withTheBorderAt(
        context,
        origin.getX() + FAR_AWAY,
        origin.getZ() + FAR_AWAY,
        TINY_BORDER,
        () -> {
          context.assertFalse(
              context.getWorld().getWorldBorder().contains(context.getAbsolutePos(CENTER)),
              "The test should stand outside the moved world border");
          edit.run();
        });
  }

  static void withTheBorderAt(
      final TestContext context,
      final double centerX,
      final double centerZ,
      final double size,
      final Runnable edit) {
    final WorldBorder border = context.getWorld().getWorldBorder();
    final double previousX = border.getCenterX();
    final double previousZ = border.getCenterZ();
    final double previousSize = border.getSize();
    try {
      border.setCenter(centerX, centerZ);
      border.setSize(size);
      edit.run();
    } finally {
      border.setSize(previousSize);
      border.setCenter(previousX, previousZ);
    }
  }

  static void fill(final TestContext context, final BlockState state, final BlockPos... relative) {
    for (final BlockPos pos : relative) {
      context.setBlockState(pos, state);
    }
  }

  static int droppedCount(final TestContext context, final Item item) {
    return droppedWithin(context.getTestBox(), context, item);
  }

  static int droppedNearby(final TestContext context, final Item item) {
    return droppedWithin(context.getTestBox().expand(NEARBY), context, item);
  }

  private static int droppedWithin(final Box area, final TestContext context, final Item item) {
    return context
        .getWorld()
        .getEntitiesByClass(ItemEntity.class, area, drop -> drop.getStack().isOf(item))
        .stream()
        .mapToInt(drop -> drop.getStack().getCount())
        .sum();
  }
}
