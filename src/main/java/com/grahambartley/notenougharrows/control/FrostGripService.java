package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.config.FrostArrowConfig;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class FrostGripService {
  private static final Map<RegistryKey<World>, Map<UUID, Long>> GRIPS = new HashMap<>();

  private FrostGripService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(FrostGripService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static boolean grip(
      final ServerWorld world, final LivingEntity target, final FrostArrowConfig frost) {
    if (target == null
        || !target.isAlive()
        || !FrostGrip.holds(frost.durationTicks())
        || !target.canFreeze()) {
      return false;
    }
    gripsIn(world).put(target.getUuid(), world.getTime() + frost.durationTicks());
    pin(target);
    return true;
  }

  private static void forget() {
    GRIPS.clear();
  }

  private static void tick(final ServerWorld world) {
    final Map<UUID, Long> gripped = GRIPS.get(world.getRegistryKey());
    if (gripped == null || gripped.isEmpty()) {
      return;
    }
    final Iterator<Map.Entry<UUID, Long>> remaining = gripped.entrySet().iterator();
    while (remaining.hasNext()) {
      final Map.Entry<UUID, Long> entry = remaining.next();
      final Entity held = world.getEntity(entry.getKey());
      if (!(held instanceof LivingEntity living) || !living.isAlive()) {
        remaining.remove();
        continue;
      }
      if (world.getTime() >= entry.getValue()) {
        remaining.remove();
        continue;
      }
      pin(living);
    }
  }

  private static void pin(final LivingEntity target) {
    target.setFrozenTicks(FrostGrip.pinnedTicks(target.getMinFreezeDamageTicks()));
  }

  private static Map<UUID, Long> gripsIn(final ServerWorld world) {
    return GRIPS.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashMap<>());
  }
}
