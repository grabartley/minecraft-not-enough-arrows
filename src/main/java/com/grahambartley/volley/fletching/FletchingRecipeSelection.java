package com.grahambartley.volley.fletching;

import java.util.List;
import net.minecraft.util.Identifier;

public final class FletchingRecipeSelection {
  public static final int NO_SELECTION = -1;

  private FletchingRecipeSelection() {}

  public static int reselect(final List<Identifier> available, final Identifier previous) {
    if (available.size() == 1) {
      return 0;
    }
    final int kept = available.indexOf(previous);
    return kept < 0 ? NO_SELECTION : kept;
  }
}
