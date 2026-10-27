package com.grahambartley.volley.agriculture;

import com.grahambartley.volley.config.BeeArrowConfig;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.world.MobSpawn;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class BeeSwarmRelease {
  private static final double SCATTER = 0.5;

  private BeeSwarmRelease() {}

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
      spawn(world, at, shooter, swarm, target, bee.lifetimeTicks()).ifPresent(released::add);
    }
    return List.copyOf(released);
  }

  private static Optional<BeeEntity> spawn(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final LivingEntity shooter,
      final BeeSwarm swarm,
      @Nullable final LivingEntity target,
      final int lifetimeTicks) {
    final Optional<BeeEntity> spawned =
        MobSpawn.at(
            world,
            EntityType.BEE,
            at.add(scatter(world), 0.0, scatter(world)),
            shooter instanceof PlayerEntity player ? player : null,
            bee -> {
              bee.setCannotEnterHiveTicks(lifetimeTicks);
              bee.setBreedingAge(lifetimeTicks);
              bee.setAttached(BeeSwarmWarden.SWARM, swarm);
              if (target != null) {
                BeeSwarmWarden.angerAt(bee, target, lifetimeTicks);
              }
            });
    spawned.ifPresent(bee -> BeeSwarmWarden.watch(world, bee));
    return spawned;
  }

  @Nullable
  private static LivingEntity targetOf(
      @Nullable final Entity struck, @Nullable final LivingEntity shooter) {
    if (!(struck instanceof LivingEntity living) || !living.isAlive() || living == shooter) {
      return null;
    }
    return living;
  }

  private static double scatter(final ServerWorld world) {
    return (world.random.nextDouble() - 0.5) * SCATTER;
  }
}
