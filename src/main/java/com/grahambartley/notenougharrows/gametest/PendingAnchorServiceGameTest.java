package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.zipline.PendingAnchor;
import com.grahambartley.notenougharrows.zipline.PendingAnchorService;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class PendingAnchorServiceGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-pending";
  private static final BlockPos ANCHOR = new BlockPos(3, 3, 3);
  private static final int WINDOW_TICKS = 200;
  private static final int SHORT_WINDOW_TICKS = 5;

  @BeforeBatch(batchId = BATCH)
  public void forgetPendingAnchorsBeforeBatch(ServerWorld world) {
    PendingAnchorService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aHeldAnchorRecordsWhereAndWhatItHolds(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    final UUID arrow = UUID.randomUUID();

    final PendingAnchor anchor = hold(context, owner, arrow, WINDOW_TICKS);

    context.assertEquals(context.getAbsolutePos(ANCHOR), anchor.pos(), "Position");
    context.assertEquals(arrow, anchor.arrowId(), "Arrow");
    context.assertEquals(
        context.getWorld().getTime() + WINDOW_TICKS, anchor.expiryTick(), "Expiry");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void claimingAnAnchorHandsItOverOnce(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    final PendingAnchor anchor = hold(context, owner, UUID.randomUUID(), WINDOW_TICKS);

    context.assertEquals(
        anchor, PendingAnchorService.claim(context.getWorld(), owner).orElse(null), "First claim");
    context.assertTrue(
        PendingAnchorService.claim(context.getWorld(), owner).isEmpty(), "Second claim");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anAnchorWhoseBlockChangedCannotBeClaimed(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    hold(context, owner, UUID.randomUUID(), WINDOW_TICKS);

    context.setBlockState(ANCHOR, Blocks.GLASS);

    context.assertTrue(
        PendingAnchorService.claim(context.getWorld(), owner).isEmpty(),
        "A replaced anchor block holds nothing");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 30)
  public void anAnchorIsDiscardedWhenItsWindowCloses(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    hold(context, owner, UUID.randomUUID(), SHORT_WINDOW_TICKS);

    context.runAtTick(
        SHORT_WINDOW_TICKS + 3,
        () -> {
          context.assertTrue(
              PendingAnchorService.pendingFor(owner.getUuid()).isEmpty(), "The window has closed");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerWhoDiesLosesTheirPendingAnchor(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    hold(context, owner, UUID.randomUUID(), WINDOW_TICKS);

    ServerLivingEntityEvents.AFTER_DEATH
        .invoker()
        .afterDeath(owner, context.getWorld().getDamageSources().generic());

    context.assertTrue(
        PendingAnchorService.pendingFor(owner.getUuid()).isEmpty(),
        "Death should discard a pending anchor");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerWhoLeavesLosesTheirPendingAnchor(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    hold(context, owner, UUID.randomUUID(), WINDOW_TICKS);

    PendingAnchorService.discard(owner.getUuid());

    context.assertTrue(
        PendingAnchorService.pendingFor(owner.getUuid()).isEmpty(),
        "Leaving should discard a pending anchor");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void noWindowHoldsNothing(TestContext context) {
    context.setBlockState(ANCHOR, Blocks.STONE);
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();

    context.assertTrue(
        PendingAnchorService.hold(
                context.getWorld(), owner, context.getAbsolutePos(ANCHOR), UUID.randomUUID(), 0)
            .isEmpty(),
        "A zero window holds nothing");
    context.complete();
  }

  private static PendingAnchor hold(
      final TestContext context,
      final ServerPlayerEntity owner,
      final UUID arrow,
      final int windowTicks) {
    return PendingAnchorService.hold(
            context.getWorld(), owner, context.getAbsolutePos(ANCHOR), arrow, windowTicks)
        .orElseThrow();
  }
}
