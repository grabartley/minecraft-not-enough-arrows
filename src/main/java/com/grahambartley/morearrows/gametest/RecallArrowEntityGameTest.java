package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.entity.RecallArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RecallArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "recall-arrow";
  private static final BlockPos SHOOTER_STAND = new BlockPos(1, 3, 3);
  private static final BlockPos DISPENSER_STAND = new BlockPos(1, 4, 3);
  private static final BlockPos TARGET_IN_THE_LANE = new BlockPos(4, 4, 3);
  private static final double ARRIVED_WITHIN = 2.0;
  private static final double STAYED_WITHIN = 1.0;
  private static final int DISPENSED_LANDING_TICK = 20;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anArrowFiredFromABowBringsTheMobItStrikesToTheShooter(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_IN_THE_LANE);
    final ServerPlayerEntity shooter = fireEast(context);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getPos().distanceTo(shooter.getPos()) <= ARRIVED_WITHIN,
              "The struck mob should arrive at the shooter, but stood at "
                  + target.getPos()
                  + " against a shooter at "
                  + shooter.getPos());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anArrowFiredFromABowHurtsNothingItStrikes(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_IN_THE_LANE);
    final float unharmed = target.getHealth();
    fireEast(context);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertEquals(
              target.getHealth(),
              unharmed,
              "The recall arrow moves what it strikes rather than hurting it");
          context.dontExpectEntity(ModArrows.RECALL_ARROW.entityType());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anArrowThatStrikesABlockMovesNothingAndEmbeds(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    fireEast(context);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, RecallArrowEntity.class) != null,
              "An arrow that struck a block should embed and stay recoverable");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowWithNobodyBehindItMovesNothing(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_IN_THE_LANE);
    final Vec3d stoodAt = target.getPos();
    FiringRangeSupport.dispenseEast(context, DISPENSER_STAND, ModArrows.RECALL_ARROW.item());

    context.runAtTick(
        DISPENSED_LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getPos().distanceTo(stoodAt) <= STAYED_WITHIN,
              "A dispensed arrow has no shooter to recall to, but the mob moved from "
                  + stoodAt
                  + " to "
                  + target.getPos());
          context.complete();
        });
  }

  private static ServerPlayerEntity fireEast(final TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.RECALL_ARROW.item());
    return shooter;
  }
}
