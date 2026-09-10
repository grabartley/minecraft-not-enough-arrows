package com.grahambartley.morearrows.fletching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.grahambartley.morearrows.recipe.FletchingRecipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FletchingStationSlotsTest {

  @Test
  void offersOneInputSlotForEveryIngredientARecipeMayDeclare() {
    assertEquals(FletchingRecipe.MAX_INPUTS, FletchingStationSlots.INPUT_COUNT);
  }

  @Test
  void countsInputsResultAndTheWholePlayerInventory() {
    assertEquals(46, FletchingStationSlots.TOTAL_SLOTS);
  }

  @Test
  void putsTheResultAfterEveryInputAndThePlayerInventoryAfterTheResult() {
    assertEquals(FletchingStationSlots.INPUT_COUNT, FletchingStationSlots.RESULT_SLOT);
    assertEquals(FletchingStationSlots.RESULT_SLOT + 1, FletchingStationSlots.FIRST_PLAYER_SLOT);
    assertEquals(
        FletchingStationSlots.FIRST_PLAYER_SLOT + FletchingStationSlots.PLAYER_MAIN_COUNT,
        FletchingStationSlots.FIRST_HOTBAR_SLOT);
  }

  @ParameterizedTest(name = "slot {0}")
  @CsvSource({"0, input", "8, input", "9, result", "10, player", "45, player"})
  void assignsEverySlotIndexToExactlyOneRegion(final int slot, final String region) {
    assertEquals("input".equals(region), FletchingStationSlots.isInput(slot));
    assertEquals("result".equals(region), FletchingStationSlots.isResult(slot));
  }

  @ParameterizedTest(name = "slot {0}")
  @ValueSource(ints = {-1, 46})
  void claimsNoRegionForASlotOutsideTheHandler(final int slot) {
    assertFalse(FletchingStationSlots.isInput(slot));
    assertFalse(FletchingStationSlots.isResult(slot));
  }
}
