package com.grahambartley.notenougharrows.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class BlockSphereTest {
  private static final BlockPos CENTER = new BlockPos(10, 64, -30);

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -8})
  void aRadiusOfZeroOrLessIsTheCentreAlone(final int radius) {
    assertEquals(List.of(CENTER), BlockSphere.blocks(CENTER, radius));
  }

  @Test
  void aMissingCentreHoldsNothing() {
    assertEquals(List.of(), BlockSphere.blocks(null, 4));
  }

  @ParameterizedTest
  @CsvSource({"1, 7", "2, 33", "3, 123", "4, 257", "8, 2109"})
  void theBlockCountMatchesTheSphereOfThatRadius(final int radius, final int expectedBlocks) {
    assertEquals(expectedBlocks, BlockSphere.blocks(CENTER, radius).size());
  }

  @Test
  void aRadiusOfOneReachesTheSixBlocksTouchingTheCentre() {
    assertEquals(
        List.of(
            CENTER,
            CENTER.add(-1, 0, 0),
            CENTER.add(0, -1, 0),
            CENTER.add(0, 0, -1),
            CENTER.add(0, 0, 1),
            CENTER.add(0, 1, 0),
            CENTER.add(1, 0, 0)),
        BlockSphere.blocks(CENTER, 1));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void everyBlockStaysWithinTheRadius(final int radius) {
    for (final BlockPos block : BlockSphere.blocks(CENTER, radius)) {
      assertTrue(
          squaredDistance(block) <= (long) radius * radius,
          "Block " + block + " lies outside radius " + radius);
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 8})
  void theCentreAlwaysComesFirst(final int radius) {
    assertEquals(CENTER, BlockSphere.blocks(CENTER, radius).get(0));
  }

  @Test
  void blocksAreOrderedOutwardsFromTheCentre() {
    long previousDistance = -1;
    for (final BlockPos block : BlockSphere.blocks(CENTER, 3)) {
      final long distance = squaredDistance(block);
      assertTrue(distance >= previousDistance, "Block " + block + " broke outward ordering");
      previousDistance = distance;
    }
  }

  @Test
  void everyBlockAppearsOnlyOnce() {
    final List<BlockPos> blocks = BlockSphere.blocks(CENTER, 4);

    assertEquals(blocks.size(), blocks.stream().distinct().count());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2})
  void theReturnedBlocksCannotBeEdited(final int radius) {
    final List<BlockPos> blocks = BlockSphere.blocks(CENTER, radius);

    assertThrows(UnsupportedOperationException.class, () -> blocks.add(BlockPos.ORIGIN));
  }

  private static long squaredDistance(final BlockPos block) {
    final long offsetX = (long) block.getX() - CENTER.getX();
    final long offsetY = (long) block.getY() - CENTER.getY();
    final long offsetZ = (long) block.getZ() - CENTER.getZ();
    return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
  }
}
