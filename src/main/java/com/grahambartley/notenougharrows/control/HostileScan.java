package com.grahambartley.notenougharrows.control;

import java.util.List;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class HostileScan {

  private HostileScan() {}

  public static boolean isHostile(final MobEntity candidate) {
    return candidate instanceof Monster && candidate.isAlive();
  }

  public static boolean isEngaged(final MobEntity candidate) {
    return isHostile(candidate) && candidate.getTarget() != null;
  }

  public static List<MobEntity> hostilesAround(
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
            isHostile(candidate)
                && center.squaredDistanceTo(candidate.getBoundingBox().getCenter()) <= limit);
  }

  public static List<MobEntity> engagedHostilesAround(
      final ServerWorld world, final Vec3d center, final double radius) {
    return hostilesAround(world, center, radius).stream().filter(HostileScan::isEngaged).toList();
  }
}
