package com.grahambartley.morearrows.compat.emi;

import com.grahambartley.morearrows.compat.info.InfoEntry;
import com.grahambartley.morearrows.compat.info.RecipeViewerInfo;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class MoreArrowsEmiPlugin implements EmiPlugin {

  private final EmiInfoRecipes recipes;
  private final Supplier<List<InfoEntry>> entries;
  private final EmiStationRegistrar station;

  public MoreArrowsEmiPlugin() {
    this(EmiInfoRecipes.viaItemRegistry(), RecipeViewerInfo::arrowEntries, new EmiStationRecipes());
  }

  MoreArrowsEmiPlugin(
      final EmiInfoRecipes recipes,
      final Supplier<List<InfoEntry>> entries,
      final EmiStationRegistrar station) {
    this.recipes = Objects.requireNonNull(recipes, "recipes");
    this.entries = Objects.requireNonNull(entries, "entries");
    this.station = Objects.requireNonNull(station, "station");
  }

  @Override
  public void register(final EmiRegistry registry) {
    Objects.requireNonNull(registry, "registry");
    recipes.from(entries.get()).forEach(registry::addRecipe);
    station.register(registry);
  }
}
