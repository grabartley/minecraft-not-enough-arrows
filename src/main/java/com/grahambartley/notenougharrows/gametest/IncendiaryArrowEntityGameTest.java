package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class IncendiaryArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "incendiary-arrow";
  private static final BlockPos BYSTANDER_STAND = new BlockPos(5, 3, 4);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowFiredFromABowSetsABystanderAlight(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander = FiringRangeSupport.liveTargetOnPedestalAt(context, BYSTANDER_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.INCENDIARY_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              bystander.isOnFire(), "A bystander beside the impact should be set alight");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowLeavesFireWithoutBreakingTerrain(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.INCENDIARY_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.dontExpectEntity(ModArrows.INCENDIARY_ARROW.entityType());
          context.complete();
        });
  }
}
