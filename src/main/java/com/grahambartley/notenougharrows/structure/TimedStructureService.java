package com.grahambartley.notenougharrows.structure;

import com.grahambartley.notenougharrows.world.LoadedGround;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public final class TimedStructureService {
  private static final Map<RegistryKey<World>, StructureLedger> LEDGERS = new HashMap<>();
  private static final StaleMarkSweep STALE = new StaleMarkSweep();

  private TimedStructureService() {}

  public static void register() {
    StructureChunkMarks.register();
    ServerChunkEvents.CHUNK_LOAD.register(TimedStructureService::onChunkLoad);
    ServerTickEvents.END_WORLD_TICK.register(TimedStructureService::tick);
    ServerLifecycleEvents.SERVER_STOPPING.register(TimedStructureService::clearEverything);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static Optional<TimedStructure> build(
      final ServerWorld world,
      @Nullable final PlayerEntity owner,
      final List<BlockPos> candidates,
      final StructureBlockSource source,
      final StructureBudget budget,
      final int lifetimeTicks) {
    if (world == null
        || candidates == null
        || source == null
        || budget == null
        || budget.isNone()
        || lifetimeTicks <= 0) {
      return Optional.empty();
    }

    final StructureLedger ledger = ledgerFor(world);
    final UUID id = UUID.randomUUID();
    final List<StructureBlock> placed =
        StructurePlacer.place(world, id, candidates, source, owner, budget, ledger::holds);
    if (placed.isEmpty()) {
      return Optional.empty();
    }

    final TimedStructure structure =
        new TimedStructure(
            id,
            owner == null ? null : owner.getUuid(),
            placed.stream().map(StructureBlock::pos).toList(),
            world.getTime() + lifetimeTicks);
    ledger.add(structure);
    return Optional.of(structure);
  }

  public static Optional<TimedStructure> find(final ServerWorld world, final UUID id) {
    final StructureLedger ledger = world == null ? null : LEDGERS.get(world.getRegistryKey());
    return ledger == null ? Optional.empty() : ledger.find(id);
  }

  public static boolean dismantle(final ServerWorld world, final UUID id) {
    final StructureLedger ledger = world == null ? null : LEDGERS.get(world.getRegistryKey());
    if (ledger == null) {
      return false;
    }
    return ledger
        .take(id)
        .map(
            structure -> {
              StructureRemoval.remove(world, structure, LoadedGround.in(world));
              return true;
            })
        .orElse(false);
  }

  public static boolean holds(final ServerWorld world, final BlockPos pos) {
    final StructureLedger ledger = LEDGERS.get(world.getRegistryKey());
    return ledger != null && ledger.holds(pos);
  }

  public static void expireIn(
      final ServerWorld world, final long tick, final Predicate<BlockPos> isLoaded) {
    final StructureLedger ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger == null || ledger.isEmpty()) {
      return;
    }
    ledger
        .takeExpired(tick)
        .forEach(structure -> StructureRemoval.remove(world, structure, isLoaded));
  }

  public static void clearAll(final ServerWorld world) {
    STALE.clearIn(world);
    final StructureLedger ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger == null) {
      return;
    }
    final Predicate<BlockPos> isLoaded = LoadedGround.in(world);
    ledger.takeAll().forEach(structure -> StructureRemoval.remove(world, structure, isLoaded));
  }

  public static void onChunkLoad(final ServerWorld world, final WorldChunk chunk) {
    final StructureLedger ledger = LEDGERS.get(world.getRegistryKey());
    final long tick = world.getTime();
    STALE.notice(world, chunk, mark -> ledger != null && ledger.isLive(mark.structure(), tick));
  }

  public static void onBlockChanged(
      final ServerWorld world, final WorldChunk chunk, final BlockPos pos, final BlockState state) {
    final Optional<StructureMark> mark = StructureChunkMarks.at(chunk, pos);
    if (mark.isEmpty() || StructureChunkMarks.stillHolds(mark.get(), state)) {
      return;
    }
    StructureChunkMarks.unmark(chunk, pos);
    final StructureLedger ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger != null) {
      ledger.release(pos);
    }
  }

  public static void forget() {
    LEDGERS.clear();
    STALE.forget();
  }

  private static void tick(final ServerWorld world) {
    STALE.clearIn(world);
    expireIn(world, world.getTime(), LoadedGround.in(world));
  }

  private static void clearEverything(final MinecraftServer server) {
    if (server != null) {
      server.getWorlds().forEach(TimedStructureService::clearAll);
    }
  }

  private static StructureLedger ledgerFor(final ServerWorld world) {
    return LEDGERS.computeIfAbsent(world.getRegistryKey(), key -> new StructureLedger());
  }
}
