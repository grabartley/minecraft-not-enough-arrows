package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.cloud.TimedCloud;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public final class StinkRepel {
  public static final double APPROACH_MARGIN = 4.0;
  public static final double PUSH = 0.12;

  private StinkRepel() {}

  public static void repel(final ServerWorld world, final TimedCloud cloud) {
    for (final MobEntity mob :
        world.getEntitiesByClass(
            MobEntity.class, cloud.bounds().expand(APPROACH_MARGIN), MobEntity::isAlive)) {
      if (cloud.contains(mob.getBoundingBox().getCenter())) {
        mob.getNavigation().stop();
        pushOut(mob, cloud);
      } else if (headsInto(mob.getNavigation().getCurrentPath(), cloud)) {
        mob.getNavigation().stop();
      }
    }
  }

  public static boolean headsInto(final Path path, final TimedCloud cloud) {
    if (path == null || cloud == null) {
      return false;
    }
    for (int node = path.getCurrentNodeIndex(); node < path.getLength(); node++) {
      if (cloud.contains(Vec3d.ofCenter(path.getNodePos(node)))) {
        return true;
      }
    }
    return false;
  }

  private static void pushOut(final MobEntity mob, final TimedCloud cloud) {
    final Vec3d away = awayFrom(cloud.center(), mob.getPos(), mob.getRandom().nextDouble());
    mob.addVelocity(away.x * PUSH, 0.0, away.z * PUSH);
    mob.velocityModified = true;
  }

  static Vec3d awayFrom(final Vec3d center, final Vec3d position, final double tieBreak) {
    final Vec3d flat = new Vec3d(position.x - center.x, 0.0, position.z - center.z);
    if (flat.lengthSquared() < 1.0E-6) {
      final double angle = tieBreak * Math.PI * 2.0;
      return new Vec3d(Math.cos(angle), 0.0, Math.sin(angle));
    }
    return flat.normalize();
  }
}
