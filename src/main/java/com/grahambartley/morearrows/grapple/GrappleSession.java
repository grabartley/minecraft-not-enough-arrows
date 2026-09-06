package com.grahambartley.morearrows.grapple;

import com.grahambartley.morearrows.anchor.BlockAnchor;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record GrappleSession(UUID playerId, BlockPos anchor, int remainingTicks) {

  public GrappleSession {
    Objects.requireNonNull(playerId, "A grapple needs the player it pulls");
    Objects.requireNonNull(anchor, "A grapple needs the block it pulls toward");
    anchor = anchor.toImmutable();
    remainingTicks = Math.max(0, remainingTicks);
  }

  public boolean hasExpired() {
    return remainingTicks == 0;
  }

  public GrappleSession pulled() {
    return new GrappleSession(playerId, anchor, remainingTicks - 1);
  }

  public Vec3d target() {
    return Vec3d.ofCenter(anchor);
  }

  public boolean holdsOnto(@Nullable final BlockAnchor held) {
    return held != null && anchor.equals(held.pos());
  }
}
