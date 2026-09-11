package com.grahambartley.notenougharrows.wind;

import com.grahambartley.notenougharrows.config.UtilityArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class WindBurstService {
  private static final float WIND_CHARGE_EXPLOSION_POWER = 1.2f;

  private WindBurstService() {}

  public static List<Entity> burst(
      final ServerWorld world, final Vec3d center, @Nullable final Entity source) {
    final UtilityArrowConfig utility = ServerConfigService.get().utility();
    return burst(world, center, source, utility.windBurstRadius(), utility.windPushStrength());
  }

  public static List<Entity> burst(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      final float radius,
      final float strength) {
    if (world == null || center == null) {
      return List.of();
    }

    final Entity shooter = shooterOf(source);
    activateBlocks(world, center, source, shooter);
    return displaceEntities(world, center, source, shooter, radius, strength);
  }

  private static void activateBlocks(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      @Nullable final Entity shooter) {
    world.createExplosion(
        source,
        null,
        new WindExplosionBehavior(shooter),
        center.getX(),
        center.getY(),
        center.getZ(),
        WIND_CHARGE_EXPLOSION_POWER,
        false,
        World.ExplosionSourceType.TRIGGER,
        ParticleTypes.GUST_EMITTER_SMALL,
        ParticleTypes.GUST_EMITTER_LARGE,
        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST);
  }

  private static List<Entity> displaceEntities(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      @Nullable final Entity shooter,
      final float radius,
      final float strength) {
    if (radius <= 0.0f || strength <= 0.0f) {
      return List.of();
    }

    final List<Entity> displaced = new ArrayList<>();
    for (final Entity entity :
        world.getOtherEntities(shooter, Box.of(center, radius * 2, radius * 2, radius * 2))) {
      if (entity != source && push(entity, center, radius, strength)) {
        displaced.add(entity);
      }
    }
    return List.copyOf(displaced);
  }

  private static boolean push(
      final Entity entity, final Vec3d center, final float radius, final float strength) {
    final Vec3d push = WindBurst.push(center, entity.getPos(), radius, strength);
    if (push.lengthSquared() == 0.0) {
      return false;
    }
    entity.addVelocity(push);
    entity.velocityModified = true;
    return true;
  }

  @Nullable
  private static Entity shooterOf(@Nullable final Entity source) {
    return source instanceof ProjectileEntity projectile ? projectile.getOwner() : null;
  }
}
