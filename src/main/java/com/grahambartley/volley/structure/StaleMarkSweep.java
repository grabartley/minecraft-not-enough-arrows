package com.grahambartley.volley.structure;

import com.grahambartley.volley.world.LoadedGround;
import com.grahambartley.volley.world.PerWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

public final class StaleMarkSweep {
  private final PerWorld<List<StructureMark>> pending = new PerWorld<>(ArrayList::new);

  public void notice(
      final ServerWorld world, final WorldChunk chunk, final Predicate<StructureMark> isLive) {
    final List<StructureMark> stale = StructureChunkMarks.in(chunk).stale(isLive);
    if (!stale.isEmpty()) {
      pending.of(world).addAll(stale);
    }
  }

  public int clearIn(final ServerWorld world) {
    final List<StructureMark> stale = pending.remove(world);
    if (stale == null) {
      return 0;
    }
    final Predicate<BlockPos> isLoaded = LoadedGround.in(world);
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
    return pending.in(world) != null;
  }

  public void forget() {
    pending.clear();
  }
}
