package com.grahambartley.morearrows.gametest;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class MockPlayerSupport {
  static final String BATCH = "grapple";

  private static final float EASTWARD_YAW = 270.0f;
  private static final float LEVEL_PITCH = 0.0f;
  private static final int FULLY_DRAWN = 0;
  private static final int A_QUIVER = 8;

  private MockPlayerSupport() {}

  static void moveTo(
      final TestContext context, final ServerPlayerEntity player, final Vec3d relativePos) {
    final Vec3d target = context.getAbsolute(relativePos);
    player.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    player.setVelocity(Vec3d.ZERO);
  }

  static void fireEastFromBow(
      final TestContext context, final ServerPlayerEntity shooter, final Item arrow) {
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);
    shooter.getInventory().setStack(0, new ItemStack(arrow, A_QUIVER));

    Items.BOW.onStoppedUsing(new ItemStack(Items.BOW), context.getWorld(), shooter, FULLY_DRAWN);
  }

  static ServerPlayerEntity playerAt(final TestContext context, final BlockPos relativePos) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
    final Vec3d target = context.getAbsolute(Vec3d.ofBottomCenter(relativePos));
    player.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    player.setVelocity(Vec3d.ZERO);
    return player;
  }
}
