package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.network.RidePayloads.RideS2CPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class RidePayloadsGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-ride-payload";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aRideMessageSurvivesTheWire(TestContext context) {
    for (final RideS2CPayload sent :
        new RideS2CPayload[] {new RideS2CPayload(4321, true), new RideS2CPayload(7, false)}) {
      final RegistryByteBuf buf =
          new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
      RideS2CPayload.CODEC.encode(buf, sent);

      context.assertEquals(sent, RideS2CPayload.CODEC.decode(buf), "Round trip");
    }
    context.complete();
  }
}
