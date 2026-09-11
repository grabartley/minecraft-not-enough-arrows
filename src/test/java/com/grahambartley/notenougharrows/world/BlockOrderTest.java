package com.grahambartley.notenougharrows.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BlockOrderTest {
  private static final BlockPos CENTER = new BlockPos(10, 64, -30);

  @ParameterizedTest
  @CsvSource({"0, 0, 0, 0", "1, 0, 0, 1", "-1, 0, 0, 1", "0, 2, 0, 4", "3, 0, 4, 25", "1, 2, 2, 9"})
  void measuresTheSquaredDistanceFromTheCentreInThreeDimensions(
      final int offsetX, final int offsetY, final int offsetZ, final long expected) {
    assertEquals(
        expected, BlockOrder.squaredDistance(CENTER, CENTER.add(offsetX, offsetY, offsetZ)));
  }

  @Test
  void aDistanceIsMeasuredWideEnoughToSurviveWorldEdgeCoordinates() {
    final BlockPos farEast = new BlockPos(30_000_000, 0, 0);
    final BlockPos farWest = new BlockPos(-30_000_000, 0, 0);

    assertEquals(3_600_000_000_000_000L, BlockOrder.squaredDistance(farWest, farEast));
  }

  @Test
  void theCentreAlwaysSortsFirst() {
    final List<BlockPos> blocks =
        sorted(List.of(CENTER.add(2, 0, 0), CENTER.add(0, -1, 0), CENTER, CENTER.add(0, 0, 3)));

    assertEquals(CENTER, blocks.get(0));
  }

  @Test
  void blocksSortOutwardsFromTheCentre() {
    final List<BlockPos> blocks =
        sorted(List.of(CENTER.add(0, 0, 3), CENTER.add(2, 0, 0), CENTER, CENTER.add(0, -1, 0)));

    long previous = -1;
    for (final BlockPos block : blocks) {
      final long distance = BlockOrder.squaredDistance(CENTER, block);
      assertTrue(distance >= previous, "Block " + block + " broke outward ordering");
      previous = distance;
    }
  }

  @Test
  void blocksTheSameDistanceOutSortByPositionSoTheOrderIsNeverArbitrary() {
    final List<BlockPos> equallyFarOut =
        List.of(
            CENTER.add(1, 0, 0),
            CENTER.add(0, 1, 0),
            CENTER.add(0, 0, 1),
            CENTER.add(0, 0, -1),
            CENTER.add(0, -1, 0),
            CENTER.add(-1, 0, 0));

    assertEquals(
        List.of(
            CENTER.add(-1, 0, 0),
            CENTER.add(0, -1, 0),
            CENTER.add(0, 0, -1),
            CENTER.add(0, 0, 1),
            CENTER.add(0, 1, 0),
            CENTER.add(1, 0, 0)),
        sorted(equallyFarOut));
  }

  private static List<BlockPos> sorted(final List<BlockPos> blocks) {
    final List<BlockPos> ordered = new ArrayList<>(blocks);
    ordered.sort(BlockOrder.nearestFirst(CENTER));
    return ordered;
  }
}
