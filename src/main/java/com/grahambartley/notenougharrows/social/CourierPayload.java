package com.grahambartley.notenougharrows.social;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class CourierPayload {
  public static final Codec<CourierPayload> CODEC =
      ItemStack.CODEC.xmap(CourierPayload::new, CourierPayload::stack);
  public static final PacketCodec<RegistryByteBuf, CourierPayload> PACKET_CODEC =
      ItemStack.PACKET_CODEC.xmap(CourierPayload::new, CourierPayload::stack);

  private final ItemStack stack;

  public CourierPayload(final ItemStack stack) {
    Objects.requireNonNull(stack, "stack");
    if (stack.isEmpty()) {
      throw new IllegalArgumentException("A courier payload must not be empty");
    }
    this.stack = stack.copy();
  }

  public ItemStack stack() {
    return stack.copy();
  }

  public int count() {
    return stack.getCount();
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof CourierPayload payload && ItemStack.areEqual(stack, payload.stack);
  }

  @Override
  public int hashCode() {
    return ItemStack.hashCode(stack);
  }

  @Override
  public String toString() {
    return "CourierPayload[" + stack + "]";
  }
}
