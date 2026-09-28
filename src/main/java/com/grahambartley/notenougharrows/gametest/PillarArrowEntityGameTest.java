package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.entity.PillarArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class PillarArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "pillar-arrow";

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowRaisesDirtAboveTheBlockItStrikes(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.PILLAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.expectBlock(Blocks.DIRT, FiringRangeSupport.BACKSTOP.up());
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowRaisesAPillarToo(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, ModArrows.PILLAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.DIRT, FiringRangeSupport.BACKSTOP.up());
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowWithNoRoomAboveRaisesNothingAndStaysRecoverable(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.PILLAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP.up());
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, PillarArrowEntity.class) != null,
              "An arrow that raised nothing should embed and stay recoverable");
          context.complete();
        });
  }
}
