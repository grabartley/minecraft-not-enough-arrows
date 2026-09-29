package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.social.CourierLoadingRecipe;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class CourierLoadingRecipeGameTest implements FabricGameTest {
  static final Identifier ID = Identifier.of(NotEnoughArrows.MOD_ID, "courier_arrow_loading");

  private static final String BATCH = "courier-loading-recipe";
  private static final String CONFIGURED_BATCH = "courier-loading-recipe-configured";
  private static final int SMALL_CAP = 16;

  @BeforeBatch(batchId = CONFIGURED_BATCH)
  public void capAndRefuseBeforeBatch(ServerWorld world) {
    SocialTestSupport.useSocial(
        social ->
            social.withCourier(
                social
                    .courier()
                    .withMaxPayload(SMALL_CAP)
                    .withUndeliverable(List.of("minecraft:elytra"))));
  }

  @AfterBatch(batchId = CONFIGURED_BATCH)
  public void restoreDefaultsAfterBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCraftingTableMatchesAnEmptyCourierArrowAndAStack(TestContext context) {
    final Optional<RecipeEntry<CraftingRecipe>> matched =
        match(context, SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND, 40));

    context.assertTrue(
        matched.isPresent() && matched.get().id().equals(ID),
        "The loading recipe should match, matched " + matched.map(RecipeEntry::id));
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theOrderOfTheTwoStacksDoesNotMatter(TestContext context) {
    context.assertTrue(
        recipe(context)
            .matches(
                grid(new ItemStack(Items.DIAMOND, 40), SocialTestSupport.emptyCourier()),
                context.getWorld()),
        "Payload first, arrow second");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void craftsOneArrowCarryingTheWholeStack(TestContext context) {
    final ItemStack crafted =
        recipe(context)
            .craft(
                grid(
                    SocialTestSupport.emptyCourier().copyWithCount(8),
                    new ItemStack(Items.DIAMOND, 40)),
                context.getWorld().getRegistryManager());

    context.assertEquals(1, crafted.getCount(), "Loaded arrows crafted");
    context.assertEquals(
        40, CourierPayloads.payloadOf(crafted).orElseThrow().getCount(), "Diamonds loaded");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void takesOneArrowAndTheWholePayload(TestContext context) {
    final CourierLoadingRecipe recipe = recipe(context);

    context.assertEquals(
        1, recipe.consumedFrom(SocialTestSupport.emptyCourier().copyWithCount(8)), "Arrows used");
    context.assertEquals(
        40, recipe.consumedFrom(new ItemStack(Items.DIAMOND, 40)), "Diamonds used");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesAnAlreadyLoadedArrow(TestContext context) {
    assertNoMatch(
        context, "A loaded arrow", SocialTestSupport.loadedCourier(), new ItemStack(Items.EMERALD));
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesToLoadACourierArrowIntoAnother(TestContext context) {
    assertNoMatch(
        context,
        "A courier arrow inside a courier arrow",
        SocialTestSupport.emptyCourier(),
        SocialTestSupport.emptyCourier());
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesAGridOfThreeStacks(TestContext context) {
    assertNoMatch(
        context,
        "Three stacks",
        SocialTestSupport.emptyCourier(),
        new ItemStack(Items.DIAMOND),
        new ItemStack(Items.EMERALD));
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void refusesAGridWithoutACourierArrow(TestContext context) {
    assertNoMatch(
        context, "No courier arrow", new ItemStack(Items.ARROW), new ItemStack(Items.DIAMOND));
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = CONFIGURED_BATCH, tickLimit = 10)
  public void refusesAnUndeliverableItem(TestContext context) {
    assertNoMatch(
        context,
        "An undeliverable elytra",
        SocialTestSupport.emptyCourier(),
        new ItemStack(Items.ELYTRA));
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = CONFIGURED_BATCH, tickLimit = 10)
  public void loadsNoMoreThanTheConfiguredCap(TestContext context) {
    final ItemStack crafted =
        recipe(context)
            .craft(
                grid(SocialTestSupport.emptyCourier(), new ItemStack(Items.DIAMOND, 40)),
                context.getWorld().getRegistryManager());

    context.assertEquals(
        SMALL_CAP, CourierPayloads.payloadOf(crafted).orElseThrow().getCount(), "Diamonds loaded");
    context.assertEquals(
        SMALL_CAP, recipe(context).consumedFrom(new ItemStack(Items.DIAMOND, 40)), "Diamonds used");
    context.complete();
  }

  static CourierLoadingRecipe recipe(final TestContext context) {
    return (CourierLoadingRecipe)
        context.getWorld().getRecipeManager().get(ID).orElseThrow().value();
  }

  static CraftingRecipeInput grid(final ItemStack... stacks) {
    return CraftingRecipeInput.create(stacks.length, 1, List.of(stacks));
  }

  private static Optional<RecipeEntry<CraftingRecipe>> match(
      final TestContext context, final ItemStack... stacks) {
    return context
        .getWorld()
        .getRecipeManager()
        .getFirstMatch(RecipeType.CRAFTING, grid(stacks), context.getWorld());
  }

  private static void assertNoMatch(
      final TestContext context, final String what, final ItemStack... stacks) {
    context.assertFalse(
        recipe(context).matches(grid(stacks), context.getWorld()), what + " should not load");
    context.complete();
  }
}
