package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.LoadedGround;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class LoadedGroundGameTest implements FabricGameTest {
  private static final String BATCH = "loaded-ground";
  private static final int FAR_AWAY = 20_000_000;

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theGroundUnderATestIsLoaded(TestContext context) {
    context.assertTrue(
        LoadedGround.in(context.getWorld()).test(context.getAbsolutePos(BlockPos.ORIGIN)),
        "The ground a test runs on is loaded");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void groundFarFromEveryoneIsNotLoadedAndIsNotLoadedByAsking(TestContext context) {
    final BlockPos far = new BlockPos(FAR_AWAY, 64, FAR_AWAY);

    context.assertFalse(LoadedGround.in(context.getWorld()).test(far), "Far ground is unloaded");
    context.assertFalse(
        LoadedGround.in(context.getWorld()).test(far), "Asking did not load it either");
    context.complete();
  }
}
