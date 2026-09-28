package com.grahambartley.notenougharrows.zipline;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record Span(UUID id, List<BlockPos> cable, long expiryTick) {

  public Span {
    Objects.requireNonNull(id, "A span needs the structure it was strung as");
    cable = cable == null ? List.of() : List.copyOf(cable);
    if (cable.isEmpty()) {
      throw new IllegalArgumentException("A span needs at least one length of cable");
    }
  }

  public Vec3d firstEnd() {
    return Vec3d.ofCenter(cable.getFirst());
  }

  public Vec3d lastEnd() {
    return Vec3d.ofCenter(cable.getLast());
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean isWhole(final List<BlockPos> standing) {
    return standing != null && standing.size() == cable.size() && standing.containsAll(cable);
  }
}
