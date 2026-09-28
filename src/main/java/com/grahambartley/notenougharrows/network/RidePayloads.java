package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class RidePayloads {
  public static final Identifier RIDE_ID = Identifier.of(NotEnoughArrows.MOD_ID, "zipline_ride");

  private RidePayloads() {}

  public record RideS2CPayload(int riderId, boolean riding) implements CustomPayload {
    public static final CustomPayload.Id<RideS2CPayload> ID = new CustomPayload.Id<>(RIDE_ID);
    public static final PacketCodec<RegistryByteBuf, RideS2CPayload> CODEC =
        PacketCodec.of(RideS2CPayload::write, RideS2CPayload::read);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeVarInt(riderId);
      buf.writeBoolean(riding);
    }

    private static RideS2CPayload read(final RegistryByteBuf buf) {
      return new RideS2CPayload(buf.readVarInt(), buf.readBoolean());
    }
  }
}
