package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.anchor.AnchorSite;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.world.BlockEditPermission;
import com.grahambartley.notenougharrows.world.BlockPlacement;
import com.grahambartley.notenougharrows.world.LoadedGround;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SpanSurvey {

  private SpanSurvey() {}

  public static Optional<SpanRefusal> refusal(
      final ServerWorld world,
      final BlockPos from,
      final BlockPos to,
      final List<BlockPos> cable,
      final BlockState cableState,
      @Nullable final PlayerEntity shooter,
      final int maxSpanBlocks) {
    if (!AnchorSite.isSuitable(world, from) || !AnchorSite.isSuitable(world, to)) {
      return Optional.of(SpanRefusal.NOT_AN_ANCHOR);
    }
    if (SpanLine.separation(from, to) > maxSpanBlocks) {
      return Optional.of(SpanRefusal.TOO_FAR);
    }
    if (cable.isEmpty()) {
      return Optional.of(SpanRefusal.TOO_SHORT);
    }
    final Predicate<BlockPos> isLoaded = LoadedGround.in(world);
    for (final BlockPos pos : cable) {
      if (!isLoaded.test(pos)
          || !BlockEditPermission.allows(world, pos, shooter)
          || TimedStructureService.holds(world, pos)
          || !BlockPlacement.canOccupy(world, pos, cableState)) {
        return Optional.of(SpanRefusal.BLOCKED);
      }
    }
    return Optional.empty();
  }
}
