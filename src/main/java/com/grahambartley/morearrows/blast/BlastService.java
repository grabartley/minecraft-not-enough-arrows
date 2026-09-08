package com.grahambartley.morearrows.blast;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.entity.ExplosiveArrowEntity;
import com.grahambartley.morearrows.fire.FirePatchService;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class BlastService {

  private BlastService() {}

  public static void register() {
    FuseService.whenExpired(
        (world, host, fuse) -> {
          if (host instanceof ExplosiveArrowEntity arrow) {
            detonate(world, arrow);
          }
        });
  }

  public static void detonate(
      @Nullable final ServerWorld world, @Nullable final ExplosiveArrowEntity arrow) {
    if (world == null || arrow == null) {
      return;
    }

    final ExplosiveArrowConfig explosive = ServerConfigService.get().explosive();
    blast(world, arrow, arrow.tier().in(explosive).power(), explosive);
    if (arrow.tier().leavesFire()) {
      FirePatchService.ignite(world, BlockPos.ofFloored(arrow.getPos()), shooterOf(arrow));
    }
    arrow.discard();
  }

  private static void blast(
      final ServerWorld world,
      final ExplosiveArrowEntity arrow,
      final float power,
      final ExplosiveArrowConfig explosive) {
    if (power <= 0f) {
      return;
    }
    world.createExplosion(
        arrow,
        null,
        new BlastBehavior(explosive.damageTerrain(), explosive.damageEntities()),
        arrow.getX(),
        arrow.getY(),
        arrow.getZ(),
        power,
        false,
        explosive.damageTerrain() ? World.ExplosionSourceType.TNT : World.ExplosionSourceType.NONE);
  }

  @Nullable
  private static PlayerEntity shooterOf(final Entity arrow) {
    return arrow instanceof ExplosiveArrowEntity explosive
        ? explosive.shootingPlayer().orElse(null)
        : null;
  }
}
