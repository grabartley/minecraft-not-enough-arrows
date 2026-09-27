package com.grahambartley.notenougharrows.structure;

import com.grahambartley.notenougharrows.world.BlockEditPermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class StructurePlacer {

  private StructurePlacer() {}

  public static List<StructureBlock> place(
      final ServerWorld world,
      final List<BlockPos> candidates,
      final StructureBlockSource source,
      @Nullable final PlayerEntity owner,
      final StructureBudget budget,
      final Predicate<BlockPos> isHeld) {
    final List<StructureBlock> placed = new ArrayList<>();
    final Set<BlockPos> taken = new HashSet<>();
    for (final BlockPos candidate : candidates) {
      if (!budget.hasRoomAfter(placed.size())) {
        break;
      }
      final StructureBlock block = candidate == null ? null : source.resolve(world, candidate);
      if (block == null) {
        continue;
      }
      if (!BlockEditPermission.allows(world, block.pos(), owner)) {
        break;
      }
      if (taken.contains(block.pos()) || isHeld.test(block.pos()) || !canOccupy(world, block)) {
        continue;
      }
      if (world.setBlockState(block.pos(), block.state(), Block.NOTIFY_ALL)) {
        placed.add(block);
        taken.add(block.pos());
      }
    }
    return List.copyOf(placed);
  }

  public static boolean canOccupy(final ServerWorld world, final StructureBlock block) {
    if (!world.isInBuildLimit(block.pos())) {
      return false;
    }
    final BlockState current = world.getBlockState(block.pos());
    return (current.isAir() || current.isReplaceable())
        && block.state().canPlaceAt(world, block.pos());
  }
}
