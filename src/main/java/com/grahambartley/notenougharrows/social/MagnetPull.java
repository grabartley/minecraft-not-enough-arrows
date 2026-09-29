package com.grahambartley.notenougharrows.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class MagnetPull {
  private static final double AIM_ABOVE_FEET = 0.5;

  private static final Map<RegistryKey<World>, Map<UUID, Pull>> PULLS = new HashMap<>();

  private MagnetPull() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(MagnetPull::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> PULLS.clear());
  }

  public static boolean isPullable(final Entity entity) {
    return entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity;
  }

  public static List<Entity> pull(
      final ServerWorld world,
      final Vec3d impact,
      @Nullable final LivingEntity shooter,
      final int radius) {
    if (world == null || impact == null || shooter == null || radius <= 0) {
      return List.of();
    }
    final List<Entity> caught =
        world.getOtherEntities(
            null,
            Box.of(impact, radius * 2.0, radius * 2.0, radius * 2.0),
            entity ->
                entity.isAlive()
                    && isPullable(entity)
                    && MagnetSteering.isWithinReach(entity.getPos(), impact, radius));
    final Map<UUID, Pull> pulls =
        PULLS.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashMap<>());
    final long until = world.getTime() + MagnetSteering.MAX_PULL_TICKS;
    for (final Entity entity : caught) {
      pulls.put(entity.getUuid(), new Pull(shooter.getUuid(), until));
      steer(entity, shooter);
    }
    return List.copyOf(caught);
  }

  public static boolean isPulling(final ServerWorld world, final UUID id) {
    final Map<UUID, Pull> pulls = PULLS.get(world.getRegistryKey());
    return pulls != null && pulls.containsKey(id);
  }

  private static void tick(final ServerWorld world) {
    final Map<UUID, Pull> pulls = PULLS.get(world.getRegistryKey());
    if (pulls == null || pulls.isEmpty()) {
      return;
    }
    final long now = world.getTime();
    final List<UUID> finished = new ArrayList<>();
    for (final Map.Entry<UUID, Pull> entry : pulls.entrySet()) {
      final Entity pulled = world.getEntity(entry.getKey());
      final Entity shooter = world.getEntity(entry.getValue().shooter());
      if (now >= entry.getValue().until()
          || pulled == null
          || !pulled.isAlive()
          || !(shooter instanceof LivingEntity living)
          || !living.isAlive()) {
        finished.add(entry.getKey());
      } else if (MagnetSteering.hasArrived(pulled.getPos(), aimAt(living))) {
        settle(pulled);
        finished.add(entry.getKey());
      } else {
        steer(pulled, living);
      }
    }
    finished.forEach(pulls::remove);
  }

  private static void steer(final Entity pulled, final LivingEntity shooter) {
    pulled.setVelocity(MagnetSteering.velocityToward(pulled.getPos(), aimAt(shooter)));
    pulled.velocityModified = true;
  }

  private static void settle(final Entity pulled) {
    pulled.setVelocity(Vec3d.ZERO);
    pulled.velocityModified = true;
  }

  private static Vec3d aimAt(final LivingEntity shooter) {
    return shooter.getPos().add(0.0, AIM_ABOVE_FEET, 0.0);
  }

  private record Pull(UUID shooter, long until) {}
}
