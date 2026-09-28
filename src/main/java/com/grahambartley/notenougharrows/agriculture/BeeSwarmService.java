package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.config.BeeArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class BeeSwarmService {
  public static final AttachmentType<BeeSwarm> SWARM =
      AttachmentRegistry.createPersistent(
          Identifier.of(NotEnoughArrows.MOD_ID, "bee_swarm"), BeeSwarm.CODEC);

  private static final Map<RegistryKey<World>, Set<UUID>> RELEASED = new HashMap<>();
  private static final double SCATTER = 0.5;

  private BeeSwarmService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(BeeSwarmService::tick);
    ServerEntityEvents.ENTITY_LOAD.register(BeeSwarmService::restore);
    ServerLivingEntityEvents.ALLOW_DAMAGE.register(BeeSwarmService::allowDamage);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> RELEASED.clear());
  }

  public static List<BeeEntity> release(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final LivingEntity shooter,
      @Nullable final Entity struck) {
    return release(world, at, shooter, struck, ServerConfigService.get().agriculture().bee());
  }

  public static List<BeeEntity> release(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final LivingEntity shooter,
      @Nullable final Entity struck,
      final BeeArrowConfig bee) {
    if (world == null || at == null || bee == null) {
      return List.of();
    }
    final LivingEntity target = targetOf(struck, shooter);
    final BeeSwarm swarm =
        new BeeSwarm(
            world.getTime() + bee.lifetimeTicks(),
            Optional.ofNullable(shooter).map(Entity::getUuid),
            Optional.ofNullable(target).map(Entity::getUuid));
    final List<BeeEntity> released = new ArrayList<>();
    for (int i = 0; i < bee.count(); i++) {
      final BeeEntity spawned = spawn(world, at, swarm, target, bee.lifetimeTicks());
      if (spawned != null) {
        released.add(spawned);
      }
    }
    return List.copyOf(released);
  }

  public static boolean isTracked(final ServerWorld world, final UUID beeId) {
    return releasedIn(world).contains(beeId);
  }

  @Nullable
  private static BeeEntity spawn(
      final ServerWorld world,
      final Vec3d at,
      final BeeSwarm swarm,
      @Nullable final LivingEntity target,
      final int lifetimeTicks) {
    final BeeEntity bee = EntityType.BEE.create(world);
    if (bee == null) {
      return null;
    }
    bee.refreshPositionAndAngles(
        at.x + scatter(world), at.y, at.z + scatter(world), world.random.nextFloat() * 360f, 0f);
    bee.setCannotEnterHiveTicks(lifetimeTicks);
    bee.setBreedingAge(lifetimeTicks);
    bee.setAttached(SWARM, swarm);
    if (target != null) {
      angerAt(bee, target, lifetimeTicks);
    }
    if (!world.spawnEntity(bee)) {
      return null;
    }
    releasedIn(world).add(bee.getUuid());
    return bee;
  }

  @Nullable
  private static LivingEntity targetOf(
      @Nullable final Entity struck, @Nullable final LivingEntity shooter) {
    if (!(struck instanceof LivingEntity living) || !living.isAlive() || living == shooter) {
      return null;
    }
    return living;
  }

  private static void angerAt(final BeeEntity bee, final LivingEntity target, final int ticks) {
    bee.setTarget(target);
    bee.setAngryAt(target.getUuid());
    bee.setAngerTime(ticks);
  }

  private static double scatter(final ServerWorld world) {
    return (world.random.nextDouble() - 0.5) * SCATTER;
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
    releasedIn(world).add(bee.getUuid());
  }

  private static void tick(final ServerWorld world) {
    final Set<UUID> released = RELEASED.get(world.getRegistryKey());
    if (released == null || released.isEmpty()) {
      return;
    }
    final Iterator<UUID> remaining = released.iterator();
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

  private static Set<UUID> releasedIn(final ServerWorld world) {
    return RELEASED.computeIfAbsent(world.getRegistryKey(), key -> new LinkedHashSet<>());
  }
}
