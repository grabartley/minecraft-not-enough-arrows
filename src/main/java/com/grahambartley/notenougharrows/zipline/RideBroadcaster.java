package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.network.RidePayloads.RideS2CPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class RideBroadcaster {

  private RideBroadcaster() {}

  public static void register() {
    EntityTrackingEvents.START_TRACKING.register(
        (tracked, viewer) -> catchUpFor(tracked).ifPresent(payload -> send(viewer, payload)));
  }

  public static void started(final ServerPlayerEntity rider) {
    announce(rider, true);
  }

  public static void stopped(final ServerPlayerEntity rider) {
    announce(rider, false);
  }

  public static Optional<RideS2CPayload> catchUpFor(final Entity tracked) {
    if (!(tracked instanceof ServerPlayerEntity rider)
        || !(rider.getWorld() instanceof ServerWorld world)
        || RideService.rideOf(world, rider.getUuid()) == null) {
      return Optional.empty();
    }
    return Optional.of(new RideS2CPayload(rider.getId(), true));
  }

  public static List<ServerPlayerEntity> audienceOf(final ServerPlayerEntity rider) {
    final List<ServerPlayerEntity> audience = new ArrayList<>(PlayerLookup.tracking(rider));
    if (!audience.contains(rider)) {
      audience.add(rider);
    }
    return List.copyOf(audience);
  }

  private static void announce(final ServerPlayerEntity rider, final boolean riding) {
    final RideS2CPayload payload = new RideS2CPayload(rider.getId(), riding);
    audienceOf(rider).forEach(viewer -> send(viewer, payload));
  }

  private static void send(final ServerPlayerEntity viewer, final RideS2CPayload payload) {
    if (ServerPlayNetworking.canSend(viewer, RideS2CPayload.ID)) {
      ServerPlayNetworking.send(viewer, payload);
    }
  }
}
