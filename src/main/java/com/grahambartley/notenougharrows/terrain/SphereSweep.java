package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.structure.StructureRemoval;
import com.grahambartley.notenougharrows.world.BlockEditPermission;
import com.grahambartley.notenougharrows.world.BlockSphere;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SphereSweep {

  @FunctionalInterface
  public interface PositionEffect {
    boolean applyAt(ServerWorld world, BlockPos pos);
  }

  private SphereSweep() {}

  public static List<BlockPos> sweep(
      final ServerWorld world,
      final BlockPos center,
      final int radius,
      final int maxBlocks,
      @Nullable final PlayerEntity shooter,
      final PositionEffect effect) {
    if (world == null || center == null || effect == null || maxBlocks <= 0) {
      return List.of();
    }
    final Predicate<BlockPos> isLoaded = StructureRemoval.loadedIn(world);
    final List<BlockPos> changed = new ArrayList<>();
    for (final BlockPos pos : BlockSphere.blocks(center, radius)) {
      if (changed.size() >= maxBlocks) {
        break;
      }
      if (isLoaded.test(pos)
          && world.isInBuildLimit(pos)
          && BlockEditPermission.allows(world, pos, shooter)
          && effect.applyAt(world, pos)) {
        changed.add(pos);
      }
    }
    return List.copyOf(changed);
  }
}
