package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.grapple.GrappleFlightCheck;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class GrappleFlightCheckGameTest implements FabricGameTest {
  private static final String TEMPLATE = "more-arrows:fire_pad";

  private static final BlockPos PLAYER_STAND = new BlockPos(0, 3, 0);
  private static final int COUNTED_TICKS = 40;

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void aPlayerHeldInTheAirHasTheirFloatingCountCleared(TestContext context) {
    final ServerPlayerEntity player = GrappleTestSupport.playerAt(context, PLAYER_STAND);
    player.networkHandler.floatingTicks = COUNTED_TICKS;

    GrappleFlightCheck.clearFloatingCountFor(player);

    context.assertEquals(
        player.networkHandler.floatingTicks,
        0,
        "Ticks the server still counts a pulled player as floating for");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 20)
  public void aMissingPlayerIsLeftAlone(TestContext context) {
    GrappleFlightCheck.clearFloatingCountFor(null);

    context.complete();
  }
}
