package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class NockedArrowPayloads {
  public static final Identifier NOCKED_ARROW_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "nocked_arrow");

  private NockedArrowPayloads() {}

  public record NockedArrowS2CPayload(int entityId, ItemStack arrow) implements CustomPayload {
    public static final CustomPayload.Id<NockedArrowS2CPayload> ID =
        new CustomPayload.Id<>(NOCKED_ARROW_ID);
    public static final PacketCodec<RegistryByteBuf, NockedArrowS2CPayload> CODEC =
        PacketCodec.of(NockedArrowS2CPayload::write, NockedArrowS2CPayload::read);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeVarInt(entityId);
      ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, arrow);
    }

    private static NockedArrowS2CPayload read(final RegistryByteBuf buf) {
      return new NockedArrowS2CPayload(
          buf.readVarInt(), ItemStack.OPTIONAL_PACKET_CODEC.decode(buf));
    }
  }
}
