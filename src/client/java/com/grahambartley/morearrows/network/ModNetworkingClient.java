package com.grahambartley.morearrows.network;

import com.grahambartley.morearrows.config.ClientConfigHolder;
import com.grahambartley.morearrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ModNetworkingClient {

  private ModNetworkingClient() {}

  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(
        SyncServerConfigS2CPayload.ID, ModNetworkingClient::handleSyncServerConfig);
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientConfigHolder.clear());
  }

  private static void handleSyncServerConfig(
      final SyncServerConfigS2CPayload payload, final ClientPlayNetworking.Context context) {
    context.client().execute(() -> ClientConfigHolder.accept(payload.config()));
  }
}
