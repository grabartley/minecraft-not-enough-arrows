package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.ControlSteering;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RepelArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "repel-arrow";
  private static final BlockPos HOSTILE_STAND = new BlockPos(5, 3, 4);
  private static final BlockPos PREY_STAND = new BlockPos(3, 3, 5);

  @BeforeBatch(batchId = BATCH)
  public void forgetHoldsLeftByOtherTests(ServerWorld world) {
    ControlTestSupport.forgetEveryHoldAndCloud();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHostileNearTheImpactIsSentRunningFromIt(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ZombieEntity hostile = ControlTestSupport.stillZombieAt(context, HOSTILE_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.REPEL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), hostile)
                  .filter(hold -> hold.steering() == ControlSteering.FLEEING)
                  .isPresent(),
              "A hostile within reach of a repel arrow should be set fleeing its impact");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFleeingHostileKeepsWhatItWasFightingSoItCanStillRetaliate(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ZombieEntity hostile =
        ControlTestSupport.engagedZombieAt(context, HOSTILE_STAND, PREY_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.REPEL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              hostile.getTarget() != null,
              "A repelled hostile should be fleeing rather than harmless, so it keeps its target");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowIsSpentByItsOwnRepelRatherThanEmbedding(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.REPEL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.dontExpectEntity(ModArrows.REPEL_ARROW.entityType());
          context.complete();
        });
  }
}
