package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.grahambartley.morearrows.server.ServerConfigUpdateReceiver;
import com.grahambartley.morearrows.server.ServerConfigUpdateReceiver.Outcome;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ServerConfigUpdateReceiverGameTest implements FabricGameTest {
  private static final String BATCH = "server-config-update";
  private static final int CHANGED_RANGE = 61;

  @BeforeBatch(batchId = BATCH)
  public void resetConfigBeforeBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @AfterBatch(batchId = BATCH)
  public void restoreConfigAfterBatch(ServerWorld world) {
    ServerConfigService.update(world.getServer(), MoreArrowsConfig.defaults());
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anOperatorsUpdateIsApplied(TestContext context) {
    final ServerPlayerEntity operator = operator(context);

    final Outcome outcome =
        ServerConfigUpdateReceiver.apply(operator, configWithRange(CHANGED_RANGE));

    context.assertTrue(
        outcome == Outcome.APPLIED,
        "An operator's config update should be applied, was " + outcome);
    context.assertEquals(
        CHANGED_RANGE,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after an operator update");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aNonOperatorsUpdateIsRejected(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), MoreArrowsConfig.defaults());
    final ServerPlayerEntity bystander = context.createMockCreativeServerPlayerInWorld();

    final Outcome outcome =
        ServerConfigUpdateReceiver.apply(bystander, configWithRange(CHANGED_RANGE));

    context.assertTrue(
        outcome == Outcome.REJECTED,
        "A non-operator's config update should be rejected, was " + outcome);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aNonOperatorsUpdateLeavesTheLiveConfigAlone(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), MoreArrowsConfig.defaults());
    final ServerPlayerEntity bystander = context.createMockCreativeServerPlayerInWorld();

    ServerConfigUpdateReceiver.apply(bystander, configWithRange(CHANGED_RANGE));

    context.assertEquals(
        GrappleArrowConfig.DEFAULT_MAX_RANGE_BLOCKS,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after a rejected update");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anUpdateWithoutAPlayerFails(TestContext context) {
    context.assertTrue(
        ServerConfigUpdateReceiver.apply(null, MoreArrowsConfig.defaults()) == Outcome.FAILED,
        "An update with no sender should fail rather than apply");
    context.complete();
  }

  private static ServerPlayerEntity operator(final TestContext context) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
    context
        .getWorld()
        .getServer()
        .getPlayerManager()
        .getOpList()
        .add(
            new OperatorEntry(
                player.getGameProfile(), ServerConfigService.OP_PERMISSION_LEVEL, false));
    return player;
  }

  private static MoreArrowsConfig configWithRange(final int maxRangeBlocks) {
    return MoreArrowsConfig.defaults()
        .withGrapple(
            new GrappleArrowConfig(
                maxRangeBlocks,
                GrappleArrowConfig.DEFAULT_PULL_SPEED,
                GrappleArrowConfig.DEFAULT_PULL_ACCELERATION,
                true,
                true,
                GrappleArrowConfig.DEFAULT_ROPE_LENGTH_BLOCKS,
                false));
  }
}
