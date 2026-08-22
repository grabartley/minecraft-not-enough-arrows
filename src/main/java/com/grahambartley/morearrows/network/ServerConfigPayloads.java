package com.grahambartley.morearrows.network;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.config.ConfigCodec;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class ServerConfigPayloads {
  public static final Identifier SYNC_SERVER_CONFIG_ID =
      Identifier.of(MoreArrows.MOD_ID, "sync_server_config");

  private ServerConfigPayloads() {}

  public record SyncServerConfigS2CPayload(MoreArrowsConfig config) implements CustomPayload {
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
}
