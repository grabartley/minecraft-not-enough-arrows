package com.grahambartley.notenougharrows.control;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class DisarmFetchService {
  public static final int FETCH_WINDOW_TICKS = 600;

  private static final Map<RegistryKey<World>, Map<UUID, DisarmFetch>> WINDOWS = new HashMap<>();

  private DisarmFetchService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(DisarmFetchService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> closeEveryWindow());
  }

  public static void letThemFetchItBack(final ServerWorld world, final LivingEntity target) {
    if (!(target instanceof MobEntity mob) || mob.canPickUpLoot()) {
      return;
    }
    windowsIn(world)
        .computeIfAbsent(
            mob.getUuid(),
            id ->
                new DisarmFetch(
                    id,
                    mob.getDropChance(EquipmentSlot.MAINHAND),
                    world.getTime() + FETCH_WINDOW_TICKS));
    mob.setCanPickUpLoot(true);
  }

  public static boolean isFetching(final ServerWorld world, final LivingEntity target) {
    final Map<UUID, DisarmFetch> open = WINDOWS.get(world.getRegistryKey());
    return open != null && open.containsKey(target.getUuid());
  }

  public static void closeWindowNow(final ServerWorld world, final LivingEntity target) {
    final Map<UUID, DisarmFetch> open = WINDOWS.get(world.getRegistryKey());
    if (open == null) {
      return;
    }
    final DisarmFetch fetch = open.remove(target.getUuid());
    if (fetch != null) {
      close(target, fetch);
    }
  }

  private static void closeEveryWindow() {
    WINDOWS.clear();
  }

  private static void tick(final ServerWorld world) {
    final Map<UUID, DisarmFetch> open = WINDOWS.get(world.getRegistryKey());
    if (open == null || open.isEmpty()) {
      return;
    }
    final Iterator<Map.Entry<UUID, DisarmFetch>> remaining = open.entrySet().iterator();
    while (remaining.hasNext()) {
      final DisarmFetch fetch = remaining.next().getValue();
      final Entity held = world.getEntity(fetch.mobId());
      if (!fetch.hasExpired(world.getTime())) {
        keepTheirGearAsDroppableAsItWas(held, fetch);
        continue;
      }
      close(held, fetch);
      remaining.remove();
    }
  }

  private static void keepTheirGearAsDroppableAsItWas(final Entity held, final DisarmFetch fetch) {
    if (held instanceof MobEntity mob
        && mob.getDropChance(EquipmentSlot.MAINHAND) != fetch.mainHandDropChance()) {
      mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, fetch.mainHandDropChance());
    }
  }

  private static void close(final Entity held, final DisarmFetch fetch) {
    if (!(held instanceof MobEntity mob)) {
      return;
    }
    mob.setCanPickUpLoot(false);
    mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, fetch.mainHandDropChance());
  }

  private static Map<UUID, DisarmFetch> windowsIn(final ServerWorld world) {
    return WINDOWS.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashMap<>());
  }
}
