package com.grahambartley.notenougharrows.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class BlockDiscTest {
  private static final BlockPos CENTER = new BlockPos(10, 64, -30);

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -8})
  void aRadiusOfZeroOrLessIsTheCentreAlone(final int radius) {
    assertEquals(List.of(CENTER), BlockDisc.blocks(CENTER, radius));
  }

  @Test
  void aMissingCentreHoldsNothing() {
    assertEquals(List.of(), BlockDisc.blocks(null, 4));
  }

  @ParameterizedTest
  @CsvSource({"1, 5", "2, 13", "3, 29", "8, 197"})
  void theBlockCountMatchesTheDiscOfThatRadius(final int radius, final int expectedBlocks) {
    assertEquals(expectedBlocks, BlockDisc.blocks(CENTER, radius).size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void everyBlockStaysOnTheCentresLayer(final int radius) {
    assertTrue(
        BlockDisc.blocks(CENTER, radius).stream().allMatch(pos -> pos.getY() == CENTER.getY()));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void everyBlockStaysWithinTheRadius(final int radius) {
    assertTrue(
        BlockDisc.blocks(CENTER, radius).stream()
            .allMatch(pos -> pos.getSquaredDistance(CENTER) <= (double) radius * radius));
  }

  @Test
  void theCentreComesFirstAndTheFarthestLast() {
    final List<BlockPos> disc = BlockDisc.blocks(CENTER, 2);

    assertEquals(CENTER, disc.get(0));
    assertEquals(4.0, disc.get(disc.size() - 1).getSquaredDistance(CENTER));
  }
}
