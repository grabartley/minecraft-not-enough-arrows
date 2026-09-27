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
        || FrostImmunity.shrugsOff(target)) {
      return false;
    }
    final long until = world.getTime() + frost.durationTicks();
    gripsIn(world).put(target.getUuid(), until);
    target.setAttached(ControlPersistence.FROST_UNTIL, until);
    pin(target);
    FrostEffects.frozeOver(world, target);
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
        living.removeAttached(ControlPersistence.FROST_UNTIL);
        FrostEffects.thawed(world, living);
        continue;
      }
      pin(living);
      if (FrostImmunity.needsHelpToFeelIt(living) && FrostGrip.bitesOn(living.age)) {
        living.damage(world.getDamageSources().freeze(), FrostImmunity.freezeDamage(living));
      }
      if (FrostGrip.shimmersOn(world.getTime())) {
        FrostEffects.stillFrozen(world, living);
      }
    }
  }

  static void resume(final ServerWorld world, final LivingEntity target, final long until) {
    gripsIn(world).put(target.getUuid(), until);
  }

  private static void pin(final LivingEntity target) {
    target.setFrozenTicks(FrostGrip.pinnedTicks(target.getMinFreezeDamageTicks()));
  }

  private static Map<UUID, Long> gripsIn(final ServerWorld world) {
    return GRIPS.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashMap<>());
  }
}
