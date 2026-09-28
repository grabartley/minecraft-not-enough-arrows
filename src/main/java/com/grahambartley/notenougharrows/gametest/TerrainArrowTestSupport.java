package com.grahambartley.notenougharrows.gametest;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class TerrainArrowTestSupport {
  static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  static final BlockPos DISPENSER_STAND = new BlockPos(1, 3, 3);
  static final int SETTLED_TICK = 30;
  static final int TICK_LIMIT = 60;

  private TerrainArrowTestSupport() {}

  static ServerPlayerEntity fireFromBow(final TestContext context, final Item arrow) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, arrow);
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);
    return shooter;
  }

  static ServerPlayerEntity fireFromBow(final TestContext context, final ItemStack quiver) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, quiver);
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);
    return shooter;
  }
}
