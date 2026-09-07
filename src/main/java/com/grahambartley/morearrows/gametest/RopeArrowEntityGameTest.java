package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RopeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "rope-arrow";
  private static final BlockPos SHOOTER_STAND = new BlockPos(0, 10, 0);
  private static final BlockPos OVERHANG = new BlockPos(4, 11, 0);
  private static final int LANDING_TICK = 20;

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowIntoAnOverhangHangsARopeBeneathIt(TestContext context) {
    context.setBlockState(OVERHANG, Blocks.STONE);
    MockPlayerSupport.fireEastFromBow(
        context, MockPlayerSupport.playerAt(context, SHOOTER_STAND), ModArrows.ROPE_ARROW.item());

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
}
