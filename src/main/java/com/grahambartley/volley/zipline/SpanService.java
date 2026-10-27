package com.grahambartley.volley.zipline;

import com.grahambartley.volley.ModBlocks;
import com.grahambartley.volley.config.ZiplineArrowConfig;
import com.grahambartley.volley.structure.StructureBlockSource;
import com.grahambartley.volley.structure.StructureBudget;
import com.grahambartley.volley.structure.TimedStructure;
import com.grahambartley.volley.structure.TimedStructureService;
import com.grahambartley.volley.world.PerWorld;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class SpanService {
  private static final PerWorld<SpanTracker> TRACKERS = new PerWorld<>(SpanTracker::new);

  private SpanService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(SpanService::forgetExpiredIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static SpanResult string(
      final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      final BlockPos from,
      final BlockPos to,
      final ZiplineArrowConfig zipline) {
    if (world == null || from == null || to == null || zipline == null) {
      return SpanResult.refused(SpanRefusal.NOT_STRUNG);
    }
    final List<BlockPos> cable = SpanLine.between(from, to);
    final BlockState cableState =
        ModBlocks.ZIPLINE_CABLE.getDefaultState().with(PillarBlock.AXIS, SpanLine.axisOf(from, to));
    final Optional<SpanRefusal> refusal =
        SpanSurvey.refusal(world, from, to, cable, cableState, shooter, zipline.maxSpanBlocks());
    if (refusal.isPresent()) {
      return SpanResult.refused(refusal.get());
    }
    final Optional<TimedStructure> built =
        TimedStructureService.build(
            world,
            shooter,
            cable,
            StructureBlockSource.of(cableState),
            StructureBudget.of(cable.size()),
            zipline.lifetimeTicks());
    if (built.isEmpty()) {
      return SpanResult.refused(SpanRefusal.NOT_STRUNG);
    }
    final Span span = new Span(built.get().id(), cable, built.get().expiryTick());
    if (!span.isWhole(built.get().positions())) {
      TimedStructureService.dismantle(world, span.id());
      return SpanResult.refused(SpanRefusal.NOT_STRUNG);
    }
    TRACKERS.of(world).add(span);
    return SpanResult.strung(span);
  }

  public static Optional<Span> spanAt(final ServerWorld world, final BlockPos pos) {
    final SpanTracker tracker = TRACKERS.in(world);
    return tracker == null
        ? Optional.empty()
        : tracker.at(pos).filter(span -> isIntact(world, span));
  }

  public static Optional<Span> find(final ServerWorld world, final UUID id) {
    final SpanTracker tracker = TRACKERS.in(world);
    return tracker == null ? Optional.empty() : tracker.find(id);
  }

  public static boolean isIntact(final ServerWorld world, final Span span) {
    return world != null
        && span != null
        && !span.hasExpired(world.getTime())
        && TimedStructureService.find(world, span.id())
            .map(TimedStructure::positions)
            .filter(span::isWhole)
            .isPresent();
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void forgetExpiredIn(final ServerWorld world) {
    final SpanTracker tracker = TRACKERS.in(world);
    if (tracker != null && !tracker.isEmpty()) {
      tracker.removeExpired(world.getTime());
    }
  }
}
