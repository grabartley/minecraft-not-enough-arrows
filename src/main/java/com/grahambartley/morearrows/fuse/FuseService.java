package com.grahambartley.morearrows.fuse;

import com.grahambartley.morearrows.ModSounds;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class FuseService {
  private static final float BEEP_PITCH = 1.0f;

  private static final Map<RegistryKey<World>, FuseTracker> TRACKERS = new HashMap<>();
  private static final List<FuseExpiry> LISTENERS = new CopyOnWriteArrayList<>();

  private FuseService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(FuseService::burnDownFusesIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (entity, damageSource) -> extinguishEverywhere(entity.getUuid()));
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> extinguishEverywhere(handler.getPlayer().getUuid()));
  }

  public static void whenExpired(@Nullable final FuseExpiry listener) {
    if (listener != null) {
      LISTENERS.add(listener);
    }
  }

  @Nullable
  public static Fuse light(
      @Nullable final ServerWorld world, @Nullable final Entity host, final int delayTicks) {
    if (world == null || host == null || host.isRemoved() || delayTicks < 0) {
      return null;
    }

    final Fuse fuse = Fuse.lit(host.getUuid(), delayTicks);
    if (fuse.isInstant()) {
      dispatchExpiry(world, host, fuse);
      return null;
    }

    trackerFor(world).add(fuse);
    if (fuse.beepsNow()) {
      beep(world, host);
    }
    return fuse;
  }

  @Nullable
  public static Fuse fuseOn(@Nullable final ServerWorld world, @Nullable final UUID hostId) {
    final FuseTracker tracker = trackerIn(world);
    return tracker == null ? null : tracker.fuseOn(hostId);
  }

  @Nullable
  public static Fuse extinguish(@Nullable final ServerWorld world, @Nullable final UUID hostId) {
    final FuseTracker tracker = trackerIn(world);
    return tracker == null ? null : tracker.remove(hostId);
  }

  public static void extinguishEverywhere(@Nullable final UUID hostId) {
    TRACKERS.values().forEach(tracker -> tracker.remove(hostId));
  }

  public static List<Fuse> fusesIn(@Nullable final ServerWorld world) {
    final FuseTracker tracker = trackerIn(world);
    return tracker == null ? List.of() : tracker.fuses();
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void burnDownFusesIn(final ServerWorld world) {
    final FuseTracker tracker = trackerIn(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }

    for (final Fuse snapshot : tracker.fuses()) {
      final Fuse fuse = tracker.fuseOn(snapshot.hostId());
      if (fuse == null) {
        continue;
      }
      final Entity host = world.getEntity(fuse.hostId());
      if (host == null || host.isRemoved()) {
        holdWithoutHost(tracker, fuse);
      } else {
        burnDown(world, tracker, host, fuse);
      }
    }
  }

  private static void burnDown(
      final ServerWorld world, final FuseTracker tracker, final Entity host, final Fuse fuse) {
    final Fuse burned = fuse.burned();
    if (burned.hasExpired()) {
      tracker.remove(burned.hostId());
      dispatchExpiry(world, host, burned);
      return;
    }

    tracker.add(burned);
    if (burned.beepsNow()) {
      beep(world, host);
    }
  }

  private static void holdWithoutHost(final FuseTracker tracker, final Fuse fuse) {
    final Fuse held = fuse.lost();
    if (held.isAbandoned()) {
      tracker.remove(held.hostId());
    } else {
      tracker.add(held);
    }
  }

  private static void beep(final ServerWorld world, final Entity host) {
    final float volume = ServerConfigService.get().explosive().beepVolume();
    if (volume <= 0f) {
      return;
    }

    world.playSound(
        null,
        host.getX(),
        host.getY(),
        host.getZ(),
        ModSounds.COUNTDOWN_BEEP,
        SoundCategory.NEUTRAL,
        volume,
        BEEP_PITCH);
  }

  private static void dispatchExpiry(final ServerWorld world, final Entity host, final Fuse fuse) {
    LISTENERS.forEach(listener -> listener.onFuseExpired(world, host, fuse));
  }

  @Nullable
  private static FuseTracker trackerIn(@Nullable final ServerWorld world) {
    return world == null ? null : TRACKERS.get(world.getRegistryKey());
  }

  private static FuseTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new FuseTracker());
  }
}
