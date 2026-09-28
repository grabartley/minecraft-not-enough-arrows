package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.entity.TowArrowEntity;
import com.grahambartley.notenougharrows.tow.TowService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class TowArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "tow-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int HIT_TICK = 6;

  @BeforeBatch(batchId = BATCH)
  public void forgetTowsBeforeBatch(ServerWorld world) {
    TowService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aTowArrowTakesHoldOfWhatItHitsWithoutHurtingIt(TestContext context) {
    final CowEntity cow = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    TerrainArrowTestSupport.fireFromBow(context, TraversalArrows.TOW_ARROW.item());

    context.runAtTick(
        HIT_TICK,
        () -> {
          context.assertTrue(
              TowService.towOf(context.getWorld(), cow.getUuid()) != null,
              "The cow should be under tow");
          context.assertEquals(cow.getMaxHealth(), cow.getHealth(), "A tow does no damage");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TowArrowEntity.class) == null,
              "The arrow is spent on what it hits");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aDispensedTowArrowMovesNothing(TestContext context) {
    final CowEntity cow = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, TraversalArrows.TOW_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              TowService.towOf(context.getWorld(), cow.getUuid()) == null,
              "With no shooter there is nobody to tow toward");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTowArrowThatHitsABlockIsRecoverable(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, TraversalArrows.TOW_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TowArrowEntity.class) != null,
              "A tow arrow that moved nothing embeds");
          context.complete();
        });
  }
}
