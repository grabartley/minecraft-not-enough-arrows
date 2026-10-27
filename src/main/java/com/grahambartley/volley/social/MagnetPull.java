package com.grahambartley.volley.social;

import com.grahambartley.volley.world.ExpiringLedger;
import com.grahambartley.volley.world.PerWorld;
import com.grahambartley.volley.world.Propel;
import com.grahambartley.volley.world.Reach;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class MagnetPull {
  private static final double AIM_ABOVE_FEET = 0.5;

  private static final PerWorld<ExpiringLedger<UUID>> PULLS = new PerWorld<>(ExpiringLedger::new);

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
    if (shooter == null || radius <= 0) {
      return List.of();
    }
    final List<Entity> caught =
        world.getOtherEntities(
            null,
            Box.of(impact, radius * 2.0, radius * 2.0, radius * 2.0),
            entity ->
                entity.isAlive()
                    && isPullable(entity)
                    && Reach.isWithin(entity.getPos(), impact, radius));
    final ExpiringLedger<UUID> pulls = PULLS.of(world);
    final long until = world.getTime() + MagnetSteering.MAX_PULL_TICKS;
    for (final Entity entity : caught) {
      pulls.put(entity.getUuid(), shooter.getUuid(), until);
      steer(entity, shooter);
    }
    return List.copyOf(caught);
  }

  public static boolean isPulling(final ServerWorld world, final UUID id) {
    final ExpiringLedger<UUID> pulls = PULLS.in(world);
    return pulls != null && pulls.contains(id);
  }

  private static void tick(final ServerWorld world) {
    final ExpiringLedger<UUID> pulls = PULLS.in(world);
    if (pulls == null || pulls.isEmpty()) {
      return;
    }
    pulls.removeExpired(world.getTime());
    for (final UUID id : pulls.ids()) {
      final Entity pulled = world.getEntity(id);
      final Entity shooter = pulls.get(id).map(world::getEntity).orElse(null);
      if (pulled == null
          || !pulled.isAlive()
          || !(shooter instanceof LivingEntity living)
          || !living.isAlive()) {
        pulls.remove(id);
      } else if (MagnetSteering.hasArrived(pulled.getPos(), aimAt(living))) {
        Propel.drive(pulled, Vec3d.ZERO);
        pulls.remove(id);
      } else {
        steer(pulled, living);
      }
    }
  }

  private static void steer(final Entity pulled, final LivingEntity shooter) {
    Propel.drive(pulled, MagnetSteering.velocityToward(pulled.getPos(), aimAt(shooter)));
  }

  private static Vec3d aimAt(final LivingEntity shooter) {
    return shooter.getPos().add(0.0, AIM_ABOVE_FEET, 0.0);
  }
}
