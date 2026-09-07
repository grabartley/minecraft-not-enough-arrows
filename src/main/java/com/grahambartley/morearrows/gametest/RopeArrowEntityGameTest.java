package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RopeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "rope-arrow";
  private static final BlockPos SHOOTER_STAND = new BlockPos(0, 10, 0);
  private static final BlockPos OVERHANG = new BlockPos(4, 11, 0);
  private static final float EASTWARD_YAW = 270.0f;
  private static final float LEVEL_PITCH = 0.0f;
  private static final int FULLY_DRAWN = 0;
  private static final int LANDING_TICK = 20;

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowIntoAnOverhangHangsARopeBeneathIt(TestContext context) {
    context.setBlockState(OVERHANG, Blocks.STONE);
    fireFromBow(context, MockPlayerSupport.playerAt(context, SHOOTER_STAND));

    context.runAtTick(
        LANDING_TICK,
        () -> {
          context.expectBlock(ModBlocks.ROPE, OVERHANG.down());
          context.expectBlock(
              ModBlocks.ROPE,
              new BlockPos(OVERHANG.getX(), RopeTestSupport.SHAFT_FLOOR_Y, OVERHANG.getZ()));
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatFindsNothingToAnchorToHangsNoRope(TestContext context) {
    fireFromBow(context, MockPlayerSupport.playerAt(context, SHOOTER_STAND));

    context.runAtTick(
        LANDING_TICK,
        () -> {
          context.dontExpectBlock(ModBlocks.ROPE, OVERHANG.down());
          context.complete();
        });
  }

  private static void fireFromBow(final TestContext context, final ServerPlayerEntity shooter) {
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);
    shooter.getInventory().setStack(0, new ItemStack(ModArrows.ROPE_ARROW.item(), 8));

    final ItemStack bow = new ItemStack(Items.BOW);
    Items.BOW.onStoppedUsing(bow, context.getWorld(), shooter, FULLY_DRAWN);
  }
}
