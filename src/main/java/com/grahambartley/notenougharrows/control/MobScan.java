package com.grahambartley.notenougharrows.control;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class MobScan {

  public static final double MAX_HUNTING_REACH = 64.0;

  private MobScan() {}

  public static boolean isEngaged(final MobEntity candidate) {
    return candidate.isAlive() && candidate.getTarget() != null;
  }

  public static List<MobEntity> mobsAround(
      final ServerWorld world, final Vec3d center, final double radius) {
    if (world == null || center == null || radius <= 0.0) {
      return List.of();
    }
    final Box search = new Box(center, center).expand(radius);
    final double limit = radius * radius;
    return world.getEntitiesByClass(
        MobEntity.class,
        search,
        candidate ->
            candidate.isAlive() && candidate.getBoundingBox().squaredMagnitude(center) <= limit);
  }

  public static List<MobEntity> mobsHunting(
      final ServerWorld world, final LivingEntity prey, final double radius) {
    if (prey == null) {
      return List.of();
    }
    final Vec3d at = prey.getBoundingBox().getCenter();
    return mobsAround(world, at, Math.max(radius, MAX_HUNTING_REACH)).stream()
        .filter(candidate -> candidate.getTarget() == prey)
        .filter(
            candidate ->
                candidate.getBoundingBox().squaredMagnitude(at)
                    <= huntingReachSquared(candidate, radius))
        .toList();
  }

  private static double huntingReachSquared(final MobEntity hunter, final double radius) {
    final double reach =
        Math.max(radius, hunter.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE));
    return reach * reach;
  }

  public static List<MobEntity> engagedMobsAround(
      final ServerWorld world, final Vec3d center, final double radius) {
    return mobsAround(world, center, radius).stream().filter(MobScan::isEngaged).toList();
  }
}
