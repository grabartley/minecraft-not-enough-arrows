package com.grahambartley.notenougharrows.compat.layout;

import com.grahambartley.notenougharrows.recipe.FletchingRecipe;

public final class FletchingRecipeLayout {

  public static final int SLOT = 18;
  public static final int COLUMNS = 3;
  public static final int ARROW_WIDTH = 24;
  public static final int ARROW_HEIGHT = 17;
  public static final int PADDING = 4;

  private final int inputCount;
  private final int offsetX;
  private final int offsetY;

  private FletchingRecipeLayout(final int inputCount, final int offsetX, final int offsetY) {
    this.inputCount = inputCount;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public static FletchingRecipeLayout sizedToFit(final int inputCount) {
    requireInputCount(inputCount);
    return new FletchingRecipeLayout(inputCount, 0, 0);
  }

  public static FletchingRecipeLayout centredIn(final int inputCount, final int boundsInputCount) {
    requireInputCount(inputCount);
    requireInputCount(boundsInputCount);
    if (inputCount > boundsInputCount) {
      throw new IllegalArgumentException(
          "A recipe of "
              + inputCount
              + " inputs cannot be centred in bounds sized for "
              + boundsInputCount);
    }
    return new FletchingRecipeLayout(
        inputCount,
        (width(boundsInputCount) - width(inputCount)) / 2,
        (height(boundsInputCount) - height(inputCount)) / 2);
  }

  public int inputX(final int index) {
    requireIndex(index);
    return offsetX + PADDING + index % COLUMNS * SLOT;
  }

  public int inputY(final int index) {
    requireIndex(index);
    return offsetY + top(rows(inputCount) * SLOT) + index / COLUMNS * SLOT;
  }

  public int arrowX() {
    return offsetX + PADDING + columns(inputCount) * SLOT + PADDING;
  }

  public int arrowY() {
    return offsetY + top(ARROW_HEIGHT);
  }

  public int outputX() {
    return arrowX() + ARROW_WIDTH + PADDING;
  }

  public int outputY() {
    return offsetY + top(SLOT);
  }

  public static int rows(final int inputCount) {
    requireInputCount(inputCount);
    return (inputCount + COLUMNS - 1) / COLUMNS;
  }

  public static int columns(final int inputCount) {
    requireInputCount(inputCount);
    return Math.min(inputCount, COLUMNS);
  }

  public static int width(final int inputCount) {
    return PADDING + columns(inputCount) * SLOT + PADDING + ARROW_WIDTH + PADDING + SLOT + PADDING;
  }

  public static int height(final int inputCount) {
    return PADDING + contentHeight(inputCount) + PADDING;
  }

  private static int contentHeight(final int inputCount) {
    return rows(inputCount) * SLOT;
  }

  private int top(final int elementHeight) {
    return PADDING + (contentHeight(inputCount) - elementHeight) / 2;
  }

  private void requireIndex(final int index) {
    if (index < 0 || index >= inputCount) {
      throw new IllegalArgumentException(
          "Input index must be between 0 and " + (inputCount - 1) + " but was " + index);
    }
  }

  private static void requireInputCount(final int inputCount) {
    if (inputCount < FletchingRecipe.MIN_INPUTS || inputCount > FletchingRecipe.MAX_INPUTS) {
      throw new IllegalArgumentException(
          "A station recipe must declare between "
              + FletchingRecipe.MIN_INPUTS
              + " and "
              + FletchingRecipe.MAX_INPUTS
              + " inputs but declared "
              + inputCount);
    }
  }
}
