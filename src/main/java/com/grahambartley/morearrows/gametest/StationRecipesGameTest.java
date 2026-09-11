package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.StationRecipes;
import java.util.Comparator;
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
        "Every arrow the mod registers should surface one station recipe to a viewer");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void ordersRecipesTheSameWayForEveryViewerThatAsks(TestContext context) {
    final RecipeManager recipes = recipes(context);
    final List<Identifier> first = ids(StationRecipes.from(recipes));
    final List<Identifier> second = ids(StationRecipes.from(recipes));
    final List<Identifier> sorted =
        first.stream().sorted(Comparator.comparing(Identifier::toString)).toList();

    context.assertTrue(
        first.equals(second),
        "Two viewers asking for the same recipes should be given the same order");
    context.assertTrue(
        first.equals(sorted), "The station recipe list should be sorted by id but was " + first);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void listsOnlyStationRecipesAndNeverACraftingTableOne(TestContext context) {
    ids(StationRecipes.from(recipes(context)))
        .forEach(
            id ->
                context.assertTrue(
                    id.getPath().startsWith("fletching/"),
                    "The station list should hold only station recipes but held " + id));
    context.complete();
  }

  private static List<Identifier> ids(final List<RecipeEntry<FletchingRecipe>> entries) {
    return entries.stream().map(RecipeEntry::id).toList();
  }

  private static RecipeManager recipes(final TestContext context) {
    return context.getWorld().getServer().getRecipeManager();
  }
}
