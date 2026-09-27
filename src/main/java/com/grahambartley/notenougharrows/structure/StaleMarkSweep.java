package com.grahambartley.notenougharrows.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public final class StaleMarkSweep {
  private final Map<RegistryKey<World>, List<StructureMark>> pending = new HashMap<>();

  public void notice(
      final ServerWorld world, final WorldChunk chunk, final Predicate<StructureMark> isLive) {
    final List<StructureMark> stale = StructureChunkMarks.in(chunk).stale(isLive);
    if (!stale.isEmpty()) {
      pending.computeIfAbsent(world.getRegistryKey(), key -> new ArrayList<>()).addAll(stale);
    }
  }

  public int clearIn(final ServerWorld world) {
    final List<StructureMark> stale = pending.remove(world.getRegistryKey());
    if (stale == null) {
      return 0;
    }
    final Predicate<BlockPos> isLoaded = StructureRemoval.loadedIn(world);
    int cleared = 0;
    for (final StructureMark mark : stale) {
      if (isLoaded.test(mark.pos())
          && StructureRemoval.clear(world, mark.pos(), mark.structure())) {
        cleared++;
      }
    }
    return cleared;
  }

  public boolean hasPendingIn(final ServerWorld world) {
    return pending.containsKey(world.getRegistryKey());
  }

  public void forget() {
    pending.clear();
  }
}
