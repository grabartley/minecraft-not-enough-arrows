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
    final List<Identifier> listed = ids(StationRecipes.from(recipes));
    final List<Identifier> sorted =
        listed.stream().sorted(Comparator.comparing(Identifier::toString)).toList();

    context.assertTrue(
        listed.equals(sorted),
        "Every viewer should be given one sorted order but was given " + listed);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void namesEveryStationRecipeUnderTheFletchingFolder(TestContext context) {
    ids(StationRecipes.from(recipes(context)))
        .forEach(
            id ->
                context.assertTrue(
                    id.getPath().startsWith("fletching/"),
                    "Every station recipe should be named under fletching/ but found " + id));
    context.complete();
  }

  private static List<Identifier> ids(final List<RecipeEntry<FletchingRecipe>> entries) {
    return entries.stream().map(RecipeEntry::id).toList();
  }

  private static RecipeManager recipes(final TestContext context) {
    return context.getWorld().getServer().getRecipeManager();
  }
}
