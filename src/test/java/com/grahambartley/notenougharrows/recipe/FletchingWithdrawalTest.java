package com.grahambartley.notenougharrows.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingWithdrawalTest {

  @ParameterizedTest(name = "slot {0} count {1}")
  @CsvSource({"0, 1", "8, 64", "3, 4"})
  void keepsTheSlotAndCountItWasGiven(final int slot, final int count) {
    final FletchingWithdrawal withdrawal = new FletchingWithdrawal(slot, count);

    assertEquals(slot, withdrawal.slot());
    assertEquals(count, withdrawal.count());
  }

  @ParameterizedTest(name = "slot {0} count {1}")
  @CsvSource({"-1, 1", "0, 0", "0, -1"})
  void refusesAWithdrawalThatCouldNotBeAppliedToASlot(final int slot, final int count) {
    assertThrows(IllegalArgumentException.class, () -> new FletchingWithdrawal(slot, count));
  }

  @Test
  void refusesToPlanWithoutARecipeOrAnInput() {
    assertThrows(NullPointerException.class, () -> FletchingWithdrawal.plan(null, null));
  }
}
