package com.grahambartley.volley.server;

import com.grahambartley.volley.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public final class ModdedClients {
  private ModdedClients() {}

  public static boolean hasTheMod(final ServerPlayNetworkHandler connection) {
    return ServerPlayNetworking.canSend(connection, SyncServerConfigS2CPayload.ID);
  }
}
