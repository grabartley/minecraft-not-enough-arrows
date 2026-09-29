package com.grahambartley.notenougharrows.reveal;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.world.LoadedGround;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class WatcherService {
  private static final Map<RegistryKey<World>, WatcherLedger> LEDGERS = new HashMap<>();
  private static final float ALERT_VOLUME = 1.0f;
  private static final float ALERT_PITCH = 1.0f;

  private WatcherService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(WatcherService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static Watcher place(
      final ServerWorld world,
      final BlockPos pos,
      @Nullable final PlayerEntity owner,
      final int lifetimeTicks,
      final int reportIntervalTicks) {
    final Watcher watcher =
        Watcher.placed(
            owner == null ? null : owner.getUuid(),
            pos,
            world.getTime(),
            lifetimeTicks,
            reportIntervalTicks);
    LEDGERS.computeIfAbsent(world.getRegistryKey(), key -> new WatcherLedger()).add(watcher);
    return watcher;
  }

  public static List<Watcher> in(final ServerWorld world) {
    final WatcherLedger ledger = LEDGERS.get(world.getRegistryKey());
    return ledger == null ? List.of() : ledger.all();
  }

  public static List<WatcherAlarm> sweep(
      final ServerWorld world, final long tick, final Predicate<BlockPos> isLoaded) {
    final WatcherLedger ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger == null || ledger.isEmpty()) {
      return List.of();
    }
    ledger.dropWhere(tick, isLoaded);
    final List<WatcherAlarm> alarms = new ArrayList<>();
    for (final Watcher watcher : ledger.all()) {
      if (!watcher.canReportAt(tick)) {
        continue;
      }
      crosserOf(world, watcher)
          .ifPresent(
              crosser -> {
                alarms.add(new WatcherAlarm(watcher.owner(), watcher.pos(), crosser));
                ledger.replace(watcher.reportedAt(tick));
              });
    }
    return List.copyOf(alarms);
  }

  public static void forget() {
    LEDGERS.clear();
  }

  private static Optional<LivingEntity> crosserOf(final ServerWorld world, final Watcher watcher) {
    return world
        .getEntitiesByClass(
            LivingEntity.class,
            new Box(watcher.pos()),
            living ->
                living.isAlive() && !living.isSpectator() && !watcher.ignores(living.getUuid()))
        .stream()
        .findFirst();
  }

  private static void tick(final ServerWorld world) {
    sweep(world, world.getTime(), LoadedGround.in(world)).forEach(alarm -> report(world, alarm));
  }

  private static void report(final ServerWorld world, final WatcherAlarm alarm) {
    final ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(alarm.owner());
    if (owner == null) {
      return;
    }
    owner.sendMessage(WatcherReport.of(alarm, owner), false);
    ModSoundPlayer.playTo(owner, ModSounds.TRIPWIRE_ARROW_ALERT, ALERT_VOLUME, ALERT_PITCH);
  }
}
