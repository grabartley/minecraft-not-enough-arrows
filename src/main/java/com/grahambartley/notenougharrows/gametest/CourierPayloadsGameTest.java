package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.social.CourierPayloads;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Unit;

public final class CourierPayloadsGameTest implements FabricGameTest {
  private static final String BATCH = "courier-payloads";

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void tellsAnEmptyCourierArrowFromALoadedOne(TestContext context) {
    final ItemStack empty = SocialTestSupport.emptyCourier();
    final ItemStack loaded = SocialTestSupport.loadedCourier();

    context.assertTrue(CourierPayloads.isEmptyCourier(empty), "An empty courier arrow");
    context.assertFalse(CourierPayloads.isLoaded(empty), "is not loaded");
    context.assertTrue(CourierPayloads.isLoaded(loaded), "A loaded courier arrow");
    context.assertFalse(CourierPayloads.isEmptyCourier(loaded), "is not empty");
    context.assertFalse(
        CourierPayloads.isCourier(new ItemStack(Items.ARROW)), "A vanilla arrow is no courier");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void loadingPutsTheExactStackOnOneArrow(TestContext context) {
    final ItemStack payload = new ItemStack(Items.GOLDEN_APPLE, 3);
    final ItemStack loaded =
        CourierPayloads.loaded(SocialTestSupport.emptyCourier().copyWithCount(8), payload);

    context.assertEquals(1, loaded.getCount(), "Loading acts on one arrow");
    context.assertTrue(
        ItemStack.areEqual(CourierPayloads.payloadOf(loaded).orElseThrow(), payload),
        "The loaded arrow carries the stack it was given");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void emptyingLeavesAPlainEmptyArrowThatStacksWithNewOnes(TestContext context) {
    final ItemStack emptied = CourierPayloads.emptied(SocialTestSupport.loadedCourier());

    context.assertTrue(
        ItemStack.areItemsAndComponentsEqual(emptied, SocialTestSupport.emptyCourier()),
        "An emptied arrow should be just an empty courier arrow");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCopyFiredWithoutSpendingTheArrowCarriesNothingForASurvivalShooter(
      TestContext context) {
    final ItemStack copy = SocialTestSupport.loadedCourier();
    copy.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

    context.assertFalse(
        CourierPayloads.isLoaded(CourierPayloads.asFired(copy, false)),
        "A multishot copy must not duplicate the payload");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCreativeShooterStillSendsThePayload(TestContext context) {
    final ItemStack copy = SocialTestSupport.loadedCourier();
    copy.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

    context.assertTrue(
        CourierPayloads.isLoaded(CourierPayloads.asFired(copy, true)),
        "A creative shooter's arrow carries its payload");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aSpentArrowKeepsItsPayload(TestContext context) {
    context.assertTrue(
        CourierPayloads.isLoaded(CourierPayloads.asFired(SocialTestSupport.loadedCourier(), false)),
        "An arrow taken from the quiver keeps its payload");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void onlyACourierArrowCanBeLoaded(TestContext context) {
    boolean refused = false;
    try {
      CourierPayloads.loaded(new ItemStack(Items.ARROW), new ItemStack(Items.DIAMOND));
    } catch (final IllegalArgumentException expected) {
      refused = true;
    }
    context.assertTrue(refused, "A vanilla arrow cannot carry a payload");
    context.complete();
  }
}
