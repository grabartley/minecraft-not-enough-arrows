package com.grahambartley.morearrows.fire;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class FirePatchService {
  private static final Map<RegistryKey<World>, FirePatchTracker> TRACKERS = new HashMap<>();

  private FirePatchService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(FirePatchService::expirePatchesIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static List<BlockPos> ignite(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity igniter) {
    final ExplosiveArrowConfig explosive = ServerConfigService.get().explosive();
    return ignite(
        world, center, igniter, explosive.firePatchRadius(), explosive.firePatchDurationTicks());
  }

  public static List<BlockPos> ignite(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity igniter,
      final int radius,
      final int durationTicks) {
    if (world == null || center == null || durationTicks <= 0) {
      return List.of();
    }

    final List<BlockPos> placed =
        FirePatchPlacer.place(world, FirePatchShape.columns(center, radius), igniter);
    if (placed.isEmpty()) {
      return List.of();
    }

    trackerFor(world).add(new FirePatch(placed, world.getTime() + durationTicks));
    return placed;
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void expirePatchesIn(final ServerWorld world) {
    final FirePatchTracker tracker = TRACKERS.get(world.getRegistryKey());
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    extinguish(world, tracker.takeExpired(world.getTime()));
  }

  private static void extinguish(final ServerWorld world, final List<BlockPos> positions) {
    positions.forEach(pos -> FirePatchPlacer.clear(world, pos));
  }

  private static FirePatchTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new FirePatchTracker());
  }
}
