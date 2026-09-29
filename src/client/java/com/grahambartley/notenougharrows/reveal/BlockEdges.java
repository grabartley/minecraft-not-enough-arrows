package com.grahambartley.notenougharrows.reveal;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BlockEdges {

  private BlockEdges() {}

  public static List<Edge> of(final Collection<BlockPos> blocks) {
    final Set<Edge> edges = new LinkedHashSet<>();
    for (final BlockPos pos : blocks) {
      final int x = pos.getX();
      final int y = pos.getY();
      final int z = pos.getZ();
      for (final int atY : new int[] {y, y + 1}) {
        edges.add(Edge.between(x, atY, z, x + 1, atY, z));
        edges.add(Edge.between(x + 1, atY, z, x + 1, atY, z + 1));
        edges.add(Edge.between(x, atY, z + 1, x + 1, atY, z + 1));
        edges.add(Edge.between(x, atY, z, x, atY, z + 1));
      }
      for (final int atX : new int[] {x, x + 1}) {
        for (final int atZ : new int[] {z, z + 1}) {
          edges.add(Edge.between(atX, y, atZ, atX, y + 1, atZ));
        }
      }
    }
    return List.copyOf(edges);
  }

  public record Edge(Vec3d from, Vec3d to) {

    static Edge between(
        final int fromX,
        final int fromY,
        final int fromZ,
        final int toX,
        final int toY,
        final int toZ) {
      return new Edge(new Vec3d(fromX, fromY, fromZ), new Vec3d(toX, toY, toZ));
    }
  }
}
