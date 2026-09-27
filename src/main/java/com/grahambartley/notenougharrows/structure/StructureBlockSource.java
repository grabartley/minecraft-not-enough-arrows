package com.grahambartley.notenougharrows.structure;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface StructureBlockSource {

  @Nullable
  StructureBlock resolve(ServerWorld world, BlockPos candidate);

  static StructureBlockSource of(final BlockState state) {
    return (world, candidate) -> new StructureBlock(candidate, state);
  }
}
