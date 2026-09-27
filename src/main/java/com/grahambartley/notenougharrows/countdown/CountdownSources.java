package com.grahambartley.notenougharrows.countdown;

import com.grahambartley.notenougharrows.control.AllegianceCountdowns;
import com.grahambartley.notenougharrows.fuse.Fuse;
import com.grahambartley.notenougharrows.fuse.FuseService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class CountdownSources {
  private static final long FUSES_NEVER_RESTART = 0L;

  private CountdownSources() {}

  public static List<CountdownTimer> timersIn(final ServerWorld world) {
    final List<CountdownTimer> timers = new ArrayList<>();
    for (final Fuse fuse : FuseService.fusesIn(world)) {
      timers.add(fromFuse(fuse));
    }
    timers.addAll(AllegianceCountdowns.timersIn(world));
    return timers;
  }

  @Nullable
  public static CountdownTimer timerOn(final ServerWorld world, final UUID hostId) {
    final Fuse fuse = FuseService.fuseOn(world, hostId);
    if (fuse != null && !fuse.hasExpired()) {
      return fromFuse(fuse);
    }
    return AllegianceCountdowns.timerOn(world, hostId);
  }

  private static CountdownTimer fromFuse(final Fuse fuse) {
    return new CountdownTimer(
        fuse.hostId(),
        fuse.delayTicks(),
        fuse.remainingTicks(),
        FUSES_NEVER_RESTART,
        CountdownKind.FUSE,
        Optional.empty());
  }
}
