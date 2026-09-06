package com.grahambartley.morearrows.gametest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class GrappleTestSupport {
  static final String BATCH = "grapple";

  private GrappleTestSupport() {}

  static ServerPlayerEntity playerAt(final TestContext context, final BlockPos relativePos) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
    final Vec3d target = context.getAbsolute(Vec3d.ofBottomCenter(relativePos));
    player.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    player.setVelocity(Vec3d.ZERO);
    return player;
  }
}
