package com.grahambartley.notenougharrows.reveal;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public record WatcherAlarm(UUID owner, BlockPos watcherPos, Entity crosser) {

  public WatcherAlarm {
    Objects.requireNonNull(owner, "owner");
    Objects.requireNonNull(watcherPos, "watcherPos");
    Objects.requireNonNull(crosser, "crosser");
  }
}
