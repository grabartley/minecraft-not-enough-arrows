package com.grahambartley.notenougharrows.reveal;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record Watcher(
    UUID id,
    @Nullable UUID owner,
    BlockPos pos,
    long expiryTick,
    int reportIntervalTicks,
    long nextReportTick) {

  public Watcher {
    Objects.requireNonNull(id, "id");
    pos = Objects.requireNonNull(pos, "pos").toImmutable();
    reportIntervalTicks = Math.max(1, reportIntervalTicks);
  }

  public static Watcher placed(
      @Nullable final UUID owner,
      final BlockPos pos,
      final long tick,
      final int lifetimeTicks,
      final int reportIntervalTicks) {
    return new Watcher(
        UUID.randomUUID(), owner, pos, tick + lifetimeTicks, reportIntervalTicks, tick);
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean canReportAt(final long tick) {
    return owner != null && tick >= nextReportTick;
  }

  public boolean ignores(@Nullable final UUID crosser) {
    return owner != null && owner.equals(crosser);
  }

  public Watcher reportedAt(final long tick) {
    return new Watcher(id, owner, pos, expiryTick, reportIntervalTicks, tick + reportIntervalTicks);
  }
}
