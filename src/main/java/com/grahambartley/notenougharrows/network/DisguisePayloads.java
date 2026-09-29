package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class DisguisePayloads {
  public static final Identifier DISGUISE_ID = Identifier.of(NotEnoughArrows.MOD_ID, "disguise");

  private DisguisePayloads() {}

  public record DisguiseS2CPayload(int entityId, Optional<Identifier> form)
      implements CustomPayload {
    public static final CustomPayload.Id<DisguiseS2CPayload> ID =
        new CustomPayload.Id<>(DISGUISE_ID);
    public static final PacketCodec<RegistryByteBuf, DisguiseS2CPayload> CODEC =
        PacketCodec.of(DisguiseS2CPayload::write, DisguiseS2CPayload::read);

    public DisguiseS2CPayload {
      Objects.requireNonNull(form, "form");
    }

    public static DisguiseS2CPayload wearing(final int entityId, final Identifier form) {
      return new DisguiseS2CPayload(entityId, Optional.of(form));
    }

    public static DisguiseS2CPayload restored(final int entityId) {
      return new DisguiseS2CPayload(entityId, Optional.empty());
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeVarInt(entityId);
      buf.writeOptional(form, (out, id) -> out.writeIdentifier(id));
    }

    private static DisguiseS2CPayload read(final RegistryByteBuf buf) {
      return new DisguiseS2CPayload(buf.readVarInt(), buf.readOptional(in -> in.readIdentifier()));
    }
  }
}
