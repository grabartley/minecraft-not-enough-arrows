package com.grahambartley.notenougharrows.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class TrampolinePadTest {
  private static final BlockPos CENTER = new BlockPos(3, 65, 3);

  @Test
  void aPadIsAFlatThreeByThreeSquare() {
    final List<BlockPos> pad = TrampolinePad.around(CENTER);

    assertEquals(9, pad.size());
    assertEquals(9, new HashSet<>(pad).size());
    for (final BlockPos pos : pad) {
      assertEquals(CENTER.getY(), pos.getY());
      assertTrue(Math.abs(pos.getX() - CENTER.getX()) <= 1);
      assertTrue(Math.abs(pos.getZ() - CENTER.getZ()) <= 1);
    }
  }

  @Test
  void theCentreIsPlacedFirstSoACrowdedPadStillHasAMiddle() {
    final List<BlockPos> pad = TrampolinePad.around(CENTER);

    assertEquals(CENTER, pad.getFirst());
    assertEquals(2, pad.getLast().getManhattanDistance(CENTER));
  }

  @Test
  void noCentreMeansNoPad() {
    assertTrue(TrampolinePad.around(null).isEmpty());
  }
}
