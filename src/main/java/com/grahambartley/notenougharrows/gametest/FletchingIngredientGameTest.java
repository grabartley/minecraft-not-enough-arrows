package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.recipe.FletchingIngredient;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FletchingIngredientGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void acceptsAStackHoldingAtLeastTheDemandedCount(TestContext context) {
    final FletchingIngredient fourArrows =
        new FletchingIngredient(Ingredient.ofItems(Items.ARROW), 4);

    context.assertTrue(
        fourArrows.test(new ItemStack(Items.ARROW, 4)), "Exactly four arrows should be accepted");
    context.assertTrue(
        fourArrows.test(new ItemStack(Items.ARROW, 64)),
        "More than four arrows should be accepted");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void rejectsAStackShortOfTheDemandedCount(TestContext context) {
    context.assertFalse(
        new FletchingIngredient(Ingredient.ofItems(Items.ARROW), 4)
            .test(new ItemStack(Items.ARROW, 3)),
        "Three arrows should not satisfy a demand for four");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void rejectsTheWrongItemNoMatterHowManyOfItThereAre(TestContext context) {
    context.assertFalse(
        new FletchingIngredient(Ingredient.ofItems(Items.ARROW), 1)
            .test(new ItemStack(Items.STONE, 64)),
        "A stack of the wrong item should never satisfy an ingredient");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void acceptsAnyItemInATaggedIngredient(TestContext context) {
    final FletchingIngredient anyPlank =
        new FletchingIngredient(Ingredient.fromTag(ItemTags.PLANKS), 2);

    context.assertTrue(
        anyPlank.test(new ItemStack(Items.OAK_PLANKS, 2)), "Oak planks should satisfy the tag");
    context.assertTrue(
        anyPlank.test(new ItemStack(Items.SPRUCE_PLANKS, 2)),
        "Spruce planks should satisfy the same tag");
    context.assertFalse(
        anyPlank.test(new ItemStack(Items.STONE, 2)), "Stone should not satisfy a planks tag");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void rejectsACountOutsideTheAllowedRange(TestContext context) {
    for (final int count : new int[] {0, -1, FletchingIngredient.MAX_COUNT + 1}) {
      boolean rejected = false;
      try {
        new FletchingIngredient(Ingredient.ofItems(Items.ARROW), count);
      } catch (final IllegalArgumentException expected) {
        rejected = true;
      }
      context.assertTrue(rejected, "A count of " + count + " should be rejected");
    }
    context.complete();
  }
}
