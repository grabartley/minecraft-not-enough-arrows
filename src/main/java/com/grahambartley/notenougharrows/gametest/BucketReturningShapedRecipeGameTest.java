package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.recipe.BucketReturningShapedRecipe;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class BucketReturningShapedRecipeGameTest implements FabricGameTest {
  private static final String BATCH = "bucket-returning-shaped";
  private static final BlockPos TABLE = new BlockPos(3, 2, 3);
  private static final Identifier FROST_RECIPE =
      Identifier.of(NotEnoughArrows.MOD_ID, "frost_arrow");
  private static final int RESULT = 0;
  private static final int FIRST_GRID_SLOT = 1;
  private static final int CENTRE_GRID_SLOT = 5;
  private static final int GRID_SLOTS = 9;
  private static final int FROST_YIELD = 8;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void theFrostRecipeLoadsAsABucketReturningShapedRecipe(TestContext context) {
    final Optional<RecipeEntry<?>> entry = context.getWorld().getRecipeManager().get(FROST_RECIPE);

    context.assertTrue(
        entry.isPresent() && entry.get().value() instanceof BucketReturningShapedRecipe,
        "The frost arrow recipe should load as a bucket returning shaped recipe, but was "
            + entry.map(RecipeEntry::value));
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void itNamesItsOwnSerializer(TestContext context) {
    final Recipe<?> recipe =
        context.getWorld().getRecipeManager().get(FROST_RECIPE).orElseThrow().value();

    context.assertTrue(
        recipe.getSerializer() == ModRecipes.BUCKET_RETURNING_SHAPED_SERIALIZER,
        "The recipe should sync to clients through its own serializer, not "
            + recipe.getSerializer());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void craftingFrostArrowsLeavesOneEmptyBucketInTheGrid(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    fillFrostRecipe(table);

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertTrue(
        table.getCursorStack().isOf(ModArrows.FROST_ARROW.item())
            && table.getCursorStack().getCount() == FROST_YIELD,
        "Taking the result should give "
            + FROST_YIELD
            + " frost arrows but gave "
            + table.getCursorStack());
    final ItemStack centre = table.getSlot(CENTRE_GRID_SLOT).getStack();
    context.assertTrue(
        centre.isOf(Items.BUCKET) && centre.getCount() == 1,
        "The powder snow bucket should come back as one empty bucket, but the centre holds "
            + centre);
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void craftingFrostArrowsLeavesNothingWhereTheArrowsWere(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    fillFrostRecipe(table);

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    for (int slot = FIRST_GRID_SLOT; slot < FIRST_GRID_SLOT + GRID_SLOTS; slot++) {
      if (slot != CENTRE_GRID_SLOT) {
        context.assertTrue(
            table.getSlot(slot).getStack().isEmpty(),
            "Grid slot " + slot + " should be empty but holds " + table.getSlot(slot).getStack());
      }
    }
    context.complete();
  }

  private static void fillFrostRecipe(final CraftingScreenHandler table) {
    for (int slot = FIRST_GRID_SLOT; slot < FIRST_GRID_SLOT + GRID_SLOTS; slot++) {
      table
          .getSlot(slot)
          .setStack(
              slot == CENTRE_GRID_SLOT
                  ? new ItemStack(Items.POWDER_SNOW_BUCKET)
                  : new ItemStack(Items.ARROW));
    }
  }

  private static ServerPlayerEntity player(final TestContext context) {
    final ServerPlayerEntity player = ChaosTestSupport.survivalPlayerAt(context, TABLE.west());
    player.getInventory().clear();
    return player;
  }

  private static CraftingScreenHandler openTable(
      final TestContext context, final ServerPlayerEntity player) {
    context.setBlockState(TABLE, Blocks.CRAFTING_TABLE);
    return new CraftingScreenHandler(
        1,
        player.getInventory(),
        ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(TABLE)));
  }
}
