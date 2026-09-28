package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.config.TraversalArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.function.UnaryOperator;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

final class TraversalTestSupport {
  static final String TEMPLATE = "not-enough-arrows:fire_pad";
  static final String ARENA = "not-enough-arrows:open_arena";
  static final int TICK_LIMIT = 40;

  private static final Vec3d WATCHER_STAND = new Vec3d(40.5, 3.0, 12.5);
  private static final double BORDER_SIZE = 200.0;
  private static final double EPSILON = 1.0E-6;

  private TraversalTestSupport() {}

  static void withTheBorderEastEdgeAt(
      final TestContext context, final int relativeX, final Runnable edit) {
    final WorldBorder border = context.getWorld().getWorldBorder();
    final double centerX = border.getCenterX();
    final double centerZ = border.getCenterZ();
    final double size = border.getSize();
    final BlockPos origin = context.getAbsolutePos(BlockPos.ORIGIN);
    try {
      border.setSize(BORDER_SIZE);
      border.setCenter(origin.getX() + relativeX - BORDER_SIZE / 2.0, origin.getZ());
      edit.run();
    } finally {
      border.setSize(size);
      border.setCenter(centerX, centerZ);
    }
  }

  static void withTraversal(final UnaryOperator<TraversalArrowConfig> change, final Runnable body) {
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    try {
      ServerConfigHolder.set(previous.withTraversal(change.apply(previous.traversal())));
      body.run();
    } finally {
      ServerConfigHolder.set(previous);
    }
  }

  static ServerPlayerEntity keepEntitiesTicking(final TestContext context) {
    return playerAt(context, WATCHER_STAND);
  }

  static void stone(final TestContext context, final BlockPos... relative) {
    for (final BlockPos pos : relative) {
      context.setBlockState(pos, Blocks.STONE);
    }
  }

  static ServerPlayerEntity playerAt(final TestContext context, final Vec3d relative) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, player, relative);
    return player;
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
