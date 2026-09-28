package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import com.grahambartley.notenougharrows.recipe.FletchingRecipeInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import net.minecraft.registry.Registries;
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
        "notenougharrows.craftingtablerecipeyieldseight",
        ShippedRecipesGameTest::assertCraftingTableRecipeYieldsEight);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyArrowHasAStationRecipe() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "notenougharrows.stationrecipeyieldstwelve",
        ShippedRecipesGameTest::assertStationRecipeYieldsTwelve);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyStationRecipeCostsTheSameAndYieldsMore() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "notenougharrows.stationcoststhesameandyieldsmore",
        ShippedRecipesGameTest::assertStationCostsTheSameAndYieldsMore);
  }

  private static void assertCraftingTableRecipeYieldsEight(
      final TestContext context, final RegisteredArrow<?> arrow) {
    for (final RecipeCase recipeCase : RecipeCase.of(arrow)) {
      assertCraftingTableRecipeYieldsEight(context, recipeCase);
    }
    context.complete();
  }

  private static void assertCraftingTableRecipeYieldsEight(
      final TestContext context, final RecipeCase recipeCase) {
    final RecipeManager recipes = recipeManager(context);
    final Identifier id = recipeCase.tableId();
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
        ItemStack.areItemsAndComponentsEqual(crafted, recipeCase.result()),
        "The crafting table recipe "
            + id
            + " should yield "
            + recipeCase.result()
            + " but yielded "
            + crafted
            + " "
            + crafted.getComponentChanges());
    context.assertEquals(
        crafted.getCount(),
        CRAFTING_TABLE_YIELD,
        "The crafting table recipe " + id + " should still yield " + CRAFTING_TABLE_YIELD);
    recipeCase
        .centre()
        .ifPresent(
            centre ->
                context.assertTrue(
                    recipe.getIngredients().stream()
                        .anyMatch(ingredient -> ingredient.test(new ItemStack(centre))),
                    "The crafting table recipe " + id + " should be built around " + centre));
  }

  private static void assertStationRecipeYieldsTwelve(
      final TestContext context, final RegisteredArrow<?> arrow) {
    for (final RecipeCase recipeCase : RecipeCase.of(arrow)) {
      assertStationRecipeYieldsTwelve(context, recipeCase);
    }
    context.complete();
  }

  private static void assertStationRecipeYieldsTwelve(
      final TestContext context, final RecipeCase recipeCase) {
    final RecipeManager recipes = recipeManager(context);
    final Identifier id = recipeCase.stationId();
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
        ItemStack.areItemsAndComponentsEqual(crafted, recipeCase.result()),
        "The station recipe "
            + id
            + " should yield "
            + recipeCase.result()
            + " but yielded "
            + crafted
            + " "
            + crafted.getComponentChanges());
    context.assertEquals(
        crafted.getCount(),
        STATION_YIELD,
        "The station recipe " + id + " should yield " + STATION_YIELD);
  }

  private static void assertStationCostsTheSameAndYieldsMore(
      final TestContext context, final RegisteredArrow<?> arrow) {
    for (final RecipeCase recipeCase : RecipeCase.of(arrow)) {
      assertStationCostsTheSameAndYieldsMore(context, recipeCase);
    }
    context.complete();
  }

  private static void assertStationCostsTheSameAndYieldsMore(
      final TestContext context, final RecipeCase recipeCase) {
    final RecipeManager recipes = recipeManager(context);
    final ShapedRecipe table = craftingRecipe(context, recipes, recipeCase.tableId());
    final FletchingRecipe station = stationRecipe(context, recipes, recipeCase.stationId());
    final List<Cost> tableCost = costOf(table);
    final List<Cost> stationCost = costOf(station);
    final int tableYield = table.getResult(registries(context)).getCount();
    final int stationYield = station.result().getCount();

    context.assertTrue(
        tableCost.equals(stationCost),
        "The station should ask for exactly what the crafting table asks for to make "
            + recipeCase.tableId()
            + ", but asks for "
            + describe(stationCost)
            + " against "
            + describe(tableCost));
    context.assertTrue(
        stationYield > tableYield,
        "The station should yield more "
            + recipeCase.tableId()
            + " than the crafting table for the same cost, but yielded "
            + stationYield
            + " against "
            + tableYield);
  }

  private record RecipeCase(
      Identifier tableId, Identifier stationId, ItemStack result, Optional<Item> centre) {

    static List<RecipeCase> of(final RegisteredArrow<?> arrow) {
      final Identifier id = arrow.id();
      if (!(arrow.item() instanceof TintedArrowItem tinted)) {
        return List.of(
            new RecipeCase(id, stationIdOf(id), new ItemStack(arrow.item()), Optional.empty()));
      }
      return tinted.palette().choices().stream()
          .map(
              choice -> {
                final Identifier tableId = id.withSuffixedPath("/" + choice.key());
                return new RecipeCase(
                    tableId,
                    stationIdOf(tableId),
                    tinted.stackOf(choice),
                    Optional.of(Registries.ITEM.get(choice.ingredient())));
              })
          .toList();
    }

    private static Identifier stationIdOf(final Identifier tableId) {
      return tableId.withPrefixedPath("fletching/");
    }
  }

  private static List<Cost> costOf(final ShapedRecipe recipe) {
    return tally(
        recipe.getIngredients().stream()
            .filter(ingredient -> !ingredient.isEmpty())
            .map(ingredient -> new Cost(ingredient, 1))
            .toList());
  }

  private static List<Cost> costOf(final FletchingRecipe recipe) {
    return tally(
        recipe.inputs().stream()
            .map(input -> new Cost(input.ingredient(), input.count()))
            .toList());
  }

  private static List<Cost> tally(final List<Cost> entries) {
    final List<Cost> tallied = new ArrayList<>();
    for (final Cost entry : entries) {
      final int existing = indexOfIngredient(tallied, entry.ingredient());
      if (existing < 0) {
        tallied.add(entry);
      } else {
        tallied.set(
            existing, new Cost(entry.ingredient(), tallied.get(existing).count() + entry.count()));
      }
    }
    tallied.sort(Comparator.comparing(Cost::toString));
    return tallied;
  }

  private static int indexOfIngredient(final List<Cost> entries, final Ingredient ingredient) {
    for (int index = 0; index < entries.size(); index++) {
      if (entries.get(index).ingredient().equals(ingredient)) {
        return index;
      }
    }
    return -1;
  }

  private static String describe(final List<Cost> cost) {
    return cost.stream().map(Cost::toString).collect(Collectors.joining(", "));
  }

  private record Cost(Ingredient ingredient, int count) {
    @Override
    public String toString() {
      return count
          + " x "
          + Arrays.stream(ingredient.getMatchingStacks())
              .map(stack -> stack.getItem().toString())
              .collect(Collectors.joining("|"));
    }
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

  private static RegistryWrapper.WrapperLookup registries(final TestContext context) {
    return context.getWorld().getRegistryManager();
  }

  private static RecipeManager recipeManager(final TestContext context) {
    return context.getWorld().getServer().getRecipeManager();
  }
}
