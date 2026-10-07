package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.recipe.RecipeRemainders;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class RecipeRemaindersGameTest implements FabricGameTest {
  private static final String BATCH = "recipe-remainders";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aPowderSnowBucketLeavesAnEmptyBucket(TestContext context) {
    assertRemainder(context, Items.POWDER_SNOW_BUCKET, Optional.of(Items.BUCKET));
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aMilkBucketKeepsVanillasEmptyBucket(TestContext context) {
    assertRemainder(context, Items.MILK_BUCKET, Optional.of(Items.BUCKET));
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aHoneyBottleKeepsVanillasGlassBottle(TestContext context) {
    assertRemainder(context, Items.HONEY_BOTTLE, Optional.of(Items.GLASS_BOTTLE));
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void anArrowLeavesNothing(TestContext context) {
    assertRemainder(context, Items.ARROW, Optional.empty());
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void airLeavesNothing(TestContext context) {
    assertRemainder(context, Items.AIR, Optional.empty());
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void vanillaStillGivesThePowderSnowBucketNoRemainder(TestContext context) {
    context.assertFalse(
        Items.POWDER_SNOW_BUCKET.hasRecipeRemainder(),
        "The powder snow bucket's own remainder must stay untouched for every other recipe");
    context.complete();
  }

  private static void assertRemainder(
      final TestContext context, final Item ingredient, final Optional<Item> expected) {
    final Optional<Item> actual = RecipeRemainders.remainderOf(ingredient);
    context.assertTrue(
        actual.equals(expected),
        ingredient + " should leave " + expected + " but leaves " + actual);
    context.complete();
  }
}
