package com.grahambartley.notenougharrows.terrain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PillarColumnTest {
  private static final BlockPos STRUCK = new BlockPos(4, 70, -9);

  @Test
  void aColumnRisesStraightUpFromTheBlockAboveTheOneStruck() {
    assertEquals(List.of(STRUCK.up(1), STRUCK.up(2), STRUCK.up(3)), PillarColumn.above(STRUCK, 3));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 4, 16})
  void aColumnIsExactlyTheConfiguredHeight(final int height) {
    assertEquals(height, PillarColumn.above(STRUCK, height).size());
  }

  @Test
  void theStruckBlockIsNeverPartOfItsOwnColumn() {
    assertTrue(!PillarColumn.above(STRUCK, 16).contains(STRUCK));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void aHeightOfZeroOrLessRaisesNothing(final int height) {
    assertTrue(PillarColumn.above(STRUCK, height).isEmpty());
  }

  @Test
  void aMissingStruckBlockRaisesNothing() {
    assertTrue(PillarColumn.above(null, 4).isEmpty());
  }

  @Test
  void theReturnedColumnCannotBeEdited() {
    assertThrows(
        UnsupportedOperationException.class, () -> PillarColumn.above(STRUCK, 2).add(STRUCK));
  }
}
