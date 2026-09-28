package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.ZiplineArrowConfig;
import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class SpanService {
  private static final Map<RegistryKey<World>, SpanTracker> TRACKERS = new HashMap<>();

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
    trackerFor(world).add(span);
    return SpanResult.strung(span);
  }

  public static Optional<Span> spanAt(final ServerWorld world, final BlockPos pos) {
    final SpanTracker tracker = trackerIn(world);
    return tracker == null
        ? Optional.empty()
        : tracker.at(pos).filter(span -> isIntact(world, span));
  }

  public static Optional<Span> find(final ServerWorld world, final UUID id) {
    final SpanTracker tracker = trackerIn(world);
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
    final SpanTracker tracker = trackerIn(world);
    if (tracker != null && !tracker.isEmpty()) {
      tracker.removeExpired(world.getTime());
    }
  }

  @Nullable
  private static SpanTracker trackerIn(@Nullable final ServerWorld world) {
    return world == null ? null : TRACKERS.get(world.getRegistryKey());
  }

  private static SpanTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new SpanTracker());
  }
}
