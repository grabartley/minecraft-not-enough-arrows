package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.countdown.CountdownKind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class CountdownPayloads {
  public static final Identifier COUNTDOWN_ID = Identifier.of(NotEnoughArrows.MOD_ID, "countdown");

  private CountdownPayloads() {}

  public record CountdownS2CPayload(
      int carrierId, int delayTicks, int remainingTicks, CountdownKind kind)
      implements CustomPayload {
    public static final CustomPayload.Id<CountdownS2CPayload> ID =
        new CustomPayload.Id<>(COUNTDOWN_ID);
    public static final PacketCodec<RegistryByteBuf, CountdownS2CPayload> CODEC =
        PacketCodec.of(CountdownS2CPayload::write, CountdownS2CPayload::read);

    public CountdownS2CPayload(
        final int carrierId, final int delayTicks, final int remainingTicks) {
      this(carrierId, delayTicks, remainingTicks, CountdownKind.FUSE);
    }

    public static CountdownS2CPayload ended(final int carrierId) {
      return new CountdownS2CPayload(carrierId, 0, 0);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeVarInt(carrierId);
      buf.writeVarInt(delayTicks);
      buf.writeVarInt(remainingTicks);
      buf.writeVarInt(kind.ordinal());
    }

    private static CountdownS2CPayload read(final RegistryByteBuf buf) {
      return new CountdownS2CPayload(
          buf.readVarInt(),
          buf.readVarInt(),
          buf.readVarInt(),
          CountdownKind.fromOrdinal(buf.readVarInt()));
    }
  }
}
