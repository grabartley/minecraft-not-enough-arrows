package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.grapple.GrappleSession;
import com.grahambartley.morearrows.server.ServerConfigService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class GrappleServiceGameTest implements FabricGameTest {
  private static final String TEMPLATE = "more-arrows:fire_pad";

  private static final BlockPos PLAYER_STAND = new BlockPos(0, 3, 0);
  private static final BlockPos HIGH_ANCHOR = new BlockPos(5, 5, 5);
  private static final BlockPos SECOND_ANCHOR = new BlockPos(1, 5, 1);
  private static final BlockPos FLOOR_UNDERFOOT = new BlockPos(0, 2, 0);
  private static final BlockPos OPEN_AIR = new BlockPos(3, 5, 3);

  private static final int BREAK_TICK = 3;
  private static final int ASSERT_TICK = 10;
  private static final int OVERRUN_MARGIN_TICKS = 10;
  private static final double SPEED_TOLERANCE = 0.01;

  @BeforeBatch(batchId = GrappleTestSupport.BATCH)
  public void forgetEveryGrappleBeforeBatch(ServerWorld world) {
    GrappleService.forget();
    AnchorService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void aGrappleWithinRangeStartsASessionAndTakesTheAnchor(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);

    final GrappleSession session =
        GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));

    context.assertTrue(session != null, "A grapple within range should hand back a session");
    context.assertTrue(
        GrappleService.sessionOf(context.getWorld(), player.getUuid()) != null,
        "A grapple should be tracked against the player it pulls");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), player.getUuid()) != null,
        "A grapple should hold the block it pulls toward");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void aGrappleBeyondTheConfiguredRangeStartsNothing(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    final MoreArrowsConfig previous = ServerConfigService.get();

    final GrappleSession session;
    try {
      ServerConfigHolder.set(configWithMaxRange(GrappleArrowConfig.MAX_RANGE_BLOCKS_MIN));
      session =
          GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertTrue(session == null, "A grapple past its range should hand back nothing");
    context.assertTrue(
        GrappleService.sessionOf(context.getWorld(), player.getUuid()) == null,
        "A grapple past its range should leave nothing tracked");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), player.getUuid()) == null,
        "A grapple past its range should take no anchor");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void aGrappleAtNothingSolidStartsNothing(TestContext context) {
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);

    final GrappleSession session =
        GrappleService.start(context.getWorld(), player, context.getAbsolutePos(OPEN_AIR));

    context.assertTrue(session == null, "Open air should not hold a grapple");
    context.assertTrue(
        GrappleService.sessionOf(context.getWorld(), player.getUuid()) == null,
        "A grapple at open air should leave nothing tracked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void grapplingAgainReplacesTheSessionRatherThanStackingASecond(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    context.setBlockState(SECOND_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);

    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));
    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(SECOND_ANCHOR));

    context.assertEquals(
        GrappleService.sessionOf(context.getWorld(), player.getUuid()).anchor(),
        context.getAbsolutePos(SECOND_ANCHOR),
        "Block the replaced grapple pulls toward");
    context.assertEquals(
        AnchorService.anchorOf(context.getWorld(), player.getUuid()).pos(),
        context.getAbsolutePos(SECOND_ANCHOR),
        "Block the replaced grapple holds");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 40)
  public void aPulledPlayerIsGivenVelocityTowardTheAnchor(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    final Vec3d[] strongestPull = {Vec3d.ZERO};
    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));

    context.runAtEveryTick(
        () -> {
          if (player.getVelocity().length() > strongestPull[0].length()) {
            strongestPull[0] = player.getVelocity();
          }
        });
    context.runAtTick(
        ASSERT_TICK,
        () -> {
          final Vec3d pull = strongestPull[0];
          context.assertTrue(
              pull.length() > 0.0, "A grappled player should be given a pull, was " + pull);
          context.assertTrue(
              pull.x > 0.0 && pull.y > 0.0 && pull.z > 0.0,
              "A pull should point at an anchor up and away from the player, was " + pull);
          context.assertTrue(
              Math.abs(pull.length() - GrappleArrowConfig.DEFAULT_PULL_SPEED) < SPEED_TOLERANCE,
              "A pull should carry the configured speed, was " + pull.length());
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 40)
  public void aSessionEndsOnceThePlayerHasArrivedAtTheAnchor(TestContext context) {
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(FLOOR_UNDERFOOT));

    context.runAtTick(
        ASSERT_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), player.getUuid()) == null,
              "A player already standing on the anchor should stop being pulled");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 40)
  public void aSessionEndsWhenTheAnchorBlockIsBroken(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));

    context.runAtTick(BREAK_TICK, () -> context.setBlockState(HIGH_ANCHOR, Blocks.AIR));
    context.runAtTick(
        ASSERT_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), player.getUuid()) == null,
              "A grapple whose block was broken should stop pulling");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 120)
  public void aSessionEndsOnceItRunsOutOfTicks(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    final GrappleSession session =
        GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));
    context.assertTrue(session != null, "A grapple across the arena should start");

    context.runAtTick(
        session.remainingTicks() + OVERRUN_MARGIN_TICKS,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), player.getUuid()) == null,
              "A grapple that ran out of ticks should stop pulling");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void releasingAGrappleGivesUpItsAnchorToo(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));

    context.assertTrue(
        GrappleService.release(context.getWorld(), player.getUuid()) != null,
        "Releasing a grapple should hand back the session it ended");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), player.getUuid()) == null,
        "A released grapple should give up the block it held");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void aPlayerReleasedEverywhereIsNoLongerPulled(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    GrappleService.start(context.getWorld(), player, context.getAbsolutePos(HIGH_ANCHOR));

    GrappleService.releaseEverywhere(player.getUuid());

    context.assertTrue(
        GrappleService.sessionOf(context.getWorld(), player.getUuid()) == null,
        "A player who left should be pulled in no world at all");
    context.complete();
  }

  private static MoreArrowsConfig configWithMaxRange(final int maxRangeBlocks) {
    return MoreArrowsConfig.defaults()
        .withGrapple(GrappleArrowConfig.defaults().withMaxRangeBlocks(maxRangeBlocks));
  }
}
