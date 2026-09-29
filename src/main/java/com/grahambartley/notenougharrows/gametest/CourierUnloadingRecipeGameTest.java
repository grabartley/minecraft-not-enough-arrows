package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import com.grahambartley.notenougharrows.social.CourierUnloadingRecipe;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public final class CourierUnloadingRecipeGameTest implements FabricGameTest {
  static final Identifier ID = Identifier.of(NotEnoughArrows.MOD_ID, "courier_arrow_unloading");

  private static final String BATCH = "courier-unloading-recipe";
  private static final BlockPos CRAFTER = new BlockPos(3, 3, 3);

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCraftingTableMatchesALoadedArrowOnItsOwn(TestContext context) {
    final Optional<RecipeEntry<CraftingRecipe>> matched =
        context
            .getWorld()
            .getRecipeManager()
            .getFirstMatch(
                RecipeType.CRAFTING,
                CourierLoadingRecipeGameTest.grid(SocialTestSupport.loadedCourier()),
                context.getWorld());

    context.assertTrue(
        matched.isPresent() && matched.get().id().equals(ID),
        "The unloading recipe should match, matched " + matched.map(RecipeEntry::id));
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void craftsThePayloadBack(TestContext context) {
    final ItemStack crafted =
        recipe(context)
            .craft(
                CourierLoadingRecipeGameTest.grid(SocialTestSupport.loadedCourier()),
                context.getWorld().getRegistryManager());

    context.assertTrue(
        ItemStack.areEqual(
            crafted,
            new ItemStack(SocialTestSupport.PAYLOAD_ITEM, SocialTestSupport.PAYLOAD_COUNT)),
        "The payload should come back out, came " + crafted);
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void leavesTheEmptyArrowWhereTheLoadedOneWas(TestContext context) {
    final CraftingRecipeInput grid =
        CraftingRecipeInput.create(
            3, 1, List.of(ItemStack.EMPTY, SocialTestSupport.loadedCourier(), ItemStack.EMPTY));

    final DefaultedList<ItemStack> remainder = recipe(context).getRemainder(grid);

    context.assertTrue(
        CourierPayloads.isEmptyCourier(remainder.get(0)),
        "The trimmed grid's only slot should hold an empty courier arrow, held " + remainder);
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesAnEmptyArrow(TestContext context) {
    context.assertFalse(
        recipe(context)
            .matches(
                CourierLoadingRecipeGameTest.grid(SocialTestSupport.emptyCourier()),
                context.getWorld()),
        "An empty arrow has nothing to unload");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesALoadedArrowWithSomethingElse(TestContext context) {
    context.assertFalse(
        recipe(context)
            .matches(
                CourierLoadingRecipeGameTest.grid(
                    SocialTestSupport.loadedCourier(), new ItemStack(Items.STICK)),
                context.getWorld()),
        "Unloading takes a loaded arrow on its own");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aCrafterUnloadsOneArrowAndEjectsThePayloadAndTheEmptyArrow(TestContext context) {
    context.setBlockState(CRAFTER, Blocks.CRAFTER);
    final CrafterBlockEntity crafter = context.getBlockEntity(CRAFTER);
    crafter.setStack(0, SocialTestSupport.loadedCourier().copyWithCount(2));
    context.putAndRemoveRedstoneBlock(CRAFTER.up(), 2L);

    context.runAtTick(
        20,
        () -> {
          context.assertEquals(
              SocialTestSupport.PAYLOAD_COUNT,
              SocialTestSupport.droppedNearby(context, SocialTestSupport.PAYLOAD_ITEM),
              "Diamonds ejected");
          context.assertEquals(
              1,
              SocialTestSupport.droppedNearby(context, SocialArrows.COURIER_ARROW.item()),
              "Empty arrows ejected");
          context.assertEquals(1, crafter.getStack(0).getCount(), "Loaded arrows left inside");
          context.assertTrue(CourierPayloads.isLoaded(crafter.getStack(0)), "still loaded");
          context.complete();
        });
  }

  private static CourierUnloadingRecipe recipe(final TestContext context) {
    return (CourierUnloadingRecipe)
        context.getWorld().getRecipeManager().get(ID).orElseThrow().value();
  }
}
