package com.grahambartley.morearrows.compat.info;

public final class FletchingRecipeLayout {

  public static final int SLOT = 18;
  public static final int COLUMNS = 3;
  public static final int ARROW_WIDTH = 24;
  public static final int ARROW_HEIGHT = 17;
  public static final int PADDING = 4;

  private FletchingRecipeLayout() {}

  public static int rows(final int inputCount) {
    requirePositive(inputCount);
    return (inputCount + COLUMNS - 1) / COLUMNS;
  }

  public static int columns(final int inputCount) {
    requirePositive(inputCount);
    return Math.min(inputCount, COLUMNS);
  }

  public static int inputX(final int index) {
    requireIndex(index);
    return PADDING + index % COLUMNS * SLOT;
  }

  public static int inputY(final int index, final int inputCount) {
    requireIndex(index);
    return topOf(rows(inputCount) * SLOT, inputCount) + index / COLUMNS * SLOT;
  }

  public static int arrowX(final int inputCount) {
    return PADDING + columns(inputCount) * SLOT + PADDING;
  }

  public static int arrowY(final int inputCount) {
    return topOf(ARROW_HEIGHT, inputCount);
  }

  public static int outputX(final int inputCount) {
    return arrowX(inputCount) + ARROW_WIDTH + PADDING;
  }

  public static int outputY(final int inputCount) {
    return topOf(SLOT, inputCount);
  }

  public static int width(final int inputCount) {
    return outputX(inputCount) + SLOT + PADDING;
  }

  public static int height(final int inputCount) {
    return PADDING + contentHeight(inputCount) + PADDING;
  }

  private static int contentHeight(final int inputCount) {
    return Math.max(rows(inputCount) * SLOT, SLOT);
  }

  private static int topOf(final int elementHeight, final int inputCount) {
    return PADDING + (contentHeight(inputCount) - elementHeight) / 2;
  }

  private static void requirePositive(final int inputCount) {
    if (inputCount < 1) {
      throw new IllegalArgumentException(
          "A station recipe must declare at least one input but declared " + inputCount);
    }
  }

  private static void requireIndex(final int index) {
    if (index < 0) {
      throw new IllegalArgumentException("Input index must not be negative but was " + index);
    }
  }
}
