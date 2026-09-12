package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.recipe.FletchingIngredient;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import com.grahambartley.notenougharrows.recipe.FletchingRecipeInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class FletchingRecipeGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void staysOutOfTheVanillaRecipeBook(TestContext context) {
    context.assertTrue(
        FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows().isIgnoredInRecipeBook(),
        "A station recipe is never craftable from the recipe book, so the book must skip it"
            + " rather than warn about a category it cannot name");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void matchesInputsThatSatisfyEveryDeclaredIngredient(TestContext context) {
    context.assertTrue(
        FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()
            .matches(
                FletchingRecipeInput.of(new ItemStack(Items.ARROW, 4), new ItemStack(Items.TNT)),
                context.getWorld()),
        "Four arrows and one TNT should satisfy the recipe");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void matchesRegardlessOfWhichSlotHoldsWhichInput(TestContext context) {
    context.assertTrue(
        FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()
            .matches(
                FletchingRecipeInput.of(new ItemStack(Items.TNT), new ItemStack(Items.ARROW, 4)),
                context.getWorld()),
        "The station should not care which slot holds which input");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void doesNotMatchWhenAnInputIsShortOfTheDemandedCount(TestContext context) {
    context.assertFalse(
        FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()
            .matches(
                FletchingRecipeInput.of(new ItemStack(Items.ARROW, 3), new ItemStack(Items.TNT)),
                context.getWorld()),
        "Three arrows should not satisfy an input demanding four");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void doesNotMatchWhenADeclaredIngredientIsMissing(TestContext context) {
    context.assertFalse(
        FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()
            .matches(
                FletchingRecipeInput.of(new ItemStack(Items.ARROW, 4), ItemStack.EMPTY),
                context.getWorld()),
        "A recipe demanding TNT should not match without it");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void doesNotMatchWhenTheStationHoldsAnItemTheRecipeDoesNotWant(TestContext context) {
    context.assertFalse(
        FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()
            .matches(
                FletchingRecipeInput.of(
                    new ItemStack(Items.ARROW, 4),
                    new ItemStack(Items.TNT),
                    new ItemStack(Items.STONE)),
                context.getWorld()),
        "A leftover item in the station should stop the recipe matching");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void doesNotMatchWhenOneStackWouldHaveToCoverTwoIngredients(TestContext context) {
    final FletchingRecipe recipe =
        new FletchingRecipe(
            "",
            List.of(
                new FletchingIngredient(Ingredient.ofItems(Items.ARROW), 1),
                new FletchingIngredient(Ingredient.ofItems(Items.ARROW), 1)),
            new ItemStack(Items.ARROW, 8));

    context.assertFalse(
        recipe.matches(FletchingRecipeInput.of(new ItemStack(Items.ARROW, 8)), context.getWorld()),
        "One stack must not stand in for two declared ingredients");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void craftsTheDeclaredResultWithoutHandingOutItsOwnStack(TestContext context) {
    final FletchingRecipe recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final FletchingRecipeInput input =
        FletchingRecipeInput.of(new ItemStack(Items.ARROW, 4), new ItemStack(Items.TNT));
    final ItemStack crafted = recipe.craft(input, context.getWorld().getRegistryManager());

    context.assertTrue(
        ItemStack.areEqual(new ItemStack(Items.ARROW, 8), crafted),
        "Crafting should yield eight arrows but yielded " + crafted);
    context.assertTrue(
        crafted != recipe.getResult(context.getWorld().getRegistryManager()),
        "Crafting should hand out a copy, never the recipe's own result stack");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void reportsOneIngredientPerDeclaredInput(TestContext context) {
    final FletchingRecipe recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();

    context.assertEquals(
        recipe.inputs().size(),
        recipe.getIngredients().size(),
        "Every declared input should surface as one ingredient");
    context.assertTrue(
        recipe.getIngredients().get(1).test(new ItemStack(Items.TNT)),
        "The reported ingredients should keep the order they were declared in");
    context.complete();
  }

  @GameTest(
      templateName = FabricGameTest.EMPTY_STRUCTURE,
      tickLimit = 10,
      batchId = "fletching-recipe-lookup")
  public void aLoadedRecipeIsFoundByRecipeLookup(TestContext context) {
    final RecipeManager recipes = context.getWorld().getServer().getRecipeManager();
    final List<RecipeEntry<?>> loaded = List.copyOf(recipes.values());
    final Identifier id = Identifier.of(NotEnoughArrows.MOD_ID, "gametest_fletching");

    try {
      final List<RecipeEntry<?>> withStationRecipe = new ArrayList<>(loaded);
      withStationRecipe.add(
          new RecipeEntry<>(id, FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()));
      recipes.setRecipes(withStationRecipe);

      final Optional<RecipeEntry<FletchingRecipe>> match =
          recipes.getFirstMatch(
              ModRecipes.FLETCHING,
              FletchingRecipeInput.of(new ItemStack(Items.ARROW, 4), new ItemStack(Items.TNT)),
              context.getWorld());

      context.assertTrue(
          match.isPresent(), "A loaded station recipe should be found by recipe lookup");
      context.assertTrue(
          id.equals(match.get().id()),
          "Recipe lookup should find the station recipe that was loaded");
      context.assertTrue(
          ItemStack.areEqual(
              new ItemStack(Items.ARROW, 8),
              match
                  .get()
                  .value()
                  .craft(
                      FletchingRecipeInput.of(
                          new ItemStack(Items.ARROW, 4), new ItemStack(Items.TNT)),
                      context.getWorld().getRegistryManager())),
          "The recipe found by lookup should craft its declared result");
    } finally {
      recipes.setRecipes(loaded);
    }
    context.complete();
  }

  @GameTest(
      templateName = FabricGameTest.EMPTY_STRUCTURE,
      tickLimit = 10,
      batchId = "fletching-recipe-lookup")
  public void recipeLookupFindsNothingForInputsNoStationRecipeWants(TestContext context) {
    final RecipeManager recipes = context.getWorld().getServer().getRecipeManager();
    final List<RecipeEntry<?>> loaded = List.copyOf(recipes.values());

    try {
      final List<RecipeEntry<?>> withStationRecipe = new ArrayList<>(loaded);
      withStationRecipe.add(
          new RecipeEntry<>(
              Identifier.of(NotEnoughArrows.MOD_ID, "gametest_fletching"),
              FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows()));
      recipes.setRecipes(withStationRecipe);

      context.assertTrue(
          recipes
              .getFirstMatch(
                  ModRecipes.FLETCHING,
                  FletchingRecipeInput.of(new ItemStack(Items.STONE, 4)),
                  context.getWorld())
              .isEmpty(),
          "Recipe lookup should find nothing for inputs no station recipe wants");
    } finally {
      recipes.setRecipes(loaded);
    }
    context.complete();
  }
}
