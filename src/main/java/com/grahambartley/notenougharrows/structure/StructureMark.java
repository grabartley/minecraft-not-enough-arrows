package com.grahambartley.notenougharrows.structure;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record StructureMark(BlockPos pos, Identifier block, UUID structure) {

  public StructureMark {
    Objects.requireNonNull(pos, "pos");
    Objects.requireNonNull(block, "block");
    Objects.requireNonNull(structure, "structure");
    pos = pos.toImmutable();
  }

  public boolean belongsTo(final UUID id) {
    return structure.equals(id);
  }
}
