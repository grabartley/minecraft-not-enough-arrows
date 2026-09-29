package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.social.CourierLoad;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class CourierLoadGameTest implements FabricGameTest {
  private static final String BATCH = "courier-load";
  private static final CourierArrowConfig DEFAULTS = CourierArrowConfig.defaults();

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void findsTheArrowAndThePayloadInEitherOrder(TestContext context) {
    final ItemStack arrow = SocialTestSupport.emptyCourier();
    final ItemStack diamonds = new ItemStack(Items.DIAMOND, 40);

    final CourierLoad forward =
        CourierLoad.of(List.of(arrow, ItemStack.EMPTY, diamonds), DEFAULTS).orElseThrow();
    final CourierLoad backward = CourierLoad.of(List.of(diamonds, arrow), DEFAULTS).orElseThrow();

    context.assertTrue(forward.arrow() == arrow && forward.payload() == diamonds, "Arrow first");
    context.assertTrue(
        backward.arrow() == arrow && backward.payload() == diamonds, "Payload first");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void loadsNoMoreThanTheCap(TestContext context) {
    final CourierLoad load =
        CourierLoad.of(
                List.of(SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND, 40)),
                DEFAULTS.withMaxPayload(16))
            .orElseThrow();

    context.assertEquals(16, load.count(), "Diamonds to load");
    context.assertEquals(
        16, CourierPayloads.payloadOf(load.loaded()).orElseThrow().getCount(), "Diamonds loaded");
    context.assertEquals(1, load.loaded().getCount(), "Loaded arrows");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void findsNoLoadWithoutExactlyAnEmptyArrowAndAnAcceptableStack(TestContext context) {
    assertNone(
        context, "A loaded arrow", SocialTestSupport.loadedCourier(), new ItemStack(Items.DIAMOND));
    assertNone(
        context,
        "Two couriers",
        SocialTestSupport.emptyCourier(),
        SocialTestSupport.emptyCourier());
    assertNone(context, "An arrow alone", SocialTestSupport.emptyCourier());
    assertNone(
        context,
        "Three stacks",
        SocialTestSupport.emptyCourier(),
        new ItemStack(Items.DIAMOND),
        new ItemStack(Items.EMERALD));
    assertNone(context, "No courier", new ItemStack(Items.ARROW), new ItemStack(Items.DIAMOND));
    context.complete();
  }

  private static void assertNone(
      final TestContext context, final String what, final ItemStack... stacks) {
    final Optional<CourierLoad> load = CourierLoad.of(List.of(stacks), DEFAULTS);
    context.assertTrue(load.isEmpty(), what + " should not load");
  }
}
