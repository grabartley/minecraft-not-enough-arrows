package com.grahambartley.notenougharrows.structure;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;

public final class StructureRemoval {

  private StructureRemoval() {}

  public static int remove(
      final ServerWorld world, final TimedStructure structure, final Predicate<BlockPos> isLoaded) {
    int removed = 0;
    for (final BlockPos pos : structure.positions()) {
      if (isLoaded.test(pos) && clear(world, pos, structure.id())) {
        removed++;
      }
    }
    return removed;
  }

  public static boolean clear(final ServerWorld world, final BlockPos pos, final UUID id) {
    final WorldChunk chunk = world.getWorldChunk(pos);
    final Optional<StructureMark> mark = StructureChunkMarks.at(chunk, pos);
    if (mark.isEmpty() || !mark.get().belongsTo(id)) {
      return false;
    }
    StructureChunkMarks.unmark(chunk, pos);
    if (!StructureChunkMarks.stillHolds(mark.get(), world.getBlockState(pos))) {
      return false;
    }
    return world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
  }

  public static Predicate<BlockPos> loadedIn(final ServerWorld world) {
    return pos ->
        world
            .getChunkManager()
            .isChunkLoaded(
                ChunkSectionPos.getSectionCoord(pos.getX()),
                ChunkSectionPos.getSectionCoord(pos.getZ()));
  }
}
