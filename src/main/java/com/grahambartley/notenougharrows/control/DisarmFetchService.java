package com.grahambartley.notenougharrows.control;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class DisarmFetchService {
  public static final int FETCH_WINDOW_TICKS = 600;

  private static final Map<RegistryKey<World>, Map<UUID, Long>> WINDOWS = new HashMap<>();

  private DisarmFetchService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(DisarmFetchService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> closeEveryWindow());
  }

  public static void letThemFetchItBack(final ServerWorld world, final LivingEntity target) {
    if (!(target instanceof MobEntity mob) || mob.canPickUpLoot()) {
      return;
    }
    mob.setCanPickUpLoot(true);
    windowsIn(world).put(mob.getUuid(), world.getTime() + FETCH_WINDOW_TICKS);
  }

  public static boolean isFetching(final ServerWorld world, final LivingEntity target) {
    final Map<UUID, Long> open = WINDOWS.get(world.getRegistryKey());
    return open != null && open.containsKey(target.getUuid());
  }

  private static void closeEveryWindow() {
    WINDOWS.clear();
  }

  private static void tick(final ServerWorld world) {
    final Map<UUID, Long> open = WINDOWS.get(world.getRegistryKey());
    if (open == null || open.isEmpty()) {
      return;
    }
    final Iterator<Map.Entry<UUID, Long>> remaining = open.entrySet().iterator();
    while (remaining.hasNext()) {
      final Map.Entry<UUID, Long> entry = remaining.next();
      if (world.getTime() < entry.getValue()) {
        continue;
      }
      close(world.getEntity(entry.getKey()));
      remaining.remove();
    }
  }

  private static void close(final Entity held) {
    if (held instanceof MobEntity mob) {
      mob.setCanPickUpLoot(false);
    }
  }

  private static Map<UUID, Long> windowsIn(final ServerWorld world) {
    return WINDOWS.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashMap<>());
  }
}
