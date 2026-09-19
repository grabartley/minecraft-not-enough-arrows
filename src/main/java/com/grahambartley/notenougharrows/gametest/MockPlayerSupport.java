package com.grahambartley.notenougharrows.gametest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

final class MockPlayerSupport {
  static final String BATCH = "grapple";

  private static final float EASTWARD_YAW = 270.0f;
  private static final float LEVEL_PITCH = 0.0f;
  private static final int FULLY_DRAWN = 0;
  private static final int A_QUIVER = 8;
  private static final float NO_ROLL = 0.0f;
  private static final float BOW_SPEED = 3.0f;
  private static final float NO_DIVERGENCE = 0.0f;

  static final double CLOSING_STEP = 0.1;

  private MockPlayerSupport() {}

  static void moveTo(
      final TestContext context, final PlayerEntity player, final Vec3d relativePos) {
    final Vec3d target = context.getAbsolute(relativePos);
    player.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    player.setVelocity(Vec3d.ZERO);
  }

  static void creepToward(
      final TestContext context, final PlayerEntity player, final BlockPos relativeAnchor) {
    final Vec3d target = Vec3d.ofCenter(context.getAbsolutePos(relativeAnchor));
    final Vec3d closer =
        player
            .getPos()
            .add(
                target
                    .subtract(player.getBoundingBox().getCenter())
                    .normalize()
                    .multiply(CLOSING_STEP));
    player.refreshPositionAndAngles(
        closer.getX(), closer.getY(), closer.getZ(), player.getYaw(), player.getPitch());
  }

  static void fireEastStraight(
      final TestContext context, final ServerPlayerEntity shooter, final Item arrow) {
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);

    final PersistentProjectileEntity projectile =
        ((ArrowItem) arrow).createArrow(context.getWorld(), new ItemStack(arrow), shooter, null);
    projectile.setVelocity(
        shooter, shooter.getPitch(), shooter.getYaw(), NO_ROLL, BOW_SPEED, NO_DIVERGENCE);
    projectile.setCritical(true);
    context.getWorld().spawnEntity(projectile);
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
    moveTo(context, player, Vec3d.ofBottomCenter(relativePos));
    return player;
  }

  static PlayerEntity mortalPlayerAt(final TestContext context, final BlockPos relativePos) {
    final PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
    moveTo(context, player, Vec3d.ofBottomCenter(relativePos));
    return player;
  }
}
