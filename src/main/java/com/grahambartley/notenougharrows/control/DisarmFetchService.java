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
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class DisarmFetchService {
  public static final int FETCH_WINDOW_TICKS = 600;
  public static final int REPATH_INTERVAL_TICKS = 5;
  public static final double FETCH_SPEED = 1.2;

  private static final Map<RegistryKey<World>, Map<UUID, DisarmFetch>> ERRANDS = new HashMap<>();

  private DisarmFetchService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(DisarmFetchService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> ERRANDS.clear());
  }

  public static void sendToFetch(
      final ServerWorld world, final LivingEntity target, final ItemEntity thrown) {
    if (!(target instanceof MobEntity mob)) {
      return;
    }
    errandsIn(world)
        .put(
            mob.getUuid(),
            new DisarmFetch(
                mob.getUuid(),
                thrown.getUuid(),
                mob.getDropChance(EquipmentSlot.MAINHAND),
                world.getTime() + FETCH_WINDOW_TICKS));
  }

  public static boolean isFetching(final ServerWorld world, final LivingEntity target) {
    final Map<UUID, DisarmFetch> open = ERRANDS.get(world.getRegistryKey());
    return open != null && open.containsKey(target.getUuid());
  }

  public static void endErrandNow(final ServerWorld world, final LivingEntity target) {
    final Map<UUID, DisarmFetch> open = ERRANDS.get(world.getRegistryKey());
    final DisarmFetch fetch = open == null ? null : open.remove(target.getUuid());
    if (fetch != null) {
      keepTheirGearAsDroppableAsItWas(target, fetch);
    }
  }

  private static void tick(final ServerWorld world) {
    final Map<UUID, DisarmFetch> open = ERRANDS.get(world.getRegistryKey());
    if (open == null || open.isEmpty()) {
      return;
    }
    final Iterator<Map.Entry<UUID, DisarmFetch>> remaining = open.entrySet().iterator();
    while (remaining.hasNext()) {
      final DisarmFetch fetch = remaining.next().getValue();
      if (!(world.getEntity(fetch.mobId()) instanceof MobEntity mob) || !mob.isAlive()) {
        remaining.remove();
        continue;
      }
      keepTheirGearAsDroppableAsItWas(mob, fetch);
      if (fetch.hasExpired(world.getTime()) || runErrand(world, mob, fetch)) {
        remaining.remove();
      }
    }
  }

  private static boolean runErrand(
      final ServerWorld world, final MobEntity mob, final DisarmFetch fetch) {
    if (!mob.getMainHandStack().isEmpty()) {
      return true;
    }
    final Entity found = world.getEntity(fetch.itemId());
    if (!(found instanceof ItemEntity thrown) || !thrown.isAlive()) {
      return true;
    }
    MobAggression.aim(mob, null);
    if (DisarmFetch.canGrab(mob.squaredDistanceTo(thrown), thrown.getItemAge())) {
      mob.equipStack(EquipmentSlot.MAINHAND, thrown.getStack().copy());
      mob.sendPickup(thrown, thrown.getStack().getCount());
      thrown.discard();
      mob.getNavigation().stop();
      return true;
    }
    if (world.getTime() % REPATH_INTERVAL_TICKS == 0 || mob.getNavigation().isIdle()) {
      mob.getNavigation().startMovingTo(thrown, FETCH_SPEED);
    }
    return false;
  }

  private static void keepTheirGearAsDroppableAsItWas(
      final LivingEntity held, final DisarmFetch fetch) {
    if (held instanceof MobEntity mob
        && mob.getDropChance(EquipmentSlot.MAINHAND) != fetch.mainHandDropChance()) {
      mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, fetch.mainHandDropChance());
    }
  }

  private static Map<UUID, DisarmFetch> errandsIn(final ServerWorld world) {
    return ERRANDS.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashMap<>());
  }
}
