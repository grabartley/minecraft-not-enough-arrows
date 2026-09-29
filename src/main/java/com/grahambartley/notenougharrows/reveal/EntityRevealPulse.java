package com.grahambartley.notenougharrows.reveal;

import com.grahambartley.notenougharrows.glow.GlowService;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class EntityRevealPulse {

  private EntityRevealPulse() {}

  public static List<LivingEntity> fire(
      final ServerWorld world,
      final Vec3d center,
      final int radius,
      final int durationTicks,
      @Nullable final Entity source,
      @Nullable final Entity shooter) {
    if (world == null || center == null || radius < 0 || durationTicks <= 0) {
      return List.of();
    }
    final double reachSquared = (double) radius * radius;
    final List<LivingEntity> found =
        world.getEntitiesByClass(
            LivingEntity.class,
            Box.of(center, radius * 2.0, radius * 2.0, radius * 2.0),
            living ->
                living.isAlive()
                    && !living.isSpectator()
                    && living != shooter
                    && living.getPos().squaredDistanceTo(center) <= reachSquared);
    found.forEach(living -> GlowService.mark(world, living, source, durationTicks));
    return List.copyOf(found);
  }
}
