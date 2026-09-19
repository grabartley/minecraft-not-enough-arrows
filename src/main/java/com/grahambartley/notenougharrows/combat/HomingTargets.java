package com.grahambartley.notenougharrows.combat;

import com.grahambartley.notenougharrows.config.HomingArrowConfig;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class HomingTargets {

  private HomingTargets() {}

  public static boolean isEligible(final Entity candidate) {
    return candidate instanceof Monster
        && !(candidate instanceof PlayerEntity)
        && candidate.isAlive();
  }

  public static Vec3d aimPointOf(final Entity target) {
    return target.getBoundingBox().getCenter();
  }

  public static Optional<Entity> ahead(
      final ServerWorld world,
      final Entity arrow,
      final Vec3d velocity,
      final HomingArrowConfig config) {
    if (!config.seeks()) {
      return Optional.empty();
    }

    final Vec3d origin = arrow.getPos();
    final Box search = new Box(origin, origin).expand(config.searchRadius());
    final List<Entity> eligible =
        world.getOtherEntities(
            arrow,
            search,
            candidate ->
                isEligible(candidate)
                    && HomingSteering.withinCone(
                        velocity,
                        aimPointOf(candidate).subtract(origin),
                        config.searchConeDegrees()));
    return NearestCandidate.nearest(
        origin, eligible, HomingTargets::aimPointOf, config.searchRadius());
  }
}
