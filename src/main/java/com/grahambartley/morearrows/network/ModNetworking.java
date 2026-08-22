package com.grahambartley.morearrows.network;

import com.grahambartley.morearrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetworking {

  private ModNetworking() {}

  public static void registerPayloads() {
    PayloadTypeRegistry.playS2C()
        .register(SyncServerConfigS2CPayload.ID, SyncServerConfigS2CPayload.CODEC);
  }
}
