package com.grahambartley.morearrows.fletching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.recipe.FletchingRecipe;
import java.util.HashSet;
import java.util.Set;
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
  void arrangesEveryInputSlotIntoTheDeclaredGrid() {
    assertEquals(
        FletchingStationSlots.INPUT_COUNT,
        FletchingStationSlots.INPUT_COLUMNS * FletchingStationSlots.INPUT_ROWS);
  }

  @Test
  void countsInputsResultAndTheWholePlayerInventory() {
    assertEquals(FletchingStationSlots.INPUT_COUNT + 1 + 36, FletchingStationSlots.TOTAL_SLOTS);
  }

  @ParameterizedTest(name = "slot {0}")
  @CsvSource({"0, input", "8, input", "9, result", "10, player", "45, player"})
  void assignsEverySlotIndexToExactlyOneRegion(final int slot, final String region) {
    assertEquals("input".equals(region), FletchingStationSlots.isInput(slot));
    assertEquals("result".equals(region), FletchingStationSlots.isResult(slot));
    assertEquals("player".equals(region), FletchingStationSlots.isPlayerInventory(slot));
  }

  @ParameterizedTest(name = "slot {0}")
  @ValueSource(ints = {-1, 46})
  void claimsNoRegionForASlotOutsideTheHandler(final int slot) {
    assertFalse(FletchingStationSlots.isInput(slot));
    assertFalse(FletchingStationSlots.isResult(slot));
    assertFalse(FletchingStationSlots.isPlayerInventory(slot));
  }

  @Test
  void placesEveryInputSlotAtItsOwnScreenPosition() {
    final Set<String> positions = new HashSet<>();
    for (int index = 0; index < FletchingStationSlots.INPUT_COUNT; index++) {
      positions.add(
          FletchingStationSlots.inputX(index) + "," + FletchingStationSlots.inputY(index));
    }

    assertEquals(FletchingStationSlots.INPUT_COUNT, positions.size());
  }

  @Test
  void placesEveryPlayerSlotAtItsOwnScreenPosition() {
    final int playerSlots =
        FletchingStationSlots.PLAYER_MAIN_COUNT + FletchingStationSlots.HOTBAR_COUNT;
    final Set<String> positions = new HashSet<>();
    for (int index = 0; index < playerSlots; index++) {
      positions.add(
          FletchingStationSlots.playerX(index) + "," + FletchingStationSlots.playerY(index));
    }

    assertEquals(playerSlots, positions.size());
  }

  @Test
  void fillsEachInputRowBeforeStartingTheNext() {
    assertEquals(FletchingStationSlots.inputY(0), FletchingStationSlots.inputY(2));
    assertTrue(FletchingStationSlots.inputY(3) > FletchingStationSlots.inputY(2));
    assertEquals(FletchingStationSlots.inputX(0), FletchingStationSlots.inputX(3));
  }

  @Test
  void putsTheHotbarOnItsOwnRowBelowTheMainInventory() {
    assertTrue(
        FletchingStationSlots.playerY(FletchingStationSlots.PLAYER_MAIN_COUNT)
            > FletchingStationSlots.playerY(FletchingStationSlots.PLAYER_MAIN_COUNT - 1));
  }

  @ParameterizedTest(name = "index {0}")
  @ValueSource(ints = {-1, 9})
  void refusesToPositionAnInputSlotThatDoesNotExist(final int index) {
    assertThrows(IllegalArgumentException.class, () -> FletchingStationSlots.inputX(index));
    assertThrows(IllegalArgumentException.class, () -> FletchingStationSlots.inputY(index));
  }

  @ParameterizedTest(name = "index {0}")
  @ValueSource(ints = {-1, 36})
  void refusesToPositionAPlayerSlotThatDoesNotExist(final int index) {
    assertThrows(IllegalArgumentException.class, () -> FletchingStationSlots.playerX(index));
    assertThrows(IllegalArgumentException.class, () -> FletchingStationSlots.playerY(index));
  }
}
