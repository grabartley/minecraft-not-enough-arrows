package com.grahambartley.notenougharrows.compat.info;

import java.util.List;
import java.util.Objects;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record InfoEntry(List<Identifier> itemIds, List<String> translationKeys) {

  public InfoEntry {
    Objects.requireNonNull(itemIds, "itemIds");
    Objects.requireNonNull(translationKeys, "translationKeys");

    itemIds = List.copyOf(itemIds);
    translationKeys = List.copyOf(translationKeys);

    if (itemIds.isEmpty()) {
      throw new IllegalArgumentException("Info entry must describe at least one item");
    }
    if (translationKeys.isEmpty()) {
      throw new IllegalArgumentException(
          "Info entry for " + itemIds + " must carry at least one translation key");
    }
    if (translationKeys.stream().anyMatch(String::isBlank)) {
      throw new IllegalArgumentException(
          "Info entry for " + itemIds + " must not carry a blank translation key");
    }
  }

  public static InfoEntry of(final Identifier itemId, final String... translationKeys) {
    return new InfoEntry(List.of(itemId), List.of(translationKeys));
  }

  public List<Text> texts() {
    return translationKeys.stream().<Text>map(Text::translatable).toList();
  }
}
