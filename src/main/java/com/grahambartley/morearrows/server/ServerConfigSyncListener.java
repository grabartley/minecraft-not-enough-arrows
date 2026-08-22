package com.grahambartley.morearrows.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class ServerConfigSyncListener {

  private ServerConfigSyncListener() {}

  public static void register() {
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> ServerConfigService.syncTo(handler.getPlayer()));
  }
}
