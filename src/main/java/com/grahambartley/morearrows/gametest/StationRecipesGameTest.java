package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.StationRecipes;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class StationRecipesGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void listsEveryStationRecipeTheModShips(TestContext context) {
    final List<RecipeEntry<FletchingRecipe>> listed = StationRecipes.from(recipes(context));

    context.assertEquals(
        listed.size(),
        ModArrows.registered().size(),
        "Every arrow the mod registers should surface one station recipe");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void listsEveryArrowsOwnStationRecipe(TestContext context) {
    final List<Identifier> listed =
        StationRecipes.from(recipes(context)).stream().map(RecipeEntry::id).toList();

    ModArrows.registered()
        .forEach(
            arrow -> {
              final Identifier expected =
                  Identifier.of(arrow.id().getNamespace(), "fletching/" + arrow.id().getPath());
              context.assertTrue(
                  listed.contains(expected),
                  "The station recipe list should carry " + expected + " but held " + listed);
            });
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void ordersRecipesTheSameWayForEveryViewerThatAsks(TestContext context) {
    final RecipeManager recipes = recipes(context);
    final List<Identifier> first =
        StationRecipes.from(recipes).stream().map(RecipeEntry::id).toList();
    final List<Identifier> second =
        StationRecipes.from(recipes).stream().map(RecipeEntry::id).toList();
    final List<Identifier> sorted =
        first.stream().sorted(java.util.Comparator.comparing(Identifier::toString)).toList();

    context.assertTrue(
        first.equals(second),
        "Two viewers asking for the same recipes should be given the same order");
    context.assertTrue(
        first.equals(sorted), "The station recipe list should be sorted by id but was " + first);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyListedRecipeCarriesItsIngredientsAndResult(TestContext context) {
    StationRecipes.from(recipes(context))
        .forEach(
            entry -> {
              context.assertFalse(
                  entry.value().inputs().isEmpty(),
                  "Station recipe " + entry.id() + " should carry the ingredients a viewer draws");
              context.assertFalse(
                  entry.value().result().isEmpty(),
                  "Station recipe " + entry.id() + " should carry the result a viewer draws");
            });
    context.complete();
  }

  private static RecipeManager recipes(final TestContext context) {
    return context.getWorld().getServer().getRecipeManager();
  }
}
