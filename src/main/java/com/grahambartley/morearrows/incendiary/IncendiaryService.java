package com.grahambartley.morearrows.incendiary;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.IncendiaryArrowConfig;
import com.grahambartley.morearrows.fire.FirePatchService;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
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
    final IncendiaryArrowConfig incendiary = explosive.incendiary();
    return ignite(
        world,
        center,
        source,
        shooter,
        incendiary.burnRadius(),
        incendiary.igniteSeconds(),
        incendiary.ignitesBlocks(),
        explosive.firePatchDurationTicks());
  }

  public static List<Entity> ignite(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      @Nullable final PlayerEntity shooter,
      final int radius,
      final int igniteSeconds,
      final boolean ignitesBlocks,
      final int firePatchDurationTicks) {
    if (world == null || center == null || radius <= 0) {
      return List.of();
    }

    if (ignitesBlocks) {
      FirePatchService.ignite(
          world, BlockPos.ofFloored(center), shooter, radius, firePatchDurationTicks);
    }
    return burnEntities(world, center, source, ownerOf(source), radius, igniteSeconds);
  }

  @Nullable
  private static Entity ownerOf(@Nullable final Entity source) {
    return source instanceof ProjectileEntity projectile ? projectile.getOwner() : null;
  }

  private static List<Entity> burnEntities(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final Entity source,
      @Nullable final Entity shooter,
      final int radius,
      final int igniteSeconds) {
    final int ticks = IncendiaryBurn.igniteTicks(igniteSeconds);
    if (ticks <= 0) {
      return List.of();
    }

    final List<Entity> burned = new ArrayList<>();
    for (final Entity entity :
        world.getOtherEntities(source, Box.of(center, radius * 2.0, radius * 2.0, radius * 2.0))) {
      if (entity != shooter
          && !(entity instanceof ItemEntity)
          && !entity.isFireImmune()
          && IncendiaryBurn.reaches(center, entity.getPos(), radius)) {
        entity.setOnFireForTicks(ticks);
        burned.add(entity);
      }
    }
    return List.copyOf(burned);
  }
}
