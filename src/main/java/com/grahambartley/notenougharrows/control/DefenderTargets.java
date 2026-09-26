package com.grahambartley.notenougharrows.control;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public final class DefenderTargets {

  private DefenderTargets() {}

  public static boolean threatens(final MobEntity candidate, final LivingEntity protectedEntity) {
    return candidate != null
        && candidate.isAlive()
        && candidate.getTarget() == protectedEntity
        && candidate != protectedEntity;
  }

  public static Optional<MobEntity> threatTo(
      final ServerWorld world,
      final LivingEntity protectedEntity,
      final MobEntity defender,
      final double radius) {
    if (protectedEntity == null || radius <= 0.0) {
      return Optional.empty();
    }
    final Box search = protectedEntity.getBoundingBox().expand(radius);
    final List<MobEntity> threats =
        world.getEntitiesByClass(
            MobEntity.class,
            search,
            candidate -> candidate != defender && threatens(candidate, protectedEntity));
    return threats.stream()
        .min(
            (left, right) ->
                Double.compare(
                    defender.squaredDistanceTo(left), defender.squaredDistanceTo(right)));
  }
}
