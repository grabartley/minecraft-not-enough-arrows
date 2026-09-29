package com.grahambartley.notenougharrows.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.CrafterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class CrafterBlockMixinGameTest implements FabricGameTest {
  private static final String BATCH = "crafter-block";

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCrafterFindsNoRecipeToLoadACourierArrow(TestContext context) {
    context.assertTrue(
        CrafterBlock.getCraftingRecipe(
                context.getWorld(),
                CourierLoadingRecipeGameTest.grid(
                    SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND, 40)))
            .isEmpty(),
        "A crafter takes one item per slot, so loading there would duplicate the payload");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCrafterCanStillUnloadOne(TestContext context) {
    context.assertTrue(
        CrafterBlock.getCraftingRecipe(
                context.getWorld(),
                CourierLoadingRecipeGameTest.grid(SocialTestSupport.loadedCourier()))
            .isPresent(),
        "Unloading takes one arrow and hands back the rest, which a crafter does exactly");
    context.complete();
  }
}
