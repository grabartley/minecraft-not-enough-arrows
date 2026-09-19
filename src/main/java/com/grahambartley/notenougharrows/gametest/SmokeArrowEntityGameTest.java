package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class SmokeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "smoke-arrow";
  private static final BlockPos INSIDE_STAND = new BlockPos(5, 3, 4);
  private static final int SETTLING_TICK = FiringRangeSupport.LANDING_TICK + 5;

  @BeforeBatch(batchId = BATCH)
  public void forgetCloudsLeftByOtherTests(ServerWorld world) {
    ControlTestSupport.forgetEveryHoldAndCloud();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowFiredFromABowOpensACloudThatBlindsWhatIsInsideIt(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity inside = ControlTestSupport.stillCowAt(context, INSIDE_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SMOKE_ARROW.item());

    context.runAtTick(
        SETTLING_TICK,
        () -> {
          context.expectEntityHasEffect(inside, StatusEffects.BLINDNESS, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void aDispensedArrowOpensACloudJustTheSame(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity inside = ControlTestSupport.stillCowAt(context, INSIDE_STAND);
    FiringRangeSupport.dispenseEast(context, new BlockPos(1, 3, 3), ModArrows.SMOKE_ARROW.item());

    context.runAtTick(
        SETTLING_TICK,
        () -> {
          context.expectEntityHasEffect(inside, StatusEffects.BLINDNESS, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void theCloudLeavesTheWorldItselfUntouched(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity inside = ControlTestSupport.stillCowAt(context, INSIDE_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SMOKE_ARROW.item());

    context.runAtTick(
        SETTLING_TICK,
        () -> {
          context.expectEntityHasEffect(inside, StatusEffects.BLINDNESS, 0);
          context.assertTrue(
              context.getBlockState(FiringRangeSupport.IMPACT_FACE).isAir(),
              "A smoke cloud should place no block, so the impact position stays air");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowIsSpentByItsOwnCloudRatherThanEmbedding(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SMOKE_ARROW.item());

    context.runAtTick(
        SETTLING_TICK,
        () -> {
          context.dontExpectEntity(ModArrows.SMOKE_ARROW.entityType());
          context.complete();
        });
  }
}
