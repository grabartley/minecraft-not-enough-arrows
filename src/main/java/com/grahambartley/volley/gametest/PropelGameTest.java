package com.grahambartley.volley.gametest;

import com.grahambartley.volley.world.Propel;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PropelGameTest implements FabricGameTest {
  private static final String TEMPLATE = "volley:fire_pad";

  private static final BlockPos PLAYER_STAND = new BlockPos(0, 3, 0);
  private static final int COUNTED_TICKS = 40;
  private static final Vec3d LAUNCH = new Vec3d(0.5, 1.0, -0.25);

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 20)
  public void aDrivenPlayerTakesTheVelocityAndHasTheirFloatingCountCleared(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    player.networkHandler.floatingTicks = COUNTED_TICKS;

    Propel.drive(player, LAUNCH);

    context.assertEquals(
        player.networkHandler.floatingTicks,
        0,
        "Ticks the server still counts a pulled player as floating for");
    context.assertEquals(player.getVelocity(), LAUNCH, "Velocity after the drive");
    context.assertTrue(player.velocityModified, "The new velocity is sent to the client");
    context.complete();
  }
}
