package com.grahambartley.morearrows.ender;

import com.grahambartley.morearrows.config.EnderArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class PearlArrivalService {
  private static final double SAME_PLACE_DISTANCE = 1.0e-3;

  private PearlArrivalService() {}

  public static boolean arrive(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      @Nullable final Vec3d destination) {
    return arrive(world, shooter, destination, ServerConfigService.get().ender());
  }

  public static boolean arrive(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      @Nullable final Vec3d destination,
      @Nullable final EnderArrowConfig config) {
    if (world == null || shooter == null || destination == null || config == null) {
      return false;
    }
    if (shooter.getPos().distanceTo(destination) < SAME_PLACE_DISTANCE) {
      return false;
    }
    if (!EnderDestination.isReachable(
        world.getWorldBorder(), shooter.getPos(), destination, config.pearlMaxRangeBlocks())) {
      return false;
    }
    if (!EnderTeleport.move(world, shooter, destination)) {
      return false;
    }

    if (config.pearlArrivalDamage() > 0.0f) {
      shooter.damage(world.getDamageSources().fall(), config.pearlArrivalDamage());
    }
    return true;
  }
}
