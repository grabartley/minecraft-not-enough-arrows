package com.grahambartley.morearrows.network;

import com.grahambartley.morearrows.config.ClientConfigHolder;
import com.grahambartley.morearrows.hud.CountdownSync;
import com.grahambartley.morearrows.network.CountdownPayloads.CountdownS2CPayload;
import com.grahambartley.morearrows.network.NockedArrowPayloads.NockedArrowS2CPayload;
import com.grahambartley.morearrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import com.grahambartley.morearrows.render.NockedArrowSync;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ModNetworkingClient {

  private ModNetworkingClient() {}

  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(
        SyncServerConfigS2CPayload.ID, ModNetworkingClient::handleSyncServerConfig);
    ClientPlayNetworking.registerGlobalReceiver(
        NockedArrowS2CPayload.ID, ModNetworkingClient::handleNockedArrow);
    ClientPlayNetworking.registerGlobalReceiver(
        CountdownS2CPayload.ID, ModNetworkingClient::handleCountdown);
    ClientEntityEvents.ENTITY_UNLOAD.register(
        (entity, world) -> {
          NockedArrowSync.forget(entity.getId());
          CountdownSync.forget(entity.getId());
        });
    ClientPlayConnectionEvents.DISCONNECT.register(
        (handler, client) -> {
          ClientConfigHolder.clear();
          NockedArrowSync.clear();
          CountdownSync.clear();
        });
  }

  private static void handleCountdown(
      final CountdownS2CPayload payload, final ClientPlayNetworking.Context context) {
    context
        .client()
        .execute(
            () ->
                CountdownSync.accept(
                    payload.carrierId(), payload.delayTicks(), payload.remainingTicks()));
  }

  private static void handleNockedArrow(
      final NockedArrowS2CPayload payload, final ClientPlayNetworking.Context context) {
    context.client().execute(() -> NockedArrowSync.accept(payload.entityId(), payload.arrow()));
  }

  private static void handleSyncServerConfig(
      final SyncServerConfigS2CPayload payload, final ClientPlayNetworking.Context context) {
    context.client().execute(() -> ClientConfigHolder.accept(payload.config()));
  }
}
