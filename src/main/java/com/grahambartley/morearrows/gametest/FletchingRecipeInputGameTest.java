package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.recipe.FletchingRecipeInput;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FletchingRecipeInputGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void reportsAsManySlotsAsItWasGivenStacks(TestContext context) {
    context.assertEquals(
        3,
        FletchingRecipeInput.of(
                new ItemStack(Items.ARROW), ItemStack.EMPTY, new ItemStack(Items.TNT))
            .getSize(),
        "The input should report one slot per stack, empty slots included");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void handsBackTheStackHeldInEachSlot(TestContext context) {
    final ItemStack arrows = new ItemStack(Items.ARROW, 4);
    final FletchingRecipeInput input = FletchingRecipeInput.of(arrows, new ItemStack(Items.TNT));

    context.assertTrue(input.getStackInSlot(0) == arrows, "Slot zero should hold the arrows");
    context.assertTrue(input.getStackInSlot(1).isOf(Items.TNT), "Slot one should hold the TNT");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void treatsSlotsOutsideItsRangeAsEmpty(TestContext context) {
    final FletchingRecipeInput input = FletchingRecipeInput.of(new ItemStack(Items.ARROW));

    context.assertTrue(input.getStackInSlot(-1).isEmpty(), "A negative slot should read as empty");
    context.assertTrue(
        input.getStackInSlot(5).isEmpty(), "A slot past the end should read as empty");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void isEmptyOnlyWhileEverySlotIsEmpty(TestContext context) {
    context.assertTrue(
        FletchingRecipeInput.of(ItemStack.EMPTY, ItemStack.EMPTY).isEmpty(),
        "An input holding nothing should report itself empty");
    context.assertFalse(
        FletchingRecipeInput.of(ItemStack.EMPTY, new ItemStack(Items.ARROW)).isEmpty(),
        "An input holding one stack should not report itself empty");
    context.complete();
  }
}
