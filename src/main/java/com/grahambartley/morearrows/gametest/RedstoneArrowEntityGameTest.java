package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.ModBlocks;
import com.grahambartley.morearrows.redstone.RedstoneChargeService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RedstoneArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "redstone-arrow";
  private static final BlockPos LAMP = new BlockPos(5, 3, 4);

  @BeforeBatch(batchId = BATCH)
  public void forgetChargesBeforeBatch(ServerWorld world) {
    RedstoneChargeService.forget();
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowChargesTheFaceItStrikes(TestContext context) {
    UtilityArrowTestSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, UtilityArrowTestSupport.SHOOTER_STAND),
        ModArrows.REDSTONE_ARROW.item());

    context.runAtTick(
        UtilityArrowTestSupport.LANDING_TICK,
        () -> {
          context.expectBlock(ModBlocks.REDSTONE_CHARGE, UtilityArrowTestSupport.IMPACT_FACE);
          context.complete();
        });
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aChargedFaceLightsARedstoneLampBesideIt(TestContext context) {
    UtilityArrowTestSupport.raiseBackstop(context);
    context.setBlockState(LAMP, Blocks.REDSTONE_LAMP);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, UtilityArrowTestSupport.SHOOTER_STAND),
        ModArrows.REDSTONE_ARROW.item());

    context.runAtTick(
        UtilityArrowTestSupport.LANDING_TICK + 5,
        () -> {
          context.checkBlockState(
              LAMP,
              state -> state.get(Properties.LIT),
              () -> "A redstone arrow should light a lamp beside the face it strikes");
          context.complete();
        });
  }
}
