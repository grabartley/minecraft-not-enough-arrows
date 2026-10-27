package com.grahambartley.volley.agriculture;

import com.grahambartley.volley.Volley;
import com.grahambartley.volley.world.PerWorld;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class BeeSwarmWarden {
  public static final AttachmentType<BeeSwarm> SWARM =
      AttachmentRegistry.createPersistent(
          Identifier.of(Volley.MOD_ID, "bee_swarm"), BeeSwarm.CODEC);

  private static final PerWorld<Set<UUID>> WATCHED = new PerWorld<>(() -> new LinkedHashSet<>());

  private BeeSwarmWarden() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(BeeSwarmWarden::tick);
    ServerEntityEvents.ENTITY_LOAD.register(BeeSwarmWarden::restore);
    ServerLivingEntityEvents.ALLOW_DAMAGE.register(BeeSwarmWarden::allowDamage);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> WATCHED.clear());
  }

  public static void watch(final ServerWorld world, final BeeEntity bee) {
    WATCHED.of(world).add(bee.getUuid());
  }

  public static boolean isWatched(final ServerWorld world, final UUID beeId) {
    return WATCHED.of(world).contains(beeId);
  }

  static void angerAt(final BeeEntity bee, final LivingEntity target, final int ticks) {
    bee.setTarget(target);
    bee.setAngryAt(target.getUuid());
    bee.setAngerTime(ticks);
  }

  private static void restore(final Entity entity, final ServerWorld world) {
    if (!(entity instanceof BeeEntity bee)) {
      return;
    }
    final BeeSwarm swarm = bee.getAttached(SWARM);
    if (swarm == null) {
      return;
    }
    if (swarm.hasExpired(world.getTime())) {
      bee.discard();
      return;
    }
    watch(world, bee);
  }

  private static void tick(final ServerWorld world) {
    final Set<UUID> watched = WATCHED.in(world);
    if (watched == null || watched.isEmpty()) {
      return;
    }
    final Iterator<UUID> remaining = watched.iterator();
    while (remaining.hasNext()) {
      if (!(world.getEntity(remaining.next()) instanceof BeeEntity bee) || !bee.isAlive()) {
        remaining.remove();
        continue;
      }
      final BeeSwarm swarm = bee.getAttached(SWARM);
      if (swarm == null || swarm.hasExpired(world.getTime())) {
        remaining.remove();
        bee.discard();
        continue;
      }
      keepOffShooter(world, bee, swarm);
    }
  }

  private static void keepOffShooter(
      final ServerWorld world, final BeeEntity bee, final BeeSwarm swarm) {
    final LivingEntity target = bee.getTarget();
    final boolean chasingShooter = target != null && swarm.spares(target.getUuid());
    if (!chasingShooter && !swarm.spares(bee.getAngryAt())) {
      return;
    }
    bee.setTarget(null);
    bee.stopAnger();
    swarm
        .targetId()
        .map(world::getEntity)
        .filter(LivingEntity.class::isInstance)
        .map(LivingEntity.class::cast)
        .filter(LivingEntity::isAlive)
        .ifPresent(
            original -> angerAt(bee, original, (int) (swarm.expiryTick() - world.getTime())));
  }

  private static boolean allowDamage(
      final LivingEntity victim, final DamageSource source, final float amount) {
    if (!(source.getAttacker() instanceof BeeEntity bee)) {
      return true;
    }
    final BeeSwarm swarm = bee.getAttached(SWARM);
    return swarm == null || !swarm.spares(victim.getUuid());
  }
}
