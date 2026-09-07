package com.grahambartley.morearrows.wind;

import com.grahambartley.morearrows.config.UtilityArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
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

    activateBlocks(world, center, source);
    return displaceEntities(world, center, source, radius, strength);
  }

  private static void activateBlocks(
      final ServerWorld world, final Vec3d center, @Nullable final Entity source) {
    world.createExplosion(
        source,
        null,
        AbstractWindChargeEntity.EXPLOSION_BEHAVIOR,
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
      final float radius,
      final float strength) {
    if (radius <= 0.0f || strength <= 0.0f) {
      return List.of();
    }

    final Entity shooter = shooterOf(source);
    final List<Entity> displaced =
        world.getOtherEntities(shooter, Box.of(center, radius * 2, radius * 2, radius * 2)).stream()
            .filter(entity -> entity != source)
            .filter(entity -> push(entity, center, radius, strength))
            .toList();
    return List.copyOf(displaced);
  }

  private static boolean push(
      final Entity entity, final Vec3d center, final float radius, final float strength) {
    final Vec3d push = WindBurst.push(center, entity.getPos(), radius, strength);
    if (push.equals(Vec3d.ZERO)) {
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
