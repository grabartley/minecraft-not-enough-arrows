package com.grahambartley.morearrows.rope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RopeShapeTest {
  private static final BlockPos ANCHOR = new BlockPos(4, 70, -9);

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 16, 128})
  @DisplayName("a rope is as long as it was asked to be")
  void aRopeIsAsLongAsItWasAskedToBe(final int maxLength) {
    assertEquals(maxLength, RopeShape.below(ANCHOR, maxLength).size());
  }

  @Test
  @DisplayName("a rope hangs from the block below its anchor")
  void aRopeHangsFromTheBlockBelowItsAnchor() {
    assertEquals(ANCHOR.down(), RopeShape.below(ANCHOR, 4).getFirst());
  }

  @Test
  @DisplayName("a rope runs straight down from its anchor")
  void aRopeRunsStraightDownFromItsAnchor() {
    final List<BlockPos> column = RopeShape.below(ANCHOR, 5);

    for (int depth = 0; depth < column.size(); depth++) {
      assertEquals(ANCHOR.down(depth + 1), column.get(depth));
    }
  }

  @Test
  @DisplayName("a rope is ordered from the anchor downward")
  void aRopeIsOrderedFromTheAnchorDownward() {
    final List<BlockPos> column = RopeShape.below(ANCHOR, 6);

    for (int depth = 1; depth < column.size(); depth++) {
      assertTrue(
          column.get(depth).getY() < column.get(depth - 1).getY(),
          "Rope segments should be handed back top first so placement stops at the first"
              + " obstruction");
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
  @DisplayName("a rope of no length is no rope at all")
  void aRopeOfNoLengthIsNoRopeAtAll(final int maxLength) {
    assertTrue(RopeShape.below(ANCHOR, maxLength).isEmpty());
  }

  @Test
  @DisplayName("a rope with nothing to hang from is no rope at all")
  void aRopeWithNothingToHangFromIsNoRopeAtAll() {
    assertTrue(RopeShape.below(null, 16).isEmpty());
  }
}
