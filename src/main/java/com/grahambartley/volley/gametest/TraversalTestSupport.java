package com.grahambartley.volley.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class TraversalTestSupport {
  static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  static final String ARENA = MobArena.TEMPLATE;
  static final int TICK_LIMIT = 40;

  private static final Vec3d WATCHER_STAND = new Vec3d(40.5, 3.0, 12.5);
  private static final double BORDER_SIZE = 200.0;
  private static final double EPSILON = 1.0E-6;

  private TraversalTestSupport() {}

  static void withTheBorderEastEdgeAt(
      final TestContext context, final int relativeX, final Runnable edit) {
    final BlockPos origin = context.getAbsolutePos(BlockPos.ORIGIN);
    TerrainTestSupport.withTheBorderAt(
        context, origin.getX() + relativeX - BORDER_SIZE / 2.0, origin.getZ(), BORDER_SIZE, edit);
  }

  static ServerPlayerEntity keepEntitiesTicking(final TestContext context) {
    return MockPlayerSupport.playerAt(context, WATCHER_STAND);
  }

  static void stone(final TestContext context, final BlockPos... relative) {
    for (final BlockPos pos : relative) {
      context.setBlockState(pos, Blocks.STONE);
    }
  }

  static void placeCenteredAt(
      final TestContext context, final PlayerEntity player, final Vec3d relativeCenter) {
    final Vec3d feet =
        relativeCenter.subtract(0.0, player.getBoundingBox().getLengthY() / 2.0, 0.0);
    MockPlayerSupport.moveTo(context, player, feet);
  }

  static boolean nearlyEqual(final double expected, final double actual) {
    return Math.abs(expected - actual) < EPSILON;
  }
}
