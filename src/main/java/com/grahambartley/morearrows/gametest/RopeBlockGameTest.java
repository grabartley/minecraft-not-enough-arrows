package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModBlocks;
import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.rope.RopeService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RopeBlockGameTest implements FabricGameTest {
  private static final String BATCH = "rope-block";
  private static final String DECAY_ON_BATCH = "rope-decay-on";
  private static final String DECAY_OFF_BATCH = "rope-decay-off";
  private static final int ROPE_LENGTH = 6;
  private static final int NEXT_TICK = 1;
  private static final int DECAY_CHECK_TICK = 5;

  @BeforeBatch(batchId = DECAY_ON_BATCH)
  public void letRopesDecayBeforeBatch(ServerWorld world) {
    ServerConfigHolder.set(configuredRopesDecay(true));
  }

  @AfterBatch(batchId = DECAY_ON_BATCH)
  public void restoreDefaultConfigAfterDecayOnBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @BeforeBatch(batchId = DECAY_OFF_BATCH)
  public void keepRopesStandingBeforeBatch(ServerWorld world) {
    ServerConfigHolder.set(configuredRopesDecay(false));
  }

  @AfterBatch(batchId = DECAY_OFF_BATCH)
  public void restoreDefaultConfigAfterDecayOffBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeIsSomethingAPlayerCanClimb(TestContext context) {
    context.assertTrue(
        ModBlocks.ROPE.getDefaultState().isIn(BlockTags.CLIMBABLE),
        "Vanilla only lets an entity climb blocks in "
            + BlockTags.CLIMBABLE.id()
            + ", so the rope has to be in it");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeStandsWhileItsAnchorStands(TestContext context) {
    dropRope(context);

    context.runAtTick(
        NEXT_TICK,
        () -> {
          context.expectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD);
          context.expectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD.down(ROPE_LENGTH - 1));
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeWhoseAnchorIsBrokenFallsAway(TestContext context) {
    dropRope(context);

    context.runAtTick(NEXT_TICK, () -> context.setBlockState(RopeTestSupport.CEILING, Blocks.AIR));
    context.runAtTick(
        NEXT_TICK + 1,
        () -> {
          for (int depth = 0; depth < ROPE_LENGTH; depth++) {
            context.expectBlock(Blocks.AIR, RopeTestSupport.ROPE_HEAD.down(depth));
          }
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeBrokenPartWayDownTakesTheRopeBelowItWithIt(TestContext context) {
    dropRope(context);

    final BlockPos cut = RopeTestSupport.ROPE_HEAD.down(2);
    context.runAtTick(NEXT_TICK, () -> context.setBlockState(cut, Blocks.AIR));
    context.runAtTick(
        NEXT_TICK + 1,
        () -> {
          context.expectBlock(ModBlocks.ROPE, cut.up());
          for (int depth = 0; depth < ROPE_LENGTH - 2; depth++) {
            context.expectBlock(Blocks.AIR, cut.down(depth));
          }
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeSchedulesItsOwnDecayCheckAsItIsPlaced(TestContext context) {
    dropRope(context);

    context.assertTrue(
        context
            .getWorld()
            .getBlockTickScheduler()
            .isQueued(context.getAbsolutePos(RopeTestSupport.ROPE_HEAD), ModBlocks.ROPE),
        "A rope that never schedules a decay check can never be swept up");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = DECAY_ON_BATCH, tickLimit = 40)
  public void aRopeDecaysWhenItsDecayCheckComesRoundAndDecayIsOn(TestContext context) {
    dropRopeWithDecayCheckDueSoon(context);

    context.runAtTick(
        DECAY_CHECK_TICK + NEXT_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, RopeTestSupport.ROPE_HEAD);
          context.expectBlock(Blocks.AIR, RopeTestSupport.ROPE_HEAD.down(ROPE_LENGTH - 1));
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = DECAY_OFF_BATCH, tickLimit = 40)
  public void aRopeSurvivesItsDecayCheckWhenDecayIsOff(TestContext context) {
    dropRopeWithDecayCheckDueSoon(context);

    context.runAtTick(
        DECAY_CHECK_TICK + NEXT_TICK,
        () -> {
          context.expectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD);
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = DECAY_OFF_BATCH, tickLimit = 40)
  public void aRopeThatSurvivedADecayCheckIsCheckedAgainLater(TestContext context) {
    dropRopeWithDecayCheckDueSoon(context);

    context.runAtTick(
        DECAY_CHECK_TICK + NEXT_TICK,
        () -> {
          context.assertTrue(
              context
                  .getWorld()
                  .getBlockTickScheduler()
                  .isQueued(context.getAbsolutePos(RopeTestSupport.ROPE_HEAD), ModBlocks.ROPE),
              "A rope spared by a decay check has to book the next one, or turning decay on later"
                  + " would never reach it");
          context.complete();
        });
  }

  private static void dropRopeWithDecayCheckDueSoon(final TestContext context) {
    for (int depth = 0; depth < ROPE_LENGTH; depth++) {
      context
          .getWorld()
          .scheduleBlockTick(
              context.getAbsolutePos(RopeTestSupport.ROPE_HEAD.down(depth)),
              ModBlocks.ROPE,
              DECAY_CHECK_TICK);
    }
    dropRope(context);
  }

  private static MoreArrowsConfig configuredRopesDecay(final boolean ropesDecay) {
    final MoreArrowsConfig defaults = MoreArrowsConfig.defaults();
    final GrappleArrowConfig grapple = defaults.grapple().withRopesDecay(ropesDecay);
    return defaults.withGrapple(grapple);
  }

  private static void dropRope(final TestContext context) {
    RopeTestSupport.raiseCeiling(context);
    RopeService.drop(
        context.getWorld(), context.getAbsolutePos(RopeTestSupport.CEILING), null, ROPE_LENGTH);
  }
}
