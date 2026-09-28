package com.grahambartley.notenougharrows.structure;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record TimedStructure(
    UUID id, @Nullable UUID owner, List<BlockPos> positions, long expiryTick) {

  public TimedStructure {
    Objects.requireNonNull(id, "id");
    positions = positions == null ? List.of() : List.copyOf(positions);
  }

  public boolean isEmpty() {
    return positions.isEmpty();
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean holds(final BlockPos pos) {
    return positions.contains(pos);
  }

  public TimedStructure without(final BlockPos pos) {
    if (!holds(pos)) {
      return this;
    }
    return new TimedStructure(
        id, owner, positions.stream().filter(held -> !held.equals(pos)).toList(), expiryTick);
  }
}
