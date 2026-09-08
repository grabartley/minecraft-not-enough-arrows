package com.grahambartley.morearrows.gravity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class GravityShapeTest {
  private static final BlockPos CENTER = new BlockPos(10, 64, -30);

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -8})
  void aRadiusOfZeroOrLessDropsOnlyTheBlockThatWasHit(final int radius) {
    assertEquals(List.of(CENTER), GravityShape.blocks(CENTER, radius));
  }

  @Test
  void aMissingCentreDropsNothing() {
    assertEquals(List.of(), GravityShape.blocks(null, 4));
  }

  @ParameterizedTest
  @CsvSource({"1, 7", "2, 33", "3, 123", "4, 257", "8, 2109"})
  void theBlockCountMatchesTheSphereOfThatRadius(final int radius, final int expectedBlocks) {
    assertEquals(expectedBlocks, GravityShape.blocks(CENTER, radius).size());
  }

  @Test
  void aRadiusOfOneReachesTheSixBlocksTouchingTheOneThatWasHit() {
    assertEquals(
        List.of(
            CENTER,
            CENTER.add(-1, 0, 0),
            CENTER.add(0, -1, 0),
            CENTER.add(0, 0, -1),
            CENTER.add(0, 0, 1),
            CENTER.add(0, 1, 0),
            CENTER.add(1, 0, 0)),
        GravityShape.blocks(CENTER, 1));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void everyBlockStaysWithinTheRadius(final int radius) {
    for (final BlockPos block : GravityShape.blocks(CENTER, radius)) {
      assertTrue(
          squaredDistance(block) <= (long) radius * radius,
          "Block " + block + " falls outside radius " + radius);
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 8})
  void theBlockThatWasHitAlwaysFallsFirst(final int radius) {
    assertEquals(CENTER, GravityShape.blocks(CENTER, radius).get(0));
  }

  @Test
  void blocksAreOrderedOutwardsFromTheBlockThatWasHit() {
    long previousDistance = -1;
    for (final BlockPos block : GravityShape.blocks(CENTER, 3)) {
      final long distance = squaredDistance(block);
      assertTrue(distance >= previousDistance, "Block " + block + " broke outward ordering");
      previousDistance = distance;
    }
  }

  @Test
  void everyBlockIsDroppedOnlyOnce() {
    final List<BlockPos> blocks = GravityShape.blocks(CENTER, 4);

    assertEquals(blocks.size(), blocks.stream().distinct().count());
  }

  @Test
  void theSameRadiusAlwaysProducesTheSameBlocksInTheSameOrder() {
    assertEquals(GravityShape.blocks(CENTER, 3), GravityShape.blocks(CENTER, 3));
  }

  @Test
  void theReturnedBlocksCannotBeEdited() {
    final List<BlockPos> blocks = GravityShape.blocks(CENTER, 2);

    assertThrows(UnsupportedOperationException.class, () -> blocks.add(BlockPos.ORIGIN));
  }

  @Test
  void theSingleBlockDefaultCannotBeEdited() {
    final List<BlockPos> blocks = GravityShape.blocks(CENTER, 0);

    assertThrows(UnsupportedOperationException.class, () -> blocks.add(BlockPos.ORIGIN));
  }

  private static long squaredDistance(final BlockPos block) {
    final long offsetX = (long) block.getX() - CENTER.getX();
    final long offsetY = (long) block.getY() - CENTER.getY();
    final long offsetZ = (long) block.getZ() - CENTER.getZ();
    return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
  }
}
