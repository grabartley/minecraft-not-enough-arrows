package com.grahambartley.notenougharrows.structure;

import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public record StructureBlock(BlockPos pos, BlockState state) {

  public StructureBlock {
    Objects.requireNonNull(pos, "pos");
    Objects.requireNonNull(state, "state");
    pos = pos.toImmutable();
  }
}
