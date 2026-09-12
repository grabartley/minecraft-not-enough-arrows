package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.recipe.FletchingIngredient;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

final class FletchingTestSupport {
  static final int TICK_LIMIT = 10;
  static final String TEMPLATE = "not-enough-arrows:fire_pad";

  static final Identifier TEST_RECIPE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "gametest_fletching");
  static final int ARROWS_CONSUMED = 4;
  static final int TNT_CONSUMED = 1;
  static final int ARROWS_PRODUCED = 8;

  private FletchingTestSupport() {}

  static FletchingRecipe fourArrowsAndOneTntGiveEightArrows() {
    return new FletchingRecipe(
        "",
        List.of(
            new FletchingIngredient(Ingredient.ofItems(Items.ARROW), ARROWS_CONSUMED),
            new FletchingIngredient(Ingredient.ofItems(Items.TNT), TNT_CONSUMED)),
        new ItemStack(Items.ARROW, ARROWS_PRODUCED));
  }

  static void installTestRecipe(final ServerWorld world) {
    final RecipeManager recipes = recipeManager(world);
    final List<RecipeEntry<?>> installed = new ArrayList<>(recipes.values());
    installed.removeIf(entry -> entry.id().equals(TEST_RECIPE_ID));
    installed.add(new RecipeEntry<>(TEST_RECIPE_ID, fourArrowsAndOneTntGiveEightArrows()));
    recipes.setRecipes(installed);
  }

  static void removeTestRecipe(final ServerWorld world) {
    final RecipeManager recipes = recipeManager(world);
    recipes.setRecipes(
        recipes.values().stream().filter(entry -> !entry.id().equals(TEST_RECIPE_ID)).toList());
  }

  private static RecipeManager recipeManager(final ServerWorld world) {
    return world.getServer().getRecipeManager();
  }
}
