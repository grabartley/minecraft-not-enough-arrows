package com.grahambartley.notenougharrows.reveal;

import com.grahambartley.notenougharrows.world.BlockSphere;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public final class RevealScan {

  private RevealScan() {}

  public static List<BlockPos> matching(
      final BlockPos center,
      final int radius,
      final int limit,
      final Predicate<BlockPos> isLoaded,
      final Predicate<BlockPos> reveals) {
    if (center == null || radius < 0 || limit <= 0 || isLoaded == null || reveals == null) {
      return List.of();
    }
    final List<BlockPos> found = new ArrayList<>();
    for (final BlockPos pos : BlockSphere.blocks(center, radius)) {
      if (found.size() >= limit) {
        break;
      }
      if (isLoaded.test(pos) && reveals.test(pos)) {
        found.add(pos);
      }
    }
    return List.copyOf(found);
  }
}
