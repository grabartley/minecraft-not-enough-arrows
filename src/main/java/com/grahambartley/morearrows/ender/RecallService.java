package com.grahambartley.morearrows.ender;

import com.grahambartley.morearrows.config.EnderArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class RecallService {

  private RecallService() {}

  public static boolean recall(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      @Nullable final Entity struck) {
    return recall(world, shooter, struck, ServerConfigService.get().ender());
  }

  public static boolean recall(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      @Nullable final Entity struck,
      @Nullable final EnderArrowConfig config) {
    if (world == null || shooter == null || config == null) {
      return false;
    }
    if (!(struck instanceof LivingEntity living) || living == shooter) {
      return false;
    }
    if (living instanceof PlayerEntity && !config.recallAffectsPlayers()) {
      return false;
    }
    if (!EnderDestination.isReachable(
        world.getWorldBorder(), living.getPos(), shooter.getPos(), config.recallMaxRangeBlocks())) {
      return false;
    }

    final Optional<Vec3d> landing = EnderLanding.forEntity(world, living, shooter.getPos());
    return landing.isPresent() && EnderTeleport.move(world, living, landing.get());
  }
}
