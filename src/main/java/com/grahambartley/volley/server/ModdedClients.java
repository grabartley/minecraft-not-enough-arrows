package com.grahambartley.volley.server;

import com.grahambartley.volley.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ModdedClients {
  private ModdedClients() {}

  public static boolean hasTheMod(final ServerPlayNetworkHandler connection) {
    return ServerPlayNetworking.canSend(connection, SyncServerConfigS2CPayload.ID);
  }

  public static void send(final ServerPlayerEntity viewer, final CustomPayload payload) {
    if (ServerPlayNetworking.canSend(viewer, payload.getId())) {
      ServerPlayNetworking.send(viewer, payload);
    }
  }

  public static void sendAll(
      final Iterable<? extends ServerPlayerEntity> viewers, final CustomPayload payload) {
    viewers.forEach(viewer -> send(viewer, payload));
  }
}
