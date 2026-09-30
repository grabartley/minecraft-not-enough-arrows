package com.grahambartley.notenougharrows.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class LeavesWhenTestEndsGameTest implements FabricGameTest {
  private static final String BATCH = "leaves-when-test-ends";
  private static final BlockPos STAND = new BlockPos(0, 1, 0);

  private static PlayerManager players(final TestContext context) {
    return context.getWorld().getServer().getPlayerManager();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH)
  public void aPlayerLeavesTheServerWhenItsTestPasses(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.survivalPlayerAt(context, STAND);

    new LeavesWhenTestEnds(player).onPassed(null, null);

    context.assertTrue(
        players(context).getPlayer(player.getUuid()) == null,
        "A test's player should leave the server once the test passes");
    context.assertTrue(player.isRemoved(), "A test's player should leave the world too");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH)
  public void aPlayerLeavesTheServerWhenItsTestFails(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.survivalPlayerAt(context, STAND);

    new LeavesWhenTestEnds(player).onFailed(null, null);

    context.assertTrue(
        players(context).getPlayer(player.getUuid()) == null,
        "A test's player should leave the server once the test fails");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH)
  public void aPlayerLeavesTheServerWhenItsTestIsRetried(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.survivalPlayerAt(context, STAND);

    new LeavesWhenTestEnds(player).onRetry(null, null, null);

    context.assertTrue(
        players(context).getPlayer(player.getUuid()) == null,
        "A test's player should leave the server before the test runs again");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH)
  public void aPlayerStaysWhileItsTestIsRunning(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, STAND);

    new LeavesWhenTestEnds(player).onStarted(null);

    context.assertTrue(
        players(context).getPlayer(player.getUuid()) == player,
        "A test's player should stay on the server while the test runs");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH)
  public void leavingTwiceIsHarmless(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.survivalPlayerAt(context, STAND);
    final LeavesWhenTestEnds leaving = new LeavesWhenTestEnds(player);
    final int before = players(context).getCurrentPlayerCount();

    leaving.leave();
    leaving.leave();

    context.assertTrue(
        players(context).getCurrentPlayerCount() == before - 1,
        "A player that has already left should not be removed a second time");
    context.complete();
  }
}
