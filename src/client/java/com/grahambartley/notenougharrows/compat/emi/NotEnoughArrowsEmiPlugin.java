package com.grahambartley.notenougharrows.compat.emi;

import com.grahambartley.notenougharrows.compat.info.InfoEntry;
import com.grahambartley.notenougharrows.compat.info.RecipeViewerInfo;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class NotEnoughArrowsEmiPlugin implements EmiPlugin {

  private final EmiInfoRecipes recipes;
  private final Supplier<List<InfoEntry>> entries;
  private final EmiStationRegistrar station;
  private final EmiVariantComparisons variants;

  public NotEnoughArrowsEmiPlugin() {
    this(
        EmiInfoRecipes.viaItemRegistry(),
        RecipeViewerInfo::arrowEntries,
        new EmiStationRecipes(),
        EmiVariantComparisons.forTintedArrows());
  }

  NotEnoughArrowsEmiPlugin(
      final EmiInfoRecipes recipes,
      final Supplier<List<InfoEntry>> entries,
      final EmiStationRegistrar station,
      final EmiVariantComparisons variants) {
    this.recipes = Objects.requireNonNull(recipes, "recipes");
    this.entries = Objects.requireNonNull(entries, "entries");
    this.station = Objects.requireNonNull(station, "station");
    this.variants = Objects.requireNonNull(variants, "variants");
  }

  @Override
  public void register(final EmiRegistry registry) {
    Objects.requireNonNull(registry, "registry");
    variants.register(registry);
    recipes.from(entries.get()).forEach(registry::addRecipe);
    station.register(registry);
  }
}
