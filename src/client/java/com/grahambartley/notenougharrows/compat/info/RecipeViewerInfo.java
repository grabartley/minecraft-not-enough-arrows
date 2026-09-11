package com.grahambartley.notenougharrows.compat.info;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class RecipeViewerInfo {

  private RecipeViewerInfo() {}

  public static List<InfoEntry> arrowEntries() {
    return entries(ModArrows.registered().stream().map(RegisteredArrow::id).toList());
  }

  public static List<InfoEntry> entries(final Collection<Identifier> itemIds) {
    Objects.requireNonNull(itemIds, "itemIds");

    final Set<Identifier> unique = new LinkedHashSet<>();
    for (final Identifier itemId : itemIds) {
      if (!unique.add(Objects.requireNonNull(itemId, "itemId"))) {
        throw new IllegalArgumentException("Item '" + itemId + "' already has an info entry");
      }
    }
    return unique.stream().map(RecipeViewerInfo::entry).toList();
  }

  private static InfoEntry entry(final Identifier itemId) {
    return InfoEntry.of(itemId, InfoKeys.description(itemId), InfoKeys.FIRING_KEY);
  }
}
