package com.grahambartley.notenougharrows.item;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;

public final class TintedArrowItems {

  private TintedArrowItems() {}

  public static List<TintedArrowItem> registered() {
    return ModArrows.registered().stream()
        .map(RegisteredArrow::item)
        .filter(TintedArrowItem.class::isInstance)
        .map(TintedArrowItem.class::cast)
        .toList();
  }
}
