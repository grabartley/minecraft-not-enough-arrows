package com.grahambartley.notenougharrows.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryOps;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FletchingRecipeSerializerGameTest implements FabricGameTest {
  private static final String VALID_RECIPE_JSON =
      """
      {
        "type": "not-enough-arrows:fletching",
        "ingredients": [
          { "ingredient": { "item": "minecraft:arrow" }, "count": 4 },
          { "ingredient": { "item": "minecraft:tnt" } }
        ],
        "result": { "id": "minecraft:arrow", "count": 8 }
      }
      """;

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void datapackJsonLoadsIntoAFletchingRecipe(TestContext context) {
    final FletchingRecipe recipe = parseOrFail(context, VALID_RECIPE_JSON);

    context.assertEquals(2, recipe.inputs().size(), "Recipe should declare two inputs");
    context.assertTrue(
        recipe.inputs().get(0).ingredient().test(new ItemStack(Items.ARROW)),
        "First declared input should accept an arrow");
    context.assertEquals(
        4, recipe.inputs().get(0).count(), "First input should demand four arrows");
    context.assertTrue(
        ItemStack.areEqual(new ItemStack(Items.ARROW, 8), recipe.result()),
        "Recipe should yield eight arrows but yielded " + recipe.result());
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void anInputCountDefaultsToOneWhenTheJsonOmitsIt(TestContext context) {
    final FletchingRecipe recipe = parseOrFail(context, VALID_RECIPE_JSON);

    context.assertEquals(
        1, recipe.inputs().get(1).count(), "An input without a count should demand one item");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aRecipeSurvivesEncodingToJsonAndBackAgain(TestContext context) {
    final FletchingRecipe original = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final RegistryOps<JsonElement> ops = jsonOps(context);
    final JsonElement encoded =
        Recipe.CODEC.encodeStart(ops, original).getOrThrow(IllegalStateException::new);
    final FletchingRecipe decoded = parseOrFail(context, encoded.toString());

    context.assertEquals(
        original.inputs().size(), decoded.inputs().size(), "Input count should survive JSON");
    context.assertEquals(
        original.inputs().get(0).count(),
        decoded.inputs().get(0).count(),
        "Input demand should survive JSON");
    context.assertTrue(
        ItemStack.areEqual(original.result(), decoded.result()),
        "Result should survive JSON but became " + decoded.result());
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aRecipeSurvivesAWireRoundTrip(TestContext context) {
    final FletchingRecipe original = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();
    final RegistryByteBuf buf =
        new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
    ModRecipes.FLETCHING_SERIALIZER.packetCodec().encode(buf, original);
    final FletchingRecipe received = ModRecipes.FLETCHING_SERIALIZER.packetCodec().decode(buf);

    context.assertEquals(
        original.inputs().size(), received.inputs().size(), "Input count should reach the client");
    context.assertEquals(
        original.inputs().get(0).count(),
        received.inputs().get(0).count(),
        "Input demand should reach the client");
    context.assertTrue(
        received.inputs().get(1).ingredient().test(new ItemStack(Items.TNT)),
        "Second input should still accept TNT after reaching the client");
    context.assertTrue(
        ItemStack.areEqual(original.result(), received.result()),
        "Result should reach the client but became " + received.result());
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void malformedJsonIsReportedAsAnErrorRatherThanThrowing(TestContext context) {
    final List<String> rejected =
        List.of(
            """
            { "type": "not-enough-arrows:fletching",
              "ingredients": [],
              "result": { "id": "minecraft:arrow", "count": 8 } }
            """,
            """
            { "type": "not-enough-arrows:fletching",
              "ingredients": [ { "ingredient": { "item": "minecraft:arrow" }, "count": 0 } ],
              "result": { "id": "minecraft:arrow", "count": 8 } }
            """,
            """
            { "type": "not-enough-arrows:fletching",
              "ingredients": [ { "ingredient": { "item": "not-enough-arrows:not_an_item" } } ],
              "result": { "id": "minecraft:arrow", "count": 8 } }
            """,
            """
            { "type": "not-enough-arrows:fletching",
              "ingredients": [ { "ingredient": { "item": "minecraft:arrow" } } ] }
            """,
            """
            { "type": "not-enough-arrows:fletching",
              "result": { "id": "minecraft:arrow", "count": 8 } }
            """);

    for (final String json : rejected) {
      final DataResult<Recipe<?>> result = parse(context, json);
      context.assertTrue(result.isError(), "Malformed recipe JSON should be rejected: " + json);
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void moreInputsThanTheStationCanHoldAreRejected(TestContext context) {
    final StringBuilder ingredients = new StringBuilder();
    for (int index = 0; index <= FletchingRecipe.MAX_INPUTS; index++) {
      ingredients
          .append(index == 0 ? "" : ",")
          .append("{ \"ingredient\": { \"item\": \"minecraft:arrow\" } }");
    }
    final String json =
        "{ \"type\": \"not-enough-arrows:fletching\", \"ingredients\": ["
            + ingredients
            + "], \"result\": { \"id\": \"minecraft:arrow\", \"count\": 8 } }";

    context.assertTrue(
        parse(context, json).isError(),
        "A recipe declaring more than "
            + FletchingRecipe.MAX_INPUTS
            + " inputs should be rejected");
    context.complete();
  }

  private static RegistryOps<JsonElement> jsonOps(final TestContext context) {
    return RegistryOps.of(JsonOps.INSTANCE, context.getWorld().getRegistryManager());
  }

  private static DataResult<Recipe<?>> parse(final TestContext context, final String json) {
    return Recipe.CODEC.parse(jsonOps(context), JsonParser.parseString(json));
  }

  private static FletchingRecipe parseOrFail(final TestContext context, final String json) {
    final Recipe<?> recipe = parse(context, json).getOrThrow(IllegalStateException::new);
    context.assertTrue(
        recipe instanceof FletchingRecipe,
        "Recipe JSON typed not-enough-arrows:fletching should load as a FletchingRecipe but loaded as "
            + recipe.getClass().getSimpleName());
    return (FletchingRecipe) recipe;
  }
}
