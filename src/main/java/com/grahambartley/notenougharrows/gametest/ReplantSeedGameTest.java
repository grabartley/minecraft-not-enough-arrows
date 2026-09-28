package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.ReplantSeed;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ReplantSeedGameTest implements FabricGameTest {
  private static final String BATCH = "replant-seed";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void takesExactlyOneSeedAndKeepsTheRest(TestContext context) {
    final List<ItemStack> kept =
        ReplantSeed.takenFrom(
                List.of(new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT_SEEDS, 3)),
                Items.WHEAT_SEEDS)
            .orElseThrow();

    context.assertEquals(kept.size(), 2, "Stacks kept");
    context.assertTrue(kept.get(0).isOf(Items.WHEAT) && kept.get(0).getCount() == 1, "Wheat kept");
    context.assertTrue(
        kept.get(1).isOf(Items.WHEAT_SEEDS) && kept.get(1).getCount() == 2, "Two seeds kept");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aLoneSeedIsTakenAndLeavesNoEmptyStack(TestContext context) {
    final List<ItemStack> kept =
        ReplantSeed.takenFrom(
                List.of(new ItemStack(Items.CARROT), new ItemStack(Items.WHEAT)), Items.CARROT)
            .orElseThrow();

    context.assertEquals(kept.size(), 1, "Only the wheat is left");
    context.assertTrue(kept.get(0).isOf(Items.WHEAT), "The wheat is left");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aHarvestWithNoSeedCannotReplant(TestContext context) {
    context.assertTrue(
        ReplantSeed.takenFrom(List.of(new ItemStack(Items.WHEAT)), Items.WHEAT_SEEDS).isEmpty(),
        "Nothing to replant from");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theDropsHandedInAreNeverChanged(TestContext context) {
    final ItemStack seeds = new ItemStack(Items.WHEAT_SEEDS, 2);

    ReplantSeed.takenFrom(List.of(seeds), Items.WHEAT_SEEDS);

    context.assertEquals(seeds.getCount(), 2, "The caller's seed count");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void missingDropsOrSeedTakeNothing(TestContext context) {
    context.assertTrue(ReplantSeed.takenFrom(null, Items.WHEAT_SEEDS).isEmpty(), "No drops");
    context.assertTrue(
        ReplantSeed.takenFrom(List.of(new ItemStack(Items.WHEAT_SEEDS)), null)
            .equals(Optional.empty()),
        "No seed");
    context.complete();
  }
}
