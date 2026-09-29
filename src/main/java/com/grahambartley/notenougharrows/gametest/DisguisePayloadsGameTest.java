package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.network.DisguisePayloads.DisguiseS2CPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class DisguisePayloadsGameTest implements FabricGameTest {
  private static final String BATCH = "disguise-payload";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aDisguiseMessageSurvivesTheWire(TestContext context) {
    for (final DisguiseS2CPayload sent :
        new DisguiseS2CPayload[] {
          DisguiseS2CPayload.wearing(4321, Identifier.ofVanilla("sheep")),
          DisguiseS2CPayload.restored(7)
        }) {
      final RegistryByteBuf buf =
          new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
      DisguiseS2CPayload.CODEC.encode(buf, sent);

      context.assertEquals(sent, DisguiseS2CPayload.CODEC.decode(buf), "Round trip");
    }
    context.complete();
  }
}
