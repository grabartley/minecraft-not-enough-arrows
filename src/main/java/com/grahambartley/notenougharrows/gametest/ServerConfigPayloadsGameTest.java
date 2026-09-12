package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.PhysicsArrowConfig;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ServerConfigPayloadsGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void syncPayloadRoundTripsDefaultsAcrossTheWire(TestContext context) {
    final NotEnoughArrowsConfig original = NotEnoughArrowsConfig.defaults();

    context.assertTrue(
        original.equals(roundTrip(context, original)),
        "Default config should survive a wire round trip unchanged");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void syncPayloadPreservesEveryFamilyAcrossTheWire(TestContext context) {
    final NotEnoughArrowsConfig original =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(127, 3.9f, 3.9f, false, false, 127, true))
            .withPhysics(
                new PhysicsArrowConfig(
                    8, List.of("minecraft:bedrock", "minecraft:obsidian"), 16, false));
    final NotEnoughArrowsConfig received = roundTrip(context, original);

    context.assertTrue(
        original.grapple().equals(received.grapple()),
        "Grapple family should survive a wire round trip, but was " + received.grapple());
    context.assertTrue(
        original.physics().equals(received.physics()),
        "Physics family should survive a wire round trip, but was " + received.physics());
    context.assertTrue(
        original.explosive().equals(received.explosive()),
        "Explosive family should survive a wire round trip untouched");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void syncPayloadCarriesTheBlockExclusionListAcrossTheWire(TestContext context) {
    final NotEnoughArrowsConfig original =
        NotEnoughArrowsConfig.defaults()
            .withPhysics(new PhysicsArrowConfig(0, List.of("minecraft:bedrock"), 3, true));

    context.assertTrue(
        roundTrip(context, original).physics().isExcludedFromGravity("minecraft:bedrock"),
        "Block exclusions should reach the client intact");
    context.complete();
  }

  private static NotEnoughArrowsConfig roundTrip(
      final TestContext context, final NotEnoughArrowsConfig config) {
    final RegistryByteBuf buf =
        new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
    SyncServerConfigS2CPayload.CODEC.encode(buf, new SyncServerConfigS2CPayload(config));
    return SyncServerConfigS2CPayload.CODEC.decode(buf).config();
  }
}
