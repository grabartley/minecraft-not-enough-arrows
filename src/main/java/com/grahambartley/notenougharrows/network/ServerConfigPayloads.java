package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.config.ConfigCodec;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class ServerConfigPayloads {
  public static final Identifier SYNC_SERVER_CONFIG_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "sync_server_config");
  public static final Identifier UPDATE_SERVER_CONFIG_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "update_server_config");

  private ServerConfigPayloads() {}

  public record SyncServerConfigS2CPayload(NotEnoughArrowsConfig config) implements CustomPayload {
    public static final CustomPayload.Id<SyncServerConfigS2CPayload> ID =
        new CustomPayload.Id<>(SYNC_SERVER_CONFIG_ID);
    public static final PacketCodec<RegistryByteBuf, SyncServerConfigS2CPayload> CODEC =
        PacketCodec.of(SyncServerConfigS2CPayload::write, SyncServerConfigS2CPayload::read);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeString(ConfigCodec.encode(config), ConfigCodec.MAX_ENCODED_LENGTH);
    }

    private static SyncServerConfigS2CPayload read(final RegistryByteBuf buf) {
      return new SyncServerConfigS2CPayload(
          ConfigCodec.decodeOrDefaults(buf.readString(ConfigCodec.MAX_ENCODED_LENGTH)));
    }
  }

  public record UpdateServerConfigC2SPayload(NotEnoughArrowsConfig config)
      implements CustomPayload {
    public static final CustomPayload.Id<UpdateServerConfigC2SPayload> ID =
        new CustomPayload.Id<>(UPDATE_SERVER_CONFIG_ID);
    public static final PacketCodec<RegistryByteBuf, UpdateServerConfigC2SPayload> CODEC =
        PacketCodec.of(UpdateServerConfigC2SPayload::write, UpdateServerConfigC2SPayload::read);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeString(ConfigCodec.encode(config), ConfigCodec.MAX_ENCODED_LENGTH);
    }

    private static UpdateServerConfigC2SPayload read(final RegistryByteBuf buf) {
      return new UpdateServerConfigC2SPayload(
          ConfigCodec.decodeOrDefaults(buf.readString(ConfigCodec.MAX_ENCODED_LENGTH)));
    }
  }
}
