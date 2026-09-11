package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModRecipes;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.recipe.FletchingIngredient;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.FletchingRecipeInput;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
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

  private static final String BATCH = "shipped-recipes";
  private static final int CRAFTING_TABLE_YIELD = 8;
  private static final int STATION_YIELD = 12;

  @CustomTestProvider
  public Collection<TestFunction> everyArrowKeepsItsCraftingTableRecipe() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.craftingtablerecipeyieldseight",
        ShippedRecipesGameTest::assertCraftingTableRecipeYieldsEight);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyArrowHasAStationRecipe() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.stationrecipeyieldstwelve",
        ShippedRecipesGameTest::assertStationRecipeYieldsTwelve);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyStationRecipeCostsTheSameAndYieldsMore() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.stationcoststhesameandyieldsmore",
        ShippedRecipesGameTest::assertStationCostsTheSameAndYieldsMore);
  }

  private static void assertCraftingTableRecipeYieldsEight(
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

  private static void assertStationRecipeYieldsTwelve(
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

  private static void assertStationCostsTheSameAndYieldsMore(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final RecipeManager recipes = recipeManager(context);
    final ShapedRecipe table = craftingRecipe(context, recipes, arrow.id());
    final FletchingRecipe station = stationRecipe(context, recipes, stationRecipeId(arrow));
    final Map<Item, Integer> tableCost = costOf(table);
    final Map<Item, Integer> stationCost = costOf(station);
    final int tableYield = table.getResult(registries(context)).getCount();
    final int stationYield = station.result().getCount();

    context.assertTrue(
        tableCost.equals(stationCost),
        "The station should ask for exactly what the crafting table asks for to make "
            + arrow.id()
            + ", but asks for "
            + describe(stationCost)
            + " against "
            + describe(tableCost));
    context.assertTrue(
        stationYield > tableYield,
        "The station should yield more "
            + arrow.id()
            + " than the crafting table for the same cost, but yielded "
            + stationYield
            + " against "
            + tableYield);
    context.complete();
  }

  private static Map<Item, Integer> costOf(final ShapedRecipe recipe) {
    final Map<Item, Integer> cost = new LinkedHashMap<>();
    for (final Ingredient ingredient : recipe.getIngredients()) {
      if (!ingredient.isEmpty()) {
        cost.merge(oneOf(ingredient).getItem(), 1, Integer::sum);
      }
    }
    return cost;
  }

  private static Map<Item, Integer> costOf(final FletchingRecipe recipe) {
    final Map<Item, Integer> cost = new LinkedHashMap<>();
    for (final FletchingIngredient input : recipe.inputs()) {
      cost.merge(oneOf(input.ingredient()).getItem(), input.count(), Integer::sum);
    }
    return cost;
  }

  private static String describe(final Map<Item, Integer> cost) {
    return cost.entrySet().stream()
        .map(entry -> entry.getValue() + " x " + entry.getKey())
        .reduce((left, right) -> left + ", " + right)
        .orElse("nothing");
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
        recipe.getIngredients().stream()
            .map(ingredient -> ingredient.isEmpty() ? ItemStack.EMPTY : oneOf(ingredient))
            .toList());
  }

  private static FletchingRecipeInput stationInputsOf(final FletchingRecipe recipe) {
    return new FletchingRecipeInput(
        recipe.inputs().stream()
            .map(input -> oneOf(input.ingredient()).copyWithCount(input.count()))
            .toList());
  }

  private static ItemStack oneOf(final Ingredient ingredient) {
    return ingredient.getMatchingStacks()[0].copyWithCount(1);
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
