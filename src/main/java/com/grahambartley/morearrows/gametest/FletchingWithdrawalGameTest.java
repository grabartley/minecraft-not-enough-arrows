package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.FletchingRecipeInput;
import com.grahambartley.morearrows.recipe.FletchingWithdrawal;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FletchingWithdrawalGameTest implements FabricGameTest {
  private static final String BATCH = "fletching-withdrawal";
  private static final int A_SURPLUS = 10;

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void namesTheSlotAndCountEachIngredientDrawsFrom(TestContext context) {
    final FletchingRecipe recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final FletchingRecipeInput input =
        FletchingRecipeInput.of(
            new ItemStack(Items.ARROW, FletchingTestSupport.ARROWS_CONSUMED),
            new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED));

    context.assertTrue(
        FletchingWithdrawal.plan(recipe, input)
            .equals(
                List.of(
                    new FletchingWithdrawal(0, FletchingTestSupport.ARROWS_CONSUMED),
                    new FletchingWithdrawal(1, FletchingTestSupport.TNT_CONSUMED))),
        "Each ingredient should draw its declared count from the slot it matched");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void followsTheIngredientsToWhicheverSlotsTheyLandedIn(TestContext context) {
    final FletchingRecipe recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final FletchingRecipeInput input =
        FletchingRecipeInput.of(
            new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED),
            new ItemStack(Items.ARROW, FletchingTestSupport.ARROWS_CONSUMED));

    context.assertTrue(
        FletchingWithdrawal.plan(recipe, input)
            .equals(
                List.of(
                    new FletchingWithdrawal(1, FletchingTestSupport.ARROWS_CONSUMED),
                    new FletchingWithdrawal(0, FletchingTestSupport.TNT_CONSUMED))),
        "Slot order should follow the items rather than the order the recipe declares them in");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void drawsOnlyTheDeclaredCountFromASlotHoldingMore(TestContext context) {
    final FletchingRecipe recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final FletchingRecipeInput input =
        FletchingRecipeInput.of(
            new ItemStack(Items.ARROW, A_SURPLUS),
            new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED));

    context.assertEquals(
        FletchingTestSupport.ARROWS_CONSUMED,
        FletchingWithdrawal.plan(recipe, input).get(0).count(),
        "Count drawn from a slot holding more than the recipe asks for");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void plansNothingForInputsTheRecipeDoesNotMatch(TestContext context) {
    final FletchingRecipe recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final FletchingRecipeInput input =
        FletchingRecipeInput.of(
            new ItemStack(Items.ARROW, FletchingTestSupport.ARROWS_CONSUMED - 1),
            new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED));

    context.assertTrue(
        FletchingWithdrawal.plan(recipe, input).isEmpty(),
        "Inputs no recipe matches should plan no withdrawal at all");
    context.complete();
  }
}
