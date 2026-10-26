package com.grahambartley.volley.network;

import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ServerConfigUpdateSender {

  private ServerConfigUpdateSender() {}

  public static boolean send(final VolleyConfig config) {
    if (config == null || !ClientPlayNetworking.canSend(UpdateServerConfigC2SPayload.ID)) {
      return false;
    }
    ClientPlayNetworking.send(new UpdateServerConfigC2SPayload(config));
    return true;
  }
}
