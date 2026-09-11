package com.grahambartley.notenougharrows.anchor;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record BlockAnchor(UUID ownerId, BlockPos pos, Identifier blockId, long expiryTick) {

  public BlockAnchor {
    Objects.requireNonNull(ownerId, "An anchor needs an owner");
    Objects.requireNonNull(pos, "An anchor needs a position");
    Objects.requireNonNull(blockId, "An anchor needs the block it holds onto");
    pos = pos.toImmutable();
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean holdsOnto(@Nullable final Identifier currentBlockId) {
    return blockId.equals(currentBlockId);
  }
}
