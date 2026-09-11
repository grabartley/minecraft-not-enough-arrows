package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ServerConfigUpdateSender {

  private ServerConfigUpdateSender() {}

  public static boolean send(final NotEnoughArrowsConfig config) {
    if (config == null || !ClientPlayNetworking.canSend(UpdateServerConfigC2SPayload.ID)) {
      return false;
    }
    ClientPlayNetworking.send(new UpdateServerConfigC2SPayload(config));
    return true;
  }
}
