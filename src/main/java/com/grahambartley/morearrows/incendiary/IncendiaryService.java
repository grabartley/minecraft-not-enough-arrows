package com.grahambartley.morearrows.incendiary;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.fire.FirePatchService;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class IncendiaryService {

  private IncendiaryService() {}

  public static List<Entity> ignite(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      @Nullable final PlayerEntity shooter) {
    final ExplosiveArrowConfig explosive = ServerConfigService.get().explosive();
    return ignite(
        world,
        center,
        source,
        shooter,
        explosive.incendiaryBurnRadius(),
        explosive.incendiaryIgniteSeconds(),
        explosive.incendiaryIgnitesBlocks());
  }

  public static List<Entity> ignite(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      @Nullable final PlayerEntity shooter,
      final int radius,
      final int igniteSeconds,
      final boolean ignitesBlocks) {
    if (world == null || center == null || radius <= 0) {
      return List.of();
    }

    if (ignitesBlocks) {
      FirePatchService.ignite(world, BlockPos.ofFloored(center), shooter, radius, firePatchTicks());
    }
    return burnEntities(world, center, source, radius, igniteSeconds);
  }

  private static List<Entity> burnEntities(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      final int radius,
      final int igniteSeconds) {
    final int ticks = IncendiaryBurn.igniteTicks(igniteSeconds);
    if (ticks <= 0) {
      return List.of();
    }

    final List<Entity> burned = new ArrayList<>();
    for (final Entity entity :
        world.getOtherEntities(source, Box.of(center, radius * 2.0, radius * 2.0, radius * 2.0))) {
      if (!entity.isFireImmune() && IncendiaryBurn.reaches(center, entity.getPos(), radius)) {
        entity.setOnFireForTicks(ticks);
        burned.add(entity);
      }
    }
    return List.copyOf(burned);
  }

  private static int firePatchTicks() {
    return ServerConfigService.get().explosive().firePatchDurationTicks();
  }
}
