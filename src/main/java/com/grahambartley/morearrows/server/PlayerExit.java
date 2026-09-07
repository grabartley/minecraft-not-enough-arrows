package com.grahambartley.morearrows.server;

import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerExit {

  private PlayerExit() {}

  public static void whenLeaving(final Consumer<UUID> cleanUp) {
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (entity, damageSource) -> {
          if (entity instanceof ServerPlayerEntity player) {
            cleanUp.accept(player.getUuid());
          }
        });
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> {
          final ServerPlayerEntity player = handler.getPlayer();
          if (player != null) {
            cleanUp.accept(player.getUuid());
          }
        });
  }
}
