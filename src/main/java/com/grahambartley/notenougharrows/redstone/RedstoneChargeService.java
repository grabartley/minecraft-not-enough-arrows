package com.grahambartley.notenougharrows.redstone;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.UtilityArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class RedstoneChargeService {
  private static final Map<RegistryKey<World>, RedstoneChargeTracker> TRACKERS = new HashMap<>();

  private RedstoneChargeService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(RedstoneChargeService::expireChargesIn);
    ServerLifecycleEvents.SERVER_STOPPING.register(RedstoneChargeService::clearEveryCharge);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static boolean charge(
      final ServerWorld world, final BlockPos pos, @Nullable final PlayerEntity shooter) {
    final UtilityArrowConfig utility = ServerConfigService.get().utility();
    return charge(
        world,
        pos,
        shooter,
        utility.redstoneSignalStrength(),
        utility.redstoneSignalDurationTicks());
  }

  public static boolean charge(
      final ServerWorld world,
      final BlockPos pos,
      @Nullable final PlayerEntity shooter,
      final int strength,
      final int durationTicks) {
    if (world == null || pos == null || durationTicks <= 0) {
      return false;
    }
    if (!RedstoneChargePlacer.place(world, pos, strength, shooter)) {
      return false;
    }

    world.scheduleBlockTick(pos, ModBlocks.REDSTONE_CHARGE, durationTicks);
    trackerFor(world).add(new RedstoneCharge(pos, world.getTime() + durationTicks));
    return true;
  }

  public static int remainingTicksAt(final ServerWorld world, final BlockPos pos) {
    if (world == null || pos == null) {
      return 0;
    }
    final RedstoneChargeTracker tracker = TRACKERS.get(world.getRegistryKey());
    final long remaining = tracker == null ? 0L : tracker.remainingAt(pos, world.getTime());
    return (int) Math.min(remaining, Integer.MAX_VALUE);
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void expireChargesIn(final ServerWorld world) {
    final RedstoneChargeTracker tracker = TRACKERS.get(world.getRegistryKey());
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    clear(world, tracker.takeExpired(world.getTime()));
  }

  private static void clearEveryCharge(final MinecraftServer server) {
    if (server == null) {
      return;
    }
    server
        .getWorlds()
        .forEach(
            world -> {
              final RedstoneChargeTracker tracker = TRACKERS.get(world.getRegistryKey());
              if (tracker != null) {
                clear(world, tracker.takeAll());
              }
            });
  }

  private static void clear(final ServerWorld world, final List<BlockPos> positions) {
    positions.forEach(pos -> RedstoneChargePlacer.clear(world, pos));
  }

  private static RedstoneChargeTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new RedstoneChargeTracker());
  }
}
