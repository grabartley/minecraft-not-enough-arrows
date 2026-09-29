package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.social.CourierPayload;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryOps;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class CourierPayloadGameTest implements FabricGameTest {
  private static final String BATCH = "courier-payload";

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesAnEmptyStack(TestContext context) {
    boolean refused = false;
    try {
      new CourierPayload(ItemStack.EMPTY);
    } catch (final IllegalArgumentException expected) {
      refused = true;
    }
    context.assertTrue(refused, "A payload of nothing should be refused");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void keepsItsOwnCopyOfTheStack(TestContext context) {
    final ItemStack given = new ItemStack(Items.DIAMOND, 5);
    final CourierPayload payload = new CourierPayload(given);

    given.setCount(1);
    payload.stack().setCount(2);

    context.assertEquals(5, payload.count(), "The payload's count");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void payloadsOfEqualStacksAreEqualSoLoadedArrowsStack(TestContext context) {
    final CourierPayload first = new CourierPayload(new ItemStack(Items.DIAMOND, 5));
    final CourierPayload second = new CourierPayload(new ItemStack(Items.DIAMOND, 5));

    context.assertTrue(first.equals(second), "Equal stacks make equal payloads");
    context.assertEquals(first.hashCode(), second.hashCode(), "and equal hashes");
    context.assertFalse(
        first.equals(new CourierPayload(new ItemStack(Items.DIAMOND, 6))),
        "A different count is a different payload");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void survivesTheSaveCodecWithItsComponents(TestContext context) {
    final ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
    sword.setDamage(9);
    final CourierPayload payload = new CourierPayload(sword);
    final RegistryOps<com.google.gson.JsonElement> ops =
        context.getWorld().getRegistryManager().getOps(JsonOps.INSTANCE);

    final CourierPayload decoded =
        CourierPayload.CODEC
            .parse(ops, CourierPayload.CODEC.encodeStart(ops, payload).getOrThrow())
            .getOrThrow();

    context.assertTrue(payload.equals(decoded), "The payload after a save round trip");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void survivesThePacketCodec(TestContext context) {
    final CourierPayload payload = new CourierPayload(new ItemStack(Items.EMERALD, 12));
    final RegistryByteBuf buf =
        new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());

    CourierPayload.PACKET_CODEC.encode(buf, payload);

    context.assertTrue(
        payload.equals(CourierPayload.PACKET_CODEC.decode(buf)), "The payload after the wire");
    context.complete();
  }
}
