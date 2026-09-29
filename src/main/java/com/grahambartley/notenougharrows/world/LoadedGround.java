package com.grahambartley.notenougharrows.world;

import java.util.function.Predicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public final class LoadedGround {

  private LoadedGround() {}

  public static Predicate<BlockPos> in(final ServerWorld world) {
    return pos ->
        world
            .getChunkManager()
            .isChunkLoaded(
                ChunkSectionPos.getSectionCoord(pos.getX()),
                ChunkSectionPos.getSectionCoord(pos.getZ()));
  }
}
