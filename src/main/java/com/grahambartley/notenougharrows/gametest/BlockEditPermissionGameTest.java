package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.BlockEditPermission;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BlockEditPermissionGameTest implements FabricGameTest {
  private static final String BATCH = "block-edit-permission";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos INSIDE_THE_WORLD = new BlockPos(0, 3, 0);
  private static final BlockPos FAR_OUTSIDE_THE_BORDER =
      new BlockPos(Integer.MAX_VALUE, 3, Integer.MAX_VALUE);

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEditWithNobodyBehindItIsAllowedInsideTheWorldBorder(TestContext context) {
    context.assertTrue(
        BlockEditPermission.allows(
            context.getWorld(), context.getAbsolutePos(INSIDE_THE_WORLD), null),
        "A dispenser has no player behind it, so the world border is the only rule left");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEditWithNobodyBehindItStopsAtTheWorldBorder(TestContext context) {
    context.assertFalse(
        BlockEditPermission.allows(context.getWorld(), FAR_OUTSIDE_THE_BORDER, null),
        "Nothing should be edited outside the world border");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerMayEditTheWorldTheyAreStandingIn(TestContext context) {
    context.assertTrue(
        BlockEditPermission.allows(
            context.getWorld(),
            context.getAbsolutePos(INSIDE_THE_WORLD),
            MockPlayerSupport.playerAt(context, INSIDE_THE_WORLD)),
        "A player standing in an unprotected world may edit it");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingIsEditedWithoutAWorldOrAPosition(TestContext context) {
    context.assertFalse(
        BlockEditPermission.allows(null, context.getAbsolutePos(INSIDE_THE_WORLD), null),
        "An edit with no world behind it should be refused rather than guessed at");
    context.assertFalse(
        BlockEditPermission.allows(context.getWorld(), null, null),
        "An edit with no position behind it should be refused rather than guessed at");
    context.complete();
  }
}
