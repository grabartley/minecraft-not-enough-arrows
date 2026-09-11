package com.grahambartley.morearrows.ender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class EnderLanding {
  public static final int NEIGHBOUR_OFFSET = 1;

  private EnderLanding() {}

  public static List<Vec3d> candidates(final Vec3d anchor) {
    Objects.requireNonNull(anchor, "anchor");

    final List<Vec3d> ring = new ArrayList<>();
    for (int x = -NEIGHBOUR_OFFSET; x <= NEIGHBOUR_OFFSET; x++) {
      for (int z = -NEIGHBOUR_OFFSET; z <= NEIGHBOUR_OFFSET; z++) {
        if (x != 0 || z != 0) {
          ring.add(anchor.add(x, 0.0, z));
        }
      }
    }
    ring.sort(
        Comparator.<Vec3d>comparingDouble(anchor::squaredDistanceTo)
            .thenComparingDouble(Vec3d::getX)
            .thenComparingDouble(Vec3d::getZ));

    final List<Vec3d> candidates = new ArrayList<>();
    candidates.add(anchor);
    candidates.addAll(ring);
    return List.copyOf(candidates);
  }

  public static Optional<Vec3d> forEntity(
      @Nullable final ServerWorld world,
      @Nullable final Entity subject,
      @Nullable final Vec3d anchor) {
    if (world == null || subject == null || anchor == null) {
      return Optional.empty();
    }

    final boolean anchorFits = fits(world, subject, anchor);
    return candidates(anchor).stream()
        .filter(candidate -> fits(world, subject, candidate) && isSupported(world, candidate))
        .findFirst()
        .or(() -> anchorFits ? Optional.of(anchor) : Optional.empty());
  }

  private static boolean fits(
      final ServerWorld world, final Entity subject, final Vec3d candidate) {
    return world.isSpaceEmpty(
        subject, subject.getDimensions(subject.getPose()).getBoxAt(candidate));
  }

  private static boolean isSupported(final ServerWorld world, final Vec3d candidate) {
    return !world.getBlockState(BlockPos.ofFloored(candidate).down()).isAir();
  }
}
