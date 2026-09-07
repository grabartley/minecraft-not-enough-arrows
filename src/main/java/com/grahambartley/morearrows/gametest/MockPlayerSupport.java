package com.grahambartley.morearrows.gametest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class MockPlayerSupport {
  static final String BATCH = "grapple";

  private MockPlayerSupport() {}

  static void moveTo(
      final TestContext context, final ServerPlayerEntity player, final Vec3d relativePos) {
    final Vec3d target = context.getAbsolute(relativePos);
    player.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    player.setVelocity(Vec3d.ZERO);
  }

  static ServerPlayerEntity playerAt(final TestContext context, final BlockPos relativePos) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
    final Vec3d target = context.getAbsolute(Vec3d.ofBottomCenter(relativePos));
    player.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    player.setVelocity(Vec3d.ZERO);
    return player;
  }
}
