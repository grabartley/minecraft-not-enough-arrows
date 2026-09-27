package com.grahambartley.notenougharrows.structure;

import com.grahambartley.notenougharrows.world.BlockEditPermission;
import com.grahambartley.notenougharrows.world.BlockPlacement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class StructurePlacer {

  private StructurePlacer() {}

  public static List<StructureBlock> place(
      final ServerWorld world,
      final UUID id,
      final List<BlockPos> candidates,
      final StructureBlockSource source,
      @Nullable final PlayerEntity owner,
      final StructureBudget budget,
      final Predicate<BlockPos> isHeld) {
    final Predicate<BlockPos> isLoaded = StructureRemoval.loadedIn(world);
    final List<StructureBlock> placed = new ArrayList<>();
    final Set<BlockPos> taken = new HashSet<>();
    for (final BlockPos candidate : candidates) {
      if (!budget.hasRoomAfter(placed.size())) {
        break;
      }
      if (candidate == null) {
        continue;
      }
      if (!isLoaded.test(candidate)) {
        if (!BlockEditPermission.allows(world, candidate, owner)) {
          break;
        }
        continue;
      }
      final StructureBlock block = source.resolve(world, candidate);
      if (block == null) {
        continue;
      }
      if (!BlockEditPermission.allows(world, block.pos(), owner)) {
        break;
      }
      if (!isLoaded.test(block.pos())) {
        continue;
      }
      if (taken.contains(block.pos())
          || isHeld.test(block.pos())
          || !BlockPlacement.canOccupy(world, block.pos(), block.state())) {
        continue;
      }
      if (world.setBlockState(block.pos(), block.state(), Block.NOTIFY_ALL)) {
        StructureChunkMarks.mark(world.getWorldChunk(block.pos()), block, id);
        placed.add(block);
        taken.add(block.pos());
      }
    }
    return placed.stream().filter(block -> isStillMarked(world, block, id)).toList();
  }

  private static boolean isStillMarked(
      final ServerWorld world, final StructureBlock block, final UUID id) {
    return StructureChunkMarks.at(world.getWorldChunk(block.pos()), block.pos())
        .filter(mark -> mark.belongsTo(id))
        .isPresent();
  }
}
