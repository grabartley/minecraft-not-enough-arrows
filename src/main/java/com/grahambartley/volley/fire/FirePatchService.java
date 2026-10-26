package com.grahambartley.volley.fire;

import com.grahambartley.volley.config.ExplosiveArrowConfig;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.structure.StructureBudget;
import com.grahambartley.volley.structure.TimedStructure;
import com.grahambartley.volley.structure.TimedStructureService;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class FirePatchService {

  private FirePatchService() {}

  public static List<BlockPos> ignite(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity igniter) {
    final ExplosiveArrowConfig explosive = ServerConfigService.get().explosive();
    return ignite(
        world, center, igniter, explosive.firePatchRadius(), explosive.firePatchDurationTicks());
  }

  public static List<BlockPos> ignite(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity igniter,
      final int radius,
      final int durationTicks) {
    if (world == null || center == null) {
      return List.of();
    }

    final List<BlockPos> columns = FirePatchShape.columns(center, radius);
    return TimedStructureService.build(
            world,
            igniter,
            columns,
            (target, column) -> FirePatchPlacer.surfaceIn(target, column, igniter),
            StructureBudget.of(columns.size()),
            durationTicks)
        .map(TimedStructure::positions)
        .orElse(List.of());
  }

  public static void forget() {
    TimedStructureService.forget();
  }
}
