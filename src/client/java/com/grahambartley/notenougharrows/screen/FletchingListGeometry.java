package com.grahambartley.notenougharrows.screen;

public final class FletchingListGeometry {
  public static final int NO_ROW = -1;

  public static final int VISIBLE_ROWS = 3;
  public static final int ROW_WIDTH = 16;
  public static final int ROW_HEIGHT = 18;

  public static final int LIST_X = 87;
  public static final int LIST_Y = 16;

  public static final int TRACK_X = 107;
  public static final int TRACK_Y = 16;
  public static final int TRACK_WIDTH = 12;
  public static final int TRACK_HEIGHT = 54;

  public static final int SCROLLER_WIDTH = 12;
  public static final int SCROLLER_HEIGHT = 15;
  public static final int SCROLL_TRAVEL = TRACK_HEIGHT - SCROLLER_HEIGHT;

  private FletchingListGeometry() {}

  public static int hiddenRows(final int recipeCount) {
    return Math.max(0, recipeCount - VISIBLE_ROWS);
  }

  public static boolean scrollable(final int recipeCount) {
    return hiddenRows(recipeCount) > 0;
  }

  public static int visibleRows(final int recipeCount) {
    return Math.min(VISIBLE_ROWS, Math.max(0, recipeCount));
  }

  public static float clampAmount(final float amount) {
    if (Float.isNaN(amount) || amount < 0f) {
      return 0f;
    }
    return Math.min(amount, 1f);
  }

  public static int topRow(final int recipeCount, final float amount) {
    return (int) (clampAmount(amount) * hiddenRows(recipeCount) + 0.5f);
  }

  public static float amountAfterScroll(
      final int recipeCount, final float amount, final double verticalAmount) {
    final int hidden = hiddenRows(recipeCount);
    if (hidden == 0) {
      return 0f;
    }
    return clampAmount(amount - (float) (verticalAmount / hidden));
  }

  public static float amountFromDrag(final double mouseY, final int trackTop) {
    return clampAmount((float) ((mouseY - trackTop - SCROLLER_HEIGHT / 2.0) / SCROLL_TRAVEL));
  }

  public static int scrollerOffsetY(final float amount) {
    return (int) (clampAmount(amount) * SCROLL_TRAVEL);
  }

  public static boolean withinTrack(
      final double offsetX, final double offsetY, final int recipeCount) {
    return scrollable(recipeCount)
        && offsetX >= 0
        && offsetX < TRACK_WIDTH
        && offsetY >= 0
        && offsetY < TRACK_HEIGHT;
  }

  public static int rowAtOffset(
      final int recipeCount, final float amount, final double offsetX, final double offsetY) {
    if (offsetX < 0 || offsetX >= ROW_WIDTH || offsetY < 0) {
      return NO_ROW;
    }

    final int row = (int) (offsetY / ROW_HEIGHT);
    if (row >= visibleRows(recipeCount)) {
      return NO_ROW;
    }

    final int index = topRow(recipeCount, amount) + row;
    return index < recipeCount ? index : NO_ROW;
  }
}
