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
    if (center == null) {
      return List.of();
    }
    return sweep(world, BlockSphere.blocks(center, radius), maxBlocks, shooter, effect);
  }

  public static List<BlockPos> sweep(
      final ServerWorld world,
      final List<BlockPos> positions,
      final int maxBlocks,
      @Nullable final PlayerEntity shooter,
      final PositionEffect effect) {
    if (world == null || positions == null || effect == null || maxBlocks <= 0) {
      return List.of();
    }
    final Predicate<BlockPos> editable = editableBy(world, shooter);
    final List<BlockPos> changed = new ArrayList<>();
    for (final BlockPos pos : positions) {
      if (changed.size() >= maxBlocks) {
        break;
      }
      if (editable.test(pos) && effect.applyAt(world, pos)) {
        changed.add(pos);
      }
    }
    return List.copyOf(changed);
  }

  public static Predicate<BlockPos> editableBy(
      final ServerWorld world, @Nullable final PlayerEntity shooter) {
    final Predicate<BlockPos> isLoaded = StructureRemoval.loadedIn(world);
    return pos ->
        isLoaded.test(pos)
            && world.isInBuildLimit(pos)
            && BlockEditPermission.allows(world, pos, shooter);
  }
}
