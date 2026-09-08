package com.grahambartley.morearrows.gravity;

import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class GravityService {

  private GravityService() {}

  public static List<BlockPos> collapse(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return collapse(world, center, shooter, ServerConfigService.get().physics());
  }

  public static List<BlockPos> collapse(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final PhysicsArrowConfig physics) {
    if (world == null || center == null || physics == null) {
      return List.of();
    }

    final List<BlockPos> fallen = new ArrayList<>();
    for (final BlockPos pos : GravityShape.blocks(center, physics.gravityImpactRadius())) {
      if (FallingBlocks.canFall(world, pos, shooter)
          && !physics.isExcludedFromGravity(FallingBlocks.blockIdAt(world, pos))
          && FallingBlocks.drop(world, pos) != null) {
        fallen.add(pos);
      }
    }
    return List.copyOf(fallen);
  }
}
