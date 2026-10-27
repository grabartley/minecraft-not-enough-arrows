package com.grahambartley.volley.ender;

import com.grahambartley.volley.config.EnderArrowConfig;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.world.Reach;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
    if (struck == shooter || !RecallTargets.mayMove(struck, config.recallAffectsPlayers())) {
      return false;
    }
    if (!Reach.isWithin(struck.getPos(), shooter.getPos(), config.recallMaxRangeBlocks())) {
      return false;
    }

    return EnderLanding.forEntity(world, struck, shooter.getPos())
        .filter(landing -> EnderDestination.isInsideBorder(world.getWorldBorder(), landing))
        .map(landing -> EnderTeleport.move(world, struck, landing))
        .orElse(false);
  }
}
