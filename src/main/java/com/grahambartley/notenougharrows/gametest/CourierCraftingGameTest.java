package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.social.CourierCrafting;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class CourierCraftingGameTest implements FabricGameTest {
  private static final String BATCH = "courier-crafting";
  private static final int ONE = 1;

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void takesTheWholePayloadForALoadingCraft(TestContext context) {
    context.assertEquals(
        40,
        CourierCrafting.takenFromGrid(
            entry(context, CourierLoadingRecipeGameTest.ID), new ItemStack(Items.DIAMOND, 40), ONE),
        "Diamonds taken");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void takesOneFromEverySlotForAnyOtherCraft(TestContext context) {
    context.assertEquals(
        ONE,
        CourierCrafting.takenFromGrid(
            entry(context, Identifier.of(NotEnoughArrows.MOD_ID, "chicken_arrow")),
            new ItemStack(Items.ARROW, 40),
            ONE),
        "Arrows taken by an ordinary recipe");
    context.assertEquals(
        ONE,
        CourierCrafting.takenFromGrid(null, new ItemStack(Items.ARROW, 40), ONE),
        "Arrows taken with no recipe known");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void keepsTheLoadingRecipeFromACrafter(TestContext context) {
    context.assertTrue(
        CourierCrafting.offeredToCrafter(
                Optional.of(entry(context, CourierLoadingRecipeGameTest.ID)))
            .isEmpty(),
        "A crafter must not load a courier arrow");
    context.assertTrue(
        CourierCrafting.offeredToCrafter(
                Optional.of(entry(context, CourierUnloadingRecipeGameTest.ID)))
            .isPresent(),
        "A crafter may unload one");
    context.complete();
  }

  @SuppressWarnings("unchecked")
  private static RecipeEntry<CraftingRecipe> entry(final TestContext context, final Identifier id) {
    return (RecipeEntry<CraftingRecipe>)
        context.getWorld().getRecipeManager().get(id).orElseThrow();
  }
}
