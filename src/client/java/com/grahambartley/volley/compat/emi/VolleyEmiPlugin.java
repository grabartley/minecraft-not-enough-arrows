package com.grahambartley.volley.compat.emi;

import com.grahambartley.volley.compat.info.InfoEntry;
import com.grahambartley.volley.compat.info.RecipeViewerInfo;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class VolleyEmiPlugin implements EmiPlugin {

  private final EmiInfoRecipes recipes;
  private final Supplier<List<InfoEntry>> entries;
  private final EmiStationRegistrar station;
  private final EmiVariantComparisons variants;

  public VolleyEmiPlugin() {
    this(
        EmiInfoRecipes.viaItemRegistry(),
        RecipeViewerInfo::arrowEntries,
        new EmiStationRecipes(),
        EmiVariantComparisons.forTintedArrows());
  }

  VolleyEmiPlugin(
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
