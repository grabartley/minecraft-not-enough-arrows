package com.grahambartley.notenougharrows.discovery;

import com.grahambartley.notenougharrows.config.TripwireArrowConfig;
import com.grahambartley.notenougharrows.reveal.Watcher;
import com.grahambartley.notenougharrows.reveal.WatcherService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public final class TripwireService {

  private TripwireService() {}

  public static Optional<Watcher> set(
      final ServerWorld world,
      final BlockPos struck,
      final Direction face,
      @Nullable final PlayerEntity shooter) {
    return set(world, struck, face, shooter, ServerConfigService.get().discovery().tripwire());
  }

  public static Optional<Watcher> set(
      final ServerWorld world,
      final BlockPos struck,
      final Direction face,
      @Nullable final PlayerEntity shooter,
      final TripwireArrowConfig tripwire) {
    if (world == null || struck == null || face == null || tripwire == null) {
      return Optional.empty();
    }
    final BlockPos pos = struck.offset(face);
    if (!world.isInBuildLimit(pos)
        || !world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        WatcherService.place(
            world, pos, shooter, tripwire.lifetimeTicks(), tripwire.reportIntervalTicks()));
  }
}
