package com.grahambartley.notenougharrows.fletching;

public final class FletchingStationLayout {
  public static final int INPUT_COLUMNS = 3;
  public static final int PLAYER_COLUMNS = 9;

  public static final int SLOT_PITCH = 18;
  public static final int INPUT_ORIGIN_X = 30;
  public static final int INPUT_ORIGIN_Y = 17;
  public static final int RESULT_X = 124;
  public static final int RESULT_Y = 35;
  public static final int PLAYER_ORIGIN_X = 8;
  public static final int PLAYER_ORIGIN_Y = 84;
  public static final int HOTBAR_Y = 142;

  private FletchingStationLayout() {}

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
    return index < FletchingStationSlots.PLAYER_MAIN_COUNT
        ? PLAYER_ORIGIN_Y + (index / PLAYER_COLUMNS) * SLOT_PITCH
        : HOTBAR_Y;
  }

  private static void requireInputIndex(final int index) {
    if (index < 0 || index >= FletchingStationSlots.INPUT_COUNT) {
      throw new IllegalArgumentException(
          "Input index must be between 0 and "
              + (FletchingStationSlots.INPUT_COUNT - 1)
              + " but was "
              + index);
    }
  }

  private static void requirePlayerIndex(final int index) {
    if (index < 0 || index >= FletchingStationSlots.PLAYER_SLOT_COUNT) {
      throw new IllegalArgumentException(
          "Player inventory index must be between 0 and "
              + (FletchingStationSlots.PLAYER_SLOT_COUNT - 1)
              + " but was "
              + index);
    }
  }
}
