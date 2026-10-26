package com.grahambartley.volley.item;

import com.grahambartley.volley.ModArrows;
import com.grahambartley.volley.arrow.RegisteredArrow;
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
