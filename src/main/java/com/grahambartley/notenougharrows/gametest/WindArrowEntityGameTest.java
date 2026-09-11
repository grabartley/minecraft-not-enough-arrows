package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class WindArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "wind-arrow";
  private static final BlockPos BYSTANDER_STAND = new BlockPos(5, 3, 4);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowShovesABystanderNearItsImpact(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ArmorStandEntity bystander =
        FiringRangeSupport.standOnPedestalAt(context, BYSTANDER_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.WIND_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertFalse(
              bystander.getVelocity().equals(Vec3d.ZERO),
              "A bystander beside the impact should be shoved by the burst");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowIsSpentByItsOwnBurstRatherThanEmbedding(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.WIND_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.dontExpectEntity(ModArrows.WIND_ARROW.entityType());
          context.complete();
        });
  }
}
