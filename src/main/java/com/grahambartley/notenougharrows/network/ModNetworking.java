package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.network.CountdownPayloads.CountdownS2CPayload;
import com.grahambartley.notenougharrows.network.NockedArrowPayloads.NockedArrowS2CPayload;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetworking {

  private ModNetworking() {}

  public static void registerPayloads() {
    PayloadTypeRegistry.playS2C()
        .register(SyncServerConfigS2CPayload.ID, SyncServerConfigS2CPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(NockedArrowS2CPayload.ID, NockedArrowS2CPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(CountdownS2CPayload.ID, CountdownS2CPayload.CODEC);
    PayloadTypeRegistry.playC2S()
        .register(UpdateServerConfigC2SPayload.ID, UpdateServerConfigC2SPayload.CODEC);
  }
}
