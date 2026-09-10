package com.grahambartley.morearrows.fletching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FletchingStationLayoutTest {

  @Test
  void placesEveryInputSlotAtItsOwnScreenPosition() {
    final Set<String> positions = new HashSet<>();
    for (int index = 0; index < FletchingStationSlots.INPUT_COUNT; index++) {
      positions.add(
          FletchingStationLayout.inputX(index) + "," + FletchingStationLayout.inputY(index));
    }

    assertEquals(FletchingStationSlots.INPUT_COUNT, positions.size());
  }

  @Test
  void placesEveryPlayerSlotAtItsOwnScreenPosition() {
    final Set<String> positions = new HashSet<>();
    for (int index = 0; index < FletchingStationSlots.PLAYER_SLOT_COUNT; index++) {
      positions.add(
          FletchingStationLayout.playerX(index) + "," + FletchingStationLayout.playerY(index));
    }

    assertEquals(FletchingStationSlots.PLAYER_SLOT_COUNT, positions.size());
  }

  @Test
  void fillsEachInputRowBeforeStartingTheNext() {
    assertEquals(FletchingStationLayout.inputY(0), FletchingStationLayout.inputY(2));
    assertTrue(FletchingStationLayout.inputY(3) > FletchingStationLayout.inputY(2));
    assertEquals(FletchingStationLayout.inputX(0), FletchingStationLayout.inputX(3));
  }

  @Test
  void wrapsTheInputGridAtTheDeclaredColumnCount() {
    assertEquals(
        FletchingStationLayout.INPUT_ORIGIN_X
            + (FletchingStationLayout.INPUT_COLUMNS - 1) * FletchingStationLayout.SLOT_PITCH,
        FletchingStationLayout.inputX(FletchingStationLayout.INPUT_COLUMNS - 1));
    assertEquals(
        FletchingStationLayout.INPUT_ORIGIN_X,
        FletchingStationLayout.inputX(FletchingStationLayout.INPUT_COLUMNS));
  }

  @Test
  void putsTheHotbarOnItsOwnRowBelowTheMainInventory() {
    assertEquals(
        FletchingStationLayout.HOTBAR_Y,
        FletchingStationLayout.playerY(FletchingStationSlots.PLAYER_MAIN_COUNT));
    assertTrue(
        FletchingStationLayout.playerY(FletchingStationSlots.PLAYER_MAIN_COUNT)
            > FletchingStationLayout.playerY(FletchingStationSlots.PLAYER_MAIN_COUNT - 1));
  }

  @ParameterizedTest(name = "index {0}")
  @ValueSource(ints = {-1, 9})
  void refusesToPositionAnInputSlotThatDoesNotExist(final int index) {
    assertThrows(IllegalArgumentException.class, () -> FletchingStationLayout.inputX(index));
    assertThrows(IllegalArgumentException.class, () -> FletchingStationLayout.inputY(index));
  }

  @ParameterizedTest(name = "index {0}")
  @ValueSource(ints = {-1, 36})
  void refusesToPositionAPlayerSlotThatDoesNotExist(final int index) {
    assertThrows(IllegalArgumentException.class, () -> FletchingStationLayout.playerX(index));
    assertThrows(IllegalArgumentException.class, () -> FletchingStationLayout.playerY(index));
  }
}
