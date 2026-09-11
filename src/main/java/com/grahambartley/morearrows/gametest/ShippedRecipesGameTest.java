package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.ModRecipes;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.recipe.FletchingIngredient;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.FletchingRecipeInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.Identifier;

public final class ShippedRecipesGameTest implements FabricGameTest {

  private static final String BATCH_ID = "shipped-recipes";
  private static final int TICK_LIMIT = 10;
  private static final int CRAFTING_TABLE_YIELD = 8;
  private static final int STATION_YIELD = 12;

  @CustomTestProvider
  public Collection<TestFunction> everyArrowKeepsItsCraftingTableRecipe() {
    return perArrow(
        "craftingtable", ShippedRecipesGameTest::craftingTableRecipeStillYieldsItsEightArrows);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyArrowHasAStationRecipe() {
    return perArrow("station", ShippedRecipesGameTest::stationRecipeYieldsItsTwelveArrows);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyStationRecipeBeatsTheCraftingTable() {
    return perArrow("rate", ShippedRecipesGameTest::stationBeatsTheCraftingTablePerIngredient);
  }

  private static void craftingTableRecipeStillYieldsItsEightArrows(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final RecipeManager recipes = recipeManager(context);
    final Identifier id = arrow.id();
    final ShapedRecipe recipe = craftingRecipe(context, recipes, id);
    final CraftingRecipeInput grid = gridOf(recipe);

    final Optional<RecipeEntry<CraftingRecipe>> matched =
        recipes.getFirstMatch(RecipeType.CRAFTING, grid, context.getWorld());

    context.assertTrue(
        matched.isPresent(), "A crafting table should still match the grid " + id + " declares");
    context.assertTrue(
        id.equals(matched.get().id()),
        "The grid " + id + " declares should still craft it, but matched " + matched.get().id());

    final ItemStack crafted = recipe.craft(grid, registries(context));

    context.assertTrue(
        crafted.isOf(arrow.item()),
        "The crafting table recipe " + id + " should still yield that arrow");
    context.assertEquals(
        crafted.getCount(),
        CRAFTING_TABLE_YIELD,
        "The crafting table recipe " + id + " should still yield " + CRAFTING_TABLE_YIELD);
    context.complete();
  }

  private static void stationRecipeYieldsItsTwelveArrows(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final RecipeManager recipes = recipeManager(context);
    final Identifier id = stationRecipeId(arrow);
    final FletchingRecipe recipe = stationRecipe(context, recipes, id);
    final FletchingRecipeInput inputs = stationInputsOf(recipe);

    final Optional<RecipeEntry<FletchingRecipe>> matched =
        recipes.getFirstMatch(ModRecipes.FLETCHING, inputs, context.getWorld());

    context.assertTrue(
        matched.isPresent(), "The station should match the ingredients " + id + " declares");
    context.assertTrue(
        id.equals(matched.get().id()),
        "The ingredients " + id + " declares should craft it, but matched " + matched.get().id());

    final ItemStack crafted = recipe.craft(inputs, registries(context));

    context.assertTrue(
        crafted.isOf(arrow.item()), "The station recipe " + id + " should yield that arrow");
    context.assertEquals(
        crafted.getCount(),
        STATION_YIELD,
        "The station recipe " + id + " should yield " + STATION_YIELD);
    context.complete();
  }

  private static void stationBeatsTheCraftingTablePerIngredient(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final RecipeManager recipes = recipeManager(context);
    final ShapedRecipe table = craftingRecipe(context, recipes, arrow.id());
    final FletchingRecipe station = stationRecipe(context, recipes, stationRecipeId(arrow));
    final ItemStack centre = oneOf(centreIngredientOf(table));

    final Optional<FletchingIngredient> stationCentre =
        station.inputs().stream().filter(input -> input.ingredient().test(centre)).findFirst();

    context.assertTrue(
        stationCentre.isPresent(),
        "The station recipe for " + arrow.id() + " should still ask for " + centre.getItem());

    final int tablePerIngredient = table.getResult(registries(context)).getCount();
    final int stationPerIngredient = station.result().getCount() / stationCentre.get().count();

    context.assertTrue(
        stationPerIngredient > tablePerIngredient,
        "The station should give more "
            + arrow.id()
            + " per "
            + centre.getItem()
            + " than the crafting table, but gave "
            + stationPerIngredient
            + " against "
            + tablePerIngredient);
    context.complete();
  }

  private static Collection<TestFunction> perArrow(
      final String behaviour, final BiConsumer<TestContext, RegisteredArrow<?>> body) {
    final List<TestFunction> functions = new ArrayList<>();
    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      functions.add(
          new TestFunction(
              BATCH_ID,
              "shippedrecipes." + behaviour + "." + arrow.id().getPath(),
              FabricGameTest.EMPTY_STRUCTURE,
              TICK_LIMIT,
              0L,
              true,
              context -> body.accept(context, arrow)));
    }
    return functions;
  }

  private static ShapedRecipe craftingRecipe(
      final TestContext context, final RecipeManager recipes, final Identifier id) {
    final Optional<RecipeEntry<?>> entry = recipes.get(id);

    context.assertTrue(entry.isPresent(), "The crafting table recipe " + id + " should be loaded");
    context.assertTrue(
        entry.get().value() instanceof ShapedRecipe,
        "The crafting table recipe " + id + " should still be a shaped recipe");
    return (ShapedRecipe) entry.get().value();
  }

  private static FletchingRecipe stationRecipe(
      final TestContext context, final RecipeManager recipes, final Identifier id) {
    final Optional<RecipeEntry<?>> entry = recipes.get(id);

    context.assertTrue(entry.isPresent(), "The station recipe " + id + " should be loaded");
    context.assertTrue(
        entry.get().value() instanceof FletchingRecipe,
        "The station recipe " + id + " should be a fletching recipe");
    return (FletchingRecipe) entry.get().value();
  }

  private static CraftingRecipeInput gridOf(final ShapedRecipe recipe) {
    return CraftingRecipeInput.create(
        recipe.getWidth(),
        recipe.getHeight(),
        recipe.getIngredients().stream().map(ShippedRecipesGameTest::oneOf).toList());
  }

  private static Ingredient centreIngredientOf(final ShapedRecipe recipe) {
    final int centre = recipe.getHeight() / 2 * recipe.getWidth() + recipe.getWidth() / 2;
    return recipe.getIngredients().get(centre);
  }

  private static FletchingRecipeInput stationInputsOf(final FletchingRecipe recipe) {
    return new FletchingRecipeInput(
        recipe.inputs().stream()
            .map(input -> oneOf(input.ingredient()).copyWithCount(input.count()))
            .toList());
  }

  private static ItemStack oneOf(final Ingredient ingredient) {
    final ItemStack[] matching = ingredient.getMatchingStacks();
    return matching.length == 0 ? ItemStack.EMPTY : matching[0].copyWithCount(1);
  }

  private static Identifier stationRecipeId(final RegisteredArrow<?> arrow) {
    return Identifier.of(arrow.id().getNamespace(), "fletching/" + arrow.id().getPath());
  }

  private static RegistryWrapper.WrapperLookup registries(final TestContext context) {
    return context.getWorld().getRegistryManager();
  }

  private static RecipeManager recipeManager(final TestContext context) {
    return context.getWorld().getServer().getRecipeManager();
  }
}
