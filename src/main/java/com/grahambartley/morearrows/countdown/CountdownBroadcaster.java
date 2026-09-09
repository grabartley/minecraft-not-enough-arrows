package com.grahambartley.morearrows.countdown;

import com.grahambartley.morearrows.countdown.CountdownChanges.Announced;
import com.grahambartley.morearrows.fuse.Fuse;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.network.CountdownPayloads.CountdownS2CPayload;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class CountdownBroadcaster {
  private static final Map<RegistryKey<World>, CountdownChanges> ANNOUNCED = new HashMap<>();

  private CountdownBroadcaster() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(CountdownBroadcaster::announceChangesIn);
    EntityTrackingEvents.START_TRACKING.register(CountdownBroadcaster::catchUp);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static boolean isAnnouncing(@Nullable final ServerWorld world, final UUID carrier) {
    final CountdownChanges changes = changesIn(world);
    return changes != null && changes.knows(carrier);
  }

  public static void forget() {
    ANNOUNCED.clear();
  }

  private static void announceChangesIn(final ServerWorld world) {
    final CountdownChanges changes = changesFor(world);
    final Set<UUID> burning = new HashSet<>();

    for (final Fuse fuse : FuseService.fusesIn(world)) {
      final Entity carrier = world.getEntity(fuse.hostId());
      if (carrier == null) {
        continue;
      }

      burning.add(fuse.hostId());
      if (changes.record(fuse.hostId(), carrier.getId(), fuse.delayTicks())) {
        announceTo(watchersOf(carrier), payloadFor(carrier, fuse));
      }
    }

    endEverythingNotIn(world, changes, burning);
  }

  private static void endEverythingNotIn(
      final ServerWorld world, final CountdownChanges changes, final Set<UUID> burning) {
    for (final UUID carrier : changes.announced()) {
      if (burning.contains(carrier)) {
        continue;
      }

      final Announced announced = changes.forget(carrier);
      if (announced != null) {
        announceTo(PlayerLookup.world(world), CountdownS2CPayload.ended(announced.carrierId()));
      }
    }
  }

  private static void catchUp(final Entity tracked, final ServerPlayerEntity viewer) {
    if (!(tracked.getWorld() instanceof ServerWorld world)) {
      return;
    }

    final Fuse fuse = FuseService.fuseOn(world, tracked.getUuid());
    if (fuse != null && !fuse.hasExpired()) {
      announceTo(List.of(viewer), payloadFor(tracked, fuse));
    }
  }

  private static CountdownS2CPayload payloadFor(final Entity carrier, final Fuse fuse) {
    return new CountdownS2CPayload(carrier.getId(), fuse.delayTicks(), fuse.remainingTicks());
  }

  private static Iterable<ServerPlayerEntity> watchersOf(final Entity carrier) {
    final Set<ServerPlayerEntity> watchers = new HashSet<>(PlayerLookup.tracking(carrier));
    if (carrier instanceof ServerPlayerEntity carrying) {
      watchers.add(carrying);
    }
    return watchers;
  }

  private static void announceTo(
      final Iterable<ServerPlayerEntity> viewers, final CountdownS2CPayload payload) {
    for (final ServerPlayerEntity viewer : viewers) {
      if (ServerPlayNetworking.canSend(viewer, CountdownS2CPayload.ID)) {
        ServerPlayNetworking.send(viewer, payload);
      }
    }
  }

  @Nullable
  private static CountdownChanges changesIn(@Nullable final ServerWorld world) {
    return world == null ? null : ANNOUNCED.get(world.getRegistryKey());
  }

  private static CountdownChanges changesFor(final ServerWorld world) {
    return ANNOUNCED.computeIfAbsent(world.getRegistryKey(), key -> new CountdownChanges());
  }
}
