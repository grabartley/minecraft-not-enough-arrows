package com.grahambartley.morearrows.recipe;

import com.grahambartley.morearrows.ModRecipes;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;

public final class StationRecipes {

  private StationRecipes() {}

  public static List<RecipeEntry<FletchingRecipe>> from(final RecipeManager recipes) {
    Objects.requireNonNull(recipes, "recipes");
    return recipes.listAllOfType(ModRecipes.FLETCHING).stream()
        .sorted(Comparator.comparing(entry -> entry.id().toString()))
        .toList();
  }
}
