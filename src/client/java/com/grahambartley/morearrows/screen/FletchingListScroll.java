package com.grahambartley.morearrows.screen;

import java.util.List;

public final class FletchingListScroll {
  private List<?> lastSeen = List.of();
  private float amount;
  private boolean dragging;

  public float amount(final int recipeCount) {
    return FletchingListGeometry.scrollable(recipeCount) ? amount : 0f;
  }

  public boolean dragging() {
    return dragging;
  }

  public boolean follow(final List<?> recipes) {
    if (recipes.equals(lastSeen)) {
      return false;
    }
    lastSeen = List.copyOf(recipes);
    amount = 0f;
    dragging = false;
    return true;
  }

  public boolean wheel(final int recipeCount, final double verticalAmount) {
    if (!FletchingListGeometry.scrollable(recipeCount)) {
      return false;
    }
    amount =
        FletchingListGeometry.amountAfterScroll(recipeCount, amount(recipeCount), verticalAmount);
    return true;
  }

  public boolean startDrag(final int recipeCount, final double mouseY, final int trackTop) {
    if (!FletchingListGeometry.scrollable(recipeCount)) {
      return false;
    }
    dragging = true;
    amount = FletchingListGeometry.amountFromDrag(mouseY, trackTop);
    return true;
  }

  public boolean drag(final int recipeCount, final double mouseY, final int trackTop) {
    if (!dragging || !FletchingListGeometry.scrollable(recipeCount)) {
      return false;
    }
    amount = FletchingListGeometry.amountFromDrag(mouseY, trackTop);
    return true;
  }

  public void endDrag() {
    dragging = false;
  }
}
