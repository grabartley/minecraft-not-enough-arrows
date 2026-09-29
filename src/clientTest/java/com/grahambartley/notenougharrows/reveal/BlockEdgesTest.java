package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class BlockEdgesTest {

  @Test
  void aSingleBlockHasTwelveEdgesOfLengthOne() {
    final List<BlockEdges.Edge> edges = BlockEdges.of(List.of(new BlockPos(4, 60, -2)));

    assertEquals(12, edges.size());
    assertTrue(edges.stream().allMatch(edge -> edge.from().distanceTo(edge.to()) == 1.0));
  }

  @Test
  void theEdgesRunAlongTheBlocksCorners() {
    final List<BlockEdges.Edge> edges = BlockEdges.of(List.of(BlockPos.ORIGIN));

    assertTrue(edges.contains(new BlockEdges.Edge(Vec3d.ZERO, new Vec3d(1, 0, 0))));
    assertTrue(edges.contains(new BlockEdges.Edge(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1))));
    assertTrue(edges.contains(new BlockEdges.Edge(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1))));
  }

  @Test
  void twoTouchingBlocksShareTheirCommonEdgesOnce() {
    final List<BlockEdges.Edge> edges =
        BlockEdges.of(List.of(BlockPos.ORIGIN, BlockPos.ORIGIN.east()));

    assertEquals(20, edges.size());
  }

  @Test
  void noBlocksHaveNoEdges() {
    assertTrue(BlockEdges.of(List.of()).isEmpty());
  }
}
