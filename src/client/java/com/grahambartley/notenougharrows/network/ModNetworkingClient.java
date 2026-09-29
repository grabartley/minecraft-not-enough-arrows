package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.config.ClientConfigHolder;
import com.grahambartley.notenougharrows.disguise.DisguiseStandIns;
import com.grahambartley.notenougharrows.disguise.DisguiseSync;
import com.grahambartley.notenougharrows.hud.CountdownSync;
import com.grahambartley.notenougharrows.network.CountdownPayloads.CountdownS2CPayload;
import com.grahambartley.notenougharrows.network.DisguisePayloads.DisguiseS2CPayload;
import com.grahambartley.notenougharrows.network.NockedArrowPayloads.NockedArrowS2CPayload;
import com.grahambartley.notenougharrows.network.RevealPayloads.BlockOutlineS2CPayload;
import com.grahambartley.notenougharrows.network.RevealPayloads.TracerPathS2CPayload;
import com.grahambartley.notenougharrows.network.RidePayloads.RideS2CPayload;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import com.grahambartley.notenougharrows.render.NockedArrowSync;
import com.grahambartley.notenougharrows.render.RidingPlayers;
import com.grahambartley.notenougharrows.reveal.RevealSync;
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
    ClientPlayNetworking.registerGlobalReceiver(RideS2CPayload.ID, ModNetworkingClient::handleRide);
    ClientPlayNetworking.registerGlobalReceiver(
        BlockOutlineS2CPayload.ID, ModNetworkingClient::handleBlockOutline);
    ClientPlayNetworking.registerGlobalReceiver(
        TracerPathS2CPayload.ID, ModNetworkingClient::handleTracerPath);
    ClientPlayNetworking.registerGlobalReceiver(
        DisguiseS2CPayload.ID, ModNetworkingClient::handleDisguise);
    ClientEntityEvents.ENTITY_UNLOAD.register(
        (entity, world) -> {
          NockedArrowSync.forget(entity.getId());
          CountdownSync.forget(entity.getId());
          RidingPlayers.forget(entity.getId());
          DisguiseSync.forget(entity.getId());
          DisguiseStandIns.forget(entity.getId());
        });
    ClientPlayConnectionEvents.DISCONNECT.register(
        (handler, client) -> {
          ClientConfigHolder.clear();
          NockedArrowSync.clear();
          CountdownSync.clear();
          RidingPlayers.clear();
          RevealSync.clear();
          DisguiseSync.clear();
          DisguiseStandIns.clear();
        });
  }

  private static void handleCountdown(
      final CountdownS2CPayload payload, final ClientPlayNetworking.Context context) {
    context
        .client()
        .execute(
            () ->
                CountdownSync.accept(
                    payload.carrierId(),
                    payload.delayTicks(),
                    payload.remainingTicks(),
                    payload.kind()));
  }

  private static void handleRide(
      final RideS2CPayload payload, final ClientPlayNetworking.Context context) {
    context.client().execute(() -> RidingPlayers.accept(payload.riderId(), payload.riding()));
  }

  private static void handleDisguise(
      final DisguiseS2CPayload payload, final ClientPlayNetworking.Context context) {
    context.client().execute(() -> DisguiseSync.accept(payload.entityId(), payload.form()));
  }

  private static void handleBlockOutline(
      final BlockOutlineS2CPayload payload, final ClientPlayNetworking.Context context) {
    context
        .client()
        .execute(
            () -> {
              RevealSync.enterWorld(context.client().world);
              RevealSync.acceptOutline(payload.blocks(), payload.durationTicks());
            });
  }

  private static void handleTracerPath(
      final TracerPathS2CPayload payload, final ClientPlayNetworking.Context context) {
    context
        .client()
        .execute(
            () -> {
              RevealSync.enterWorld(context.client().world);
              RevealSync.acceptPath(payload.points(), payload.lifetimeTicks());
            });
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
