package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.DrainArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class DrainService {

  private DrainService() {}

  public static List<BlockPos> drain(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return drain(world, center, shooter, ServerConfigService.get().terrain().drain());
  }

  public static List<BlockPos> drain(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final DrainArrowConfig drain) {
    if (drain == null || !drain.enabled()) {
      return List.of();
    }
    return SphereSweep.sweep(
        world, center, drain.radius(), drain.maxBlocks(), shooter, SpongeAbsorption::absorbAt);
  }
}
