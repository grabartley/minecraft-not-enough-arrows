package com.grahambartley.morearrows;

import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.FletchingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModRecipes {
  public static final Identifier FLETCHING_ID = Identifier.of(MoreArrows.MOD_ID, "fletching");

  public static final RecipeType<FletchingRecipe> FLETCHING =
      new RecipeType<>() {
        @Override
        public String toString() {
          return FLETCHING_ID.toString();
        }
      };

  public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER =
      new FletchingRecipeSerializer();

  private ModRecipes() {}

  public static void register() {
    Registry.register(Registries.RECIPE_TYPE, FLETCHING_ID, FLETCHING);
    Registry.register(Registries.RECIPE_SERIALIZER, FLETCHING_ID, FLETCHING_SERIALIZER);
    MoreArrows.LOGGER.info("Registered recipe type {}", FLETCHING_ID);
  }
}
