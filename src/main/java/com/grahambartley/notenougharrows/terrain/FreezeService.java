package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.FreezeArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class FreezeService {

  private FreezeService() {}

  public static List<BlockPos> freeze(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return freeze(world, center, shooter, ServerConfigService.get().terrain().freeze());
  }

  public static List<BlockPos> freeze(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final FreezeArrowConfig freeze) {
    if (freeze == null || !freeze.enabled()) {
      return List.of();
    }
    return SphereSweep.sweep(
        world, center, freeze.radius(), freeze.maxBlocks(), shooter, FreezeConversion::freezeAt);
  }
}
