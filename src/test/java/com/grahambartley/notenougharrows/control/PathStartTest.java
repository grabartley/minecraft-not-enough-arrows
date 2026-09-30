package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.junit.jupiter.api.Test;

class PathStartTest {
  private static final int FEET = 5;
  private static final Box WIDE_SWIMMER = new Box(616.2, FEET, 207.8, 618.2, FEET + 2, 209.8);

  private static int firstNodeAhead(final List<BlockPos> nodes, final int current, final Box box) {
    return PathStart.firstNodeAhead(nodes::get, current, nodes.size(), box);
  }

  @Test
  void skipsTheCornerNodeAWideSwimmerAlreadyCovers() {
    final List<BlockPos> nodes =
        List.of(
            new BlockPos(616, FEET, 207),
            new BlockPos(617, FEET, 208),
            new BlockPos(619, FEET, 208),
            new BlockPos(620, FEET, 208));

    assertEquals(2, firstNodeAhead(nodes, 0, WIDE_SWIMMER));
  }

  @Test
  void keepsTheFirstNodeWhenItIsAhead() {
    final List<BlockPos> nodes =
        List.of(new BlockPos(619, FEET, 208), new BlockPos(620, FEET, 208));

    assertEquals(0, firstNodeAhead(nodes, 0, WIDE_SWIMMER));
  }

  @Test
  void neverSkipsTheLastNode() {
    final List<BlockPos> nodes =
        List.of(new BlockPos(616, FEET, 208), new BlockPos(617, FEET, 208));

    assertEquals(1, firstNodeAhead(nodes, 0, WIDE_SWIMMER));
  }

  @Test
  void startsFromTheNodeThePathHasAlreadyReached() {
    final List<BlockPos> nodes =
        List.of(
            new BlockPos(610, FEET, 208),
            new BlockPos(617, FEET, 208),
            new BlockPos(619, FEET, 208));

    assertEquals(2, firstNodeAhead(nodes, 1, WIDE_SWIMMER));
  }

  @Test
  void keepsAStepUpEvenWhenItIsBesideTheMob() {
    final List<BlockPos> nodes =
        List.of(new BlockPos(617, FEET + 1, 208), new BlockPos(619, FEET + 1, 208));

    assertEquals(0, firstNodeAhead(nodes, 0, WIDE_SWIMMER));
  }

  @Test
  void aSwimmerJustBelowABlockStillCoversTheNodeAboveIt() {
    final Box sinking = new Box(616.2, FEET - 0.1, 207.8, 618.2, FEET + 1.9, 209.8);

    assertTrue(PathStart.isUnderfoot(new BlockPos(616, FEET, 207), sinking));
  }

  @Test
  void aNodeBelowTheFeetIsNotUnderfoot() {
    assertFalse(PathStart.isUnderfoot(new BlockPos(617, FEET - 1, 208), WIDE_SWIMMER));
  }

  @Test
  void aNodeTouchingTheFootprintOnlyAtAnEdgeIsNotUnderfoot() {
    final Box oneBlock = new Box(3.0, FEET, 3.0, 4.0, FEET + 1, 4.0);

    assertFalse(PathStart.isUnderfoot(new BlockPos(4, FEET, 3), oneBlock));
    assertTrue(PathStart.isUnderfoot(new BlockPos(3, FEET, 3), oneBlock));
  }
}
