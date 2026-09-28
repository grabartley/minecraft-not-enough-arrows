package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.item.BaseArrowItem;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class BaseArrowItemGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void anUntintedArrowIsItsOnlyVariant(final TestContext context) {
    final List<ItemStack> variants = ModArrows.RUST_ARROW.item().variants();

    context.assertEquals(variants.size(), 1, "An untinted arrow should have one variant");
    context.assertTrue(
        ItemStack.areItemsAndComponentsEqual(
            variants.getFirst(), new ItemStack(ModArrows.RUST_ARROW.item())),
        "An untinted arrow's only variant should be a plain stack of it");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aTintedArrowOffersEveryChoiceAsAVariant(final TestContext context) {
    context.assertEquals(
        BaseArrowItem.variantsOf(ModArrows.PAINT_ARROW.item()).size(),
        16,
        "The paint arrow should offer a variant per dye");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void anItemThatIsNotAModArrowIsItsOnlyVariant(final TestContext context) {
    final List<ItemStack> variants = BaseArrowItem.variantsOf(Items.ARROW);

    context.assertEquals(variants.size(), 1, "A vanilla arrow should have one variant");
    context.assertTrue(variants.getFirst().isOf(Items.ARROW), "It should be the vanilla arrow");
    context.complete();
  }
}
