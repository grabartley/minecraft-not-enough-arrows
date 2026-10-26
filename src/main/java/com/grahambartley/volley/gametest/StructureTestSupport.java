package com.grahambartley.volley.gametest;

import com.grahambartley.volley.structure.StructureBlockSource;
import com.grahambartley.volley.structure.StructureBudget;
import com.grahambartley.volley.structure.TimedStructure;
import com.grahambartley.volley.structure.TimedStructureService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.block.Blocks;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

final class StructureTestSupport {
  static final String TEMPLATE = "volley:fire_pad";
  static final BlockPos FIRST = new BlockPos(1, 3, 3);
  static final BlockPos SECOND = new BlockPos(2, 3, 3);
  static final BlockPos THIRD = new BlockPos(3, 3, 3);
  static final BlockPos FOURTH = new BlockPos(4, 3, 3);
  static final List<BlockPos> LINE = List.of(FIRST, SECOND, THIRD, FOURTH);
  static final StructureBlockSource PLANKS =
      StructureBlockSource.of(Blocks.OAK_PLANKS.getDefaultState());
  static final int LONG_LIFETIME_TICKS = 400;
  static final int SHORT_LIFETIME_TICKS = 10;

  private StructureTestSupport() {}

  static List<BlockPos> absolute(final TestContext context, final List<BlockPos> relative) {
    return relative.stream().map(context::getAbsolutePos).toList();
  }

  static List<BlockPos> absolute(final TestContext context, final BlockPos... relative) {
    return absolute(context, Arrays.asList(relative));
  }

  static Optional<TimedStructure> buildLine(final TestContext context, final int lifetimeTicks) {
    return TimedStructureService.build(
        context.getWorld(),
        null,
        absolute(context, LINE),
        PLANKS,
        StructureBudget.of(LINE.size()),
        lifetimeTicks);
  }

  static void reloadChunksOf(final TestContext context, final List<BlockPos> relative) {
    relative.stream()
        .map(pos -> chunkAt(context, pos))
        .distinct()
        .forEach(
            chunk -> ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad(context.getWorld(), chunk));
  }

  static WorldChunk chunkAt(final TestContext context, final BlockPos relative) {
    return context.getWorld().getWorldChunk(context.getAbsolutePos(relative));
  }
}
