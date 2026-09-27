package com.grahambartley.notenougharrows.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class StructureBudgetTest {

  @ParameterizedTest
  @ValueSource(ints = {-1, -64, Integer.MIN_VALUE})
  void aNegativeBudgetIsNoBudgetAtAll(final int given) {
    final StructureBudget budget = StructureBudget.of(given);

    assertEquals(0, budget.maxPositions());
    assertTrue(budget.isNone());
  }

  @Test
  void aZeroBudgetHasNoRoomForAnything() {
    assertFalse(StructureBudget.of(0).hasRoomAfter(0));
  }

  @ParameterizedTest
  @CsvSource({"1,0", "8,0", "8,7"})
  void aBudgetHasRoomUntilItIsSpent(final int max, final int placed) {
    assertTrue(StructureBudget.of(max).hasRoomAfter(placed));
  }

  @ParameterizedTest
  @CsvSource({"1,1", "8,8", "8,9"})
  void aSpentBudgetHasNoRoomLeft(final int max, final int placed) {
    assertFalse(StructureBudget.of(max).hasRoomAfter(placed));
  }

  @Test
  void aPositiveBudgetIsNotNone() {
    assertFalse(StructureBudget.of(1).isNone());
  }
}
