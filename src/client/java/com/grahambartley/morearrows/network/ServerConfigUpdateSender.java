package com.grahambartley.morearrows.network;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ServerConfigUpdateSender {

  private ServerConfigUpdateSender() {}

  public static boolean send(final MoreArrowsConfig config) {
    if (config == null || !ClientPlayNetworking.canSend(UpdateServerConfigC2SPayload.ID)) {
      return false;
    }
    ClientPlayNetworking.send(new UpdateServerConfigC2SPayload(config));
    return true;
  }
}
