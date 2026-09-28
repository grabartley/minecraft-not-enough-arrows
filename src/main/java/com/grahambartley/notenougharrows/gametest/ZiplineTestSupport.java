package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ZiplineArrowConfig;
import com.grahambartley.notenougharrows.zipline.SpanResult;
import com.grahambartley.notenougharrows.zipline.SpanService;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class ZiplineTestSupport {
  static final BlockPos WEST_ANCHOR = new BlockPos(0, 5, 3);
  static final BlockPos EAST_ANCHOR = new BlockPos(6, 5, 3);
  static final List<BlockPos> CABLE =
      List.of(
          new BlockPos(1, 5, 3),
          new BlockPos(2, 5, 3),
          new BlockPos(3, 5, 3),
          new BlockPos(4, 5, 3),
          new BlockPos(5, 5, 3));
  static final BlockPos MIDDLE_CABLE = new BlockPos(3, 5, 3);
  static final int LONG_LIFETIME_TICKS = 2000;

  private ZiplineTestSupport() {}

  static ZiplineArrowConfig zipline(final int maxSpanBlocks, final int lifetimeTicks) {
    return new ZiplineArrowConfig(
        maxSpanBlocks,
        ZiplineArrowConfig.DEFAULT_PENDING_WINDOW_TICKS,
        ZiplineArrowConfig.DEFAULT_RIDE_SPEED,
        lifetimeTicks);
  }

  static ZiplineArrowConfig longLived() {
    return zipline(ZiplineArrowConfig.DEFAULT_MAX_SPAN_BLOCKS, LONG_LIFETIME_TICKS);
  }

  static void raiseAnchors(final TestContext context) {
    TraversalTestSupport.stone(context, WEST_ANCHOR, EAST_ANCHOR);
  }

  static SpanResult stringAcross(
      final TestContext context, final PlayerEntity shooter, final ZiplineArrowConfig zipline) {
    raiseAnchors(context);
    return SpanService.string(
        context.getWorld(),
        shooter,
        context.getAbsolutePos(WEST_ANCHOR),
        context.getAbsolutePos(EAST_ANCHOR),
        zipline);
  }
}
