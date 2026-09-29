package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import com.grahambartley.notenougharrows.recipe.FletchingRecipeSerializer;
import com.grahambartley.notenougharrows.social.CourierLoadingRecipe;
import com.grahambartley.notenougharrows.social.CourierUnloadingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModRecipes {
  public static final Identifier FLETCHING_ID = Identifier.of(NotEnoughArrows.MOD_ID, "fletching");

  public static final RecipeType<FletchingRecipe> FLETCHING =
      new RecipeType<>() {
        @Override
        public String toString() {
          return FLETCHING_ID.toString();
        }
      };

  public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER =
      new FletchingRecipeSerializer();

  public static final Identifier COURIER_LOADING_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "courier_loading");
  public static final Identifier COURIER_UNLOADING_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "courier_unloading");

  public static final RecipeSerializer<CourierLoadingRecipe> COURIER_LOADING_SERIALIZER =
      new SpecialRecipeSerializer<>(CourierLoadingRecipe::new);
  public static final RecipeSerializer<CourierUnloadingRecipe> COURIER_UNLOADING_SERIALIZER =
      new SpecialRecipeSerializer<>(CourierUnloadingRecipe::new);

  private ModRecipes() {}

  public static void register() {
    Registry.register(Registries.RECIPE_TYPE, FLETCHING_ID, FLETCHING);
    Registry.register(Registries.RECIPE_SERIALIZER, FLETCHING_ID, FLETCHING_SERIALIZER);
    Registry.register(Registries.RECIPE_SERIALIZER, COURIER_LOADING_ID, COURIER_LOADING_SERIALIZER);
    Registry.register(
        Registries.RECIPE_SERIALIZER, COURIER_UNLOADING_ID, COURIER_UNLOADING_SERIALIZER);
    NotEnoughArrows.LOGGER.info("Registered recipe type {}", FLETCHING_ID);
  }
}
