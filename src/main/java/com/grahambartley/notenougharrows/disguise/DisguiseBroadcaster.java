package com.grahambartley.notenougharrows.disguise;

import com.grahambartley.notenougharrows.network.DisguisePayloads.DisguiseS2CPayload;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class DisguiseBroadcaster {

  private DisguiseBroadcaster() {}

  public static void register() {
    EntityTrackingEvents.START_TRACKING.register(
        (tracked, viewer) -> catchUpFor(tracked).ifPresent(payload -> send(viewer, payload)));
  }

  public static Optional<DisguiseS2CPayload> catchUpFor(final Entity tracked) {
    if (!(tracked.getWorld() instanceof ServerWorld world)) {
      return Optional.empty();
    }
    return DisguiseService.formOf(world, tracked.getUuid())
        .map(form -> DisguiseS2CPayload.wearing(tracked.getId(), form));
  }

  static void wearing(final Entity disguised, final Identifier form) {
    announce(disguised, DisguiseS2CPayload.wearing(disguised.getId(), form));
  }

  static void restored(final Entity disguised) {
    announce(disguised, DisguiseS2CPayload.restored(disguised.getId()));
  }

  private static void announce(final Entity disguised, final DisguiseS2CPayload payload) {
    PlayerLookup.tracking(disguised).forEach(viewer -> send(viewer, payload));
  }

  private static void send(final ServerPlayerEntity viewer, final DisguiseS2CPayload payload) {
    if (ServerPlayNetworking.canSend(viewer, DisguiseS2CPayload.ID)) {
      ServerPlayNetworking.send(viewer, payload);
    }
  }
}
