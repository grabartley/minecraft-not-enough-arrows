package com.grahambartley.morearrows.fletching;

import com.grahambartley.morearrows.recipe.FletchingRecipe;

public final class FletchingStationSlots {
  public static final int INPUT_COLUMNS = 3;
  public static final int INPUT_COUNT = FletchingRecipe.MAX_INPUTS;

  public static final int PLAYER_COLUMNS = 9;
  public static final int PLAYER_MAIN_COUNT = 27;
  public static final int HOTBAR_COUNT = PLAYER_COLUMNS;
  public static final int PLAYER_SLOT_COUNT = PLAYER_MAIN_COUNT + HOTBAR_COUNT;

  public static final int FIRST_INPUT_SLOT = 0;
  public static final int RESULT_SLOT = FIRST_INPUT_SLOT + INPUT_COUNT;
  public static final int FIRST_PLAYER_SLOT = RESULT_SLOT + 1;
  public static final int FIRST_HOTBAR_SLOT = FIRST_PLAYER_SLOT + PLAYER_MAIN_COUNT;
  public static final int TOTAL_SLOTS = FIRST_PLAYER_SLOT + PLAYER_SLOT_COUNT;

  private FletchingStationSlots() {}

  public static boolean isInput(final int slot) {
    return slot >= FIRST_INPUT_SLOT && slot < RESULT_SLOT;
  }

  public static boolean isResult(final int slot) {
    return slot == RESULT_SLOT;
  }
}
