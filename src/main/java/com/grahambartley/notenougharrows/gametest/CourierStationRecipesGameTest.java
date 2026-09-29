package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import com.grahambartley.notenougharrows.recipe.FletchingRecipeInput;
import com.grahambartley.notenougharrows.recipe.FletchingWithdrawal;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import com.grahambartley.notenougharrows.social.CourierStationRecipes;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class CourierStationRecipesGameTest implements FabricGameTest {
  private static final String BATCH = "courier-station-recipes";
  private static final CourierArrowConfig DEFAULTS = CourierArrowConfig.defaults();

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void offersToLoadAnEmptyArrowWithAStack(TestContext context) {
    final List<RecipeEntry<FletchingRecipe>> offered =
        CourierStationRecipes.matching(
            FletchingRecipeInput.of(
                SocialTestSupport.emptyCourier(),
                ItemStack.EMPTY,
                new ItemStack(Items.DIAMOND, 40)),
            DEFAULTS);

    context.assertEquals(1, offered.size(), "Recipes offered");
    context.assertEquals(CourierStationRecipes.LOAD_ID, offered.get(0).id(), "The offer");
    context.assertEquals(
        40,
        CourierPayloads.payloadOf(offered.get(0).value().result()).orElseThrow().getCount(),
        "Diamonds it loads");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void loadingWithdrawsOneArrowAndTheWholeStack(TestContext context) {
    final FletchingRecipeInput inputs =
        FletchingRecipeInput.of(
            SocialTestSupport.emptyCourier().copyWithCount(5), new ItemStack(Items.DIAMOND, 40));
    final RecipeEntry<FletchingRecipe> loading =
        CourierStationRecipes.matching(inputs, DEFAULTS).get(0);

    final List<FletchingWithdrawal> plan = FletchingWithdrawal.plan(loading.value(), inputs);

    context.assertTrue(
        plan.contains(new FletchingWithdrawal(0, 1))
            && plan.contains(new FletchingWithdrawal(1, 40)),
        "The station should take one arrow and forty diamonds, planned " + plan);
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void offersToUnloadALoadedArrowOnItsOwn(TestContext context) {
    final List<RecipeEntry<FletchingRecipe>> offered =
        CourierStationRecipes.matching(
            FletchingRecipeInput.of(SocialTestSupport.loadedCourier()), DEFAULTS);

    context.assertEquals(1, offered.size(), "Recipes offered");
    context.assertEquals(CourierStationRecipes.UNLOAD_ID, offered.get(0).id(), "The offer");
    context.assertEquals(
        SocialTestSupport.PAYLOAD_COUNT,
        offered.get(0).value().result().getCount(),
        "Diamonds out");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void handsBackAnEmptyArrowOnlyAfterUnloading(TestContext context) {
    final FletchingRecipeInput unloadInputs =
        FletchingRecipeInput.of(SocialTestSupport.loadedCourier());
    final RecipeEntry<FletchingRecipe> unloading =
        CourierStationRecipes.matching(unloadInputs, DEFAULTS).get(0);
    final FletchingRecipeInput loadInputs =
        FletchingRecipeInput.of(SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND));
    final RecipeEntry<FletchingRecipe> loading =
        CourierStationRecipes.matching(loadInputs, DEFAULTS).get(0);

    final List<ItemStack> afterUnloading =
        CourierStationRecipes.handedBack(unloading, unloadInputs);

    context.assertTrue(
        afterUnloading.size() == 1 && CourierPayloads.isEmptyCourier(afterUnloading.get(0)),
        "Unloading hands back one empty courier arrow");
    context.assertTrue(
        CourierStationRecipes.handedBack(loading, loadInputs).isEmpty(),
        "Loading hands nothing back");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void offersNothingForARefusedOrCrowdedInput(TestContext context) {
    final CourierArrowConfig refusing = DEFAULTS.withUndeliverable(List.of("minecraft:diamond"));

    context.assertTrue(
        CourierStationRecipes.matching(
                FletchingRecipeInput.of(
                    SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND)),
                refusing)
            .isEmpty(),
        "An undeliverable stack");
    context.assertTrue(
        CourierStationRecipes.matching(
                FletchingRecipeInput.of(
                    SocialTestSupport.emptyCourier(), SocialTestSupport.emptyCourier()),
                DEFAULTS)
            .isEmpty(),
        "A courier arrow inside a courier arrow");
    context.assertTrue(
        CourierStationRecipes.matching(
                FletchingRecipeInput.of(
                    SocialTestSupport.emptyCourier(),
                    new ItemStack(Items.DIAMOND),
                    new ItemStack(Items.EMERALD)),
                DEFAULTS)
            .isEmpty(),
        "Three stacks");
    context.assertTrue(
        CourierStationRecipes.matching(
                FletchingRecipeInput.of(SocialTestSupport.emptyCourier()), DEFAULTS)
            .isEmpty(),
        "An empty arrow alone");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void loadsNoMoreThanTheCap(TestContext context) {
    final RecipeEntry<FletchingRecipe> loading =
        CourierStationRecipes.matching(
                FletchingRecipeInput.of(
                    SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND, 40)),
                DEFAULTS.withMaxPayload(16))
            .get(0);

    context.assertEquals(
        16,
        CourierPayloads.payloadOf(loading.value().result()).orElseThrow().getCount(),
        "Diamonds loaded");
    context.complete();
  }
}
