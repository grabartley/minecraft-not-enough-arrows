package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.social.CourierRefusals;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class CourierRefusalsGameTest implements FabricGameTest {
  private static final String BATCH = "courier-refusals";
  private static final CourierArrowConfig DEFAULTS = CourierArrowConfig.defaults();

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesToCarryAnotherCourierArrow(TestContext context) {
    context.assertTrue(
        CourierRefusals.refusesToCarry(SocialTestSupport.emptyCourier(), DEFAULTS),
        "An empty courier arrow");
    context.assertTrue(
        CourierRefusals.refusesToCarry(SocialTestSupport.loadedCourier(), DEFAULTS),
        "A loaded courier arrow");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesToCarryNothing(TestContext context) {
    context.assertTrue(CourierRefusals.refusesToCarry(ItemStack.EMPTY, DEFAULTS), "Nothing");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesAnItemTheServerMadeUndeliverable(TestContext context) {
    final CourierArrowConfig config = DEFAULTS.withUndeliverable(List.of("minecraft:elytra"));

    context.assertTrue(
        CourierRefusals.refusesToCarry(new ItemStack(Items.ELYTRA), config), "An elytra");
    context.assertFalse(
        CourierRefusals.refusesToCarry(new ItemStack(Items.DIAMOND), config), "A diamond");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void carriesAnOrdinaryStack(TestContext context) {
    context.assertFalse(
        CourierRefusals.refusesToCarry(new ItemStack(Items.DIAMOND, 64), DEFAULTS), "Diamonds");
    context.assertFalse(
        CourierRefusals.refusesToCarry(new ItemStack(Items.ARROW, 64), DEFAULTS),
        "Plain arrows are not courier arrows");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesToDeliverAStackOverTheCap(TestContext context) {
    final CourierArrowConfig config = DEFAULTS.withMaxPayload(16);

    context.assertTrue(
        CourierRefusals.refusesToDeliver(new ItemStack(Items.DIAMOND, 17), config), "Seventeen");
    context.assertFalse(
        CourierRefusals.refusesToDeliver(new ItemStack(Items.DIAMOND, 16), config), "Sixteen");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void loadsNoMoreThanTheCap(TestContext context) {
    final CourierArrowConfig config = DEFAULTS.withMaxPayload(16);

    context.assertEquals(
        16, CourierRefusals.loadableCount(new ItemStack(Items.DIAMOND, 40), config), "Capped");
    context.assertEquals(
        5, CourierRefusals.loadableCount(new ItemStack(Items.DIAMOND, 5), config), "Under");
    context.assertEquals(
        0,
        CourierRefusals.loadableCount(SocialTestSupport.emptyCourier(), config),
        "A refused stack loads nothing");
    context.complete();
  }
}
