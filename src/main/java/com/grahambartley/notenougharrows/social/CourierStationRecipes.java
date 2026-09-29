package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.recipe.FletchingIngredient;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import com.grahambartley.notenougharrows.recipe.FletchingRecipeInput;
import java.util.List;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public final class CourierStationRecipes {
  public static final Identifier LOAD_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "courier_arrow/load");
  public static final Identifier UNLOAD_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "courier_arrow/unload");

  private static final String NO_GROUP = "";
  private static final int ONE_ARROW = 1;

  private CourierStationRecipes() {}

  public static List<RecipeEntry<FletchingRecipe>> matching(
      final FletchingRecipeInput input, final CourierArrowConfig config) {
    Objects.requireNonNull(input, "input");
    Objects.requireNonNull(config, "config");
    final List<ItemStack> occupied = input.stacks().stream().filter(it -> !it.isEmpty()).toList();
    if (occupied.size() == 1 && CourierPayloads.isLoaded(occupied.get(0))) {
      return List.of(unloading(occupied.get(0)));
    }
    return CourierLoad.of(input.stacks(), config).map(CourierStationRecipes::loading).stream()
        .toList();
  }

  public static boolean owns(final Identifier recipeId) {
    return LOAD_ID.equals(recipeId) || UNLOAD_ID.equals(recipeId);
  }

  public static List<ItemStack> handedBack(
      final RecipeEntry<FletchingRecipe> taken, final FletchingRecipeInput input) {
    if (!UNLOAD_ID.equals(taken.id())) {
      return List.of();
    }
    return input.stacks().stream()
        .filter(CourierPayloads::isLoaded)
        .findFirst()
        .map(arrow -> List.of(CourierPayloads.emptied(arrow)))
        .orElseGet(List::of);
  }

  private static RecipeEntry<FletchingRecipe> unloading(final ItemStack loaded) {
    return new RecipeEntry<>(
        UNLOAD_ID,
        new FletchingRecipe(
            NO_GROUP,
            List.of(new FletchingIngredient(Ingredient.ofItems(loaded.getItem()), ONE_ARROW)),
            CourierPayloads.payloadOf(loaded).orElseThrow()));
  }

  private static RecipeEntry<FletchingRecipe> loading(final CourierLoad load) {
    return new RecipeEntry<>(
        LOAD_ID,
        new FletchingRecipe(
            NO_GROUP,
            List.of(
                new FletchingIngredient(Ingredient.ofItems(load.arrow().getItem()), ONE_ARROW),
                new FletchingIngredient(
                    Ingredient.ofItems(load.payload().getItem()), load.count())),
            load.loaded()));
  }
}
