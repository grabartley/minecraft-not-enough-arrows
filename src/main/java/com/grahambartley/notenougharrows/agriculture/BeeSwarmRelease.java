package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.config.BeeArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
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
      final BeeEntity spawned = spawn(world, at, swarm, target, bee.lifetimeTicks());
      if (spawned != null) {
        released.add(spawned);
      }
    }
    return List.copyOf(released);
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
    bee.setAttached(BeeSwarmWarden.SWARM, swarm);
    if (target != null) {
      BeeSwarmWarden.angerAt(bee, target, lifetimeTicks);
    }
    if (!world.spawnEntity(bee)) {
      return null;
    }
    BeeSwarmWarden.watch(world, bee);
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

  private static double scatter(final ServerWorld world) {
    return (world.random.nextDouble() - 0.5) * SCATTER;
  }
}
