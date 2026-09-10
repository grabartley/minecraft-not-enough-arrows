package com.grahambartley.morearrows.fletching;

import com.grahambartley.morearrows.recipe.FletchingRecipe;

public final class FletchingStationSlots {
  public static final int INPUT_COLUMNS = 3;
  public static final int INPUT_ROWS = 3;
  public static final int INPUT_COUNT = FletchingRecipe.MAX_INPUTS;

  public static final int PLAYER_COLUMNS = 9;
  public static final int PLAYER_MAIN_ROWS = 3;
  public static final int PLAYER_MAIN_COUNT = PLAYER_COLUMNS * PLAYER_MAIN_ROWS;
  public static final int HOTBAR_COUNT = PLAYER_COLUMNS;

  public static final int FIRST_INPUT_SLOT = 0;
  public static final int RESULT_SLOT = FIRST_INPUT_SLOT + INPUT_COUNT;
  public static final int FIRST_PLAYER_SLOT = RESULT_SLOT + 1;
  public static final int FIRST_HOTBAR_SLOT = FIRST_PLAYER_SLOT + PLAYER_MAIN_COUNT;
  public static final int TOTAL_SLOTS = FIRST_HOTBAR_SLOT + HOTBAR_COUNT;

  public static final int SLOT_PITCH = 18;
  public static final int INPUT_ORIGIN_X = 30;
  public static final int INPUT_ORIGIN_Y = 17;
  public static final int RESULT_X = 124;
  public static final int RESULT_Y = 35;
  public static final int PLAYER_ORIGIN_X = 8;
  public static final int PLAYER_ORIGIN_Y = 84;
  public static final int HOTBAR_Y = 142;

  private FletchingStationSlots() {}

  public static boolean isInput(final int slot) {
    return slot >= FIRST_INPUT_SLOT && slot < RESULT_SLOT;
  }

  public static boolean isResult(final int slot) {
    return slot == RESULT_SLOT;
  }

  public static boolean isPlayerInventory(final int slot) {
    return slot >= FIRST_PLAYER_SLOT && slot < TOTAL_SLOTS;
  }

  public static int inputX(final int index) {
    requireInputIndex(index);
    return INPUT_ORIGIN_X + (index % INPUT_COLUMNS) * SLOT_PITCH;
  }

  public static int inputY(final int index) {
    requireInputIndex(index);
    return INPUT_ORIGIN_Y + (index / INPUT_COLUMNS) * SLOT_PITCH;
  }

  public static int playerX(final int index) {
    requirePlayerIndex(index);
    return PLAYER_ORIGIN_X + (index % PLAYER_COLUMNS) * SLOT_PITCH;
  }

  public static int playerY(final int index) {
    requirePlayerIndex(index);
    return index < PLAYER_MAIN_COUNT
        ? PLAYER_ORIGIN_Y + (index / PLAYER_COLUMNS) * SLOT_PITCH
        : HOTBAR_Y;
  }

  private static void requireInputIndex(final int index) {
    if (index < 0 || index >= INPUT_COUNT) {
      throw new IllegalArgumentException(
          "Input index must be between 0 and " + (INPUT_COUNT - 1) + " but was " + index);
    }
  }

  private static void requirePlayerIndex(final int index) {
    if (index < 0 || index >= PLAYER_MAIN_COUNT + HOTBAR_COUNT) {
      throw new IllegalArgumentException(
          "Player inventory index must be between 0 and "
              + (PLAYER_MAIN_COUNT + HOTBAR_COUNT - 1)
              + " but was "
              + index);
    }
  }
}
