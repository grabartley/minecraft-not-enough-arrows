package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.recipe.BucketReturningShapedRecipe;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public final class BucketReturningShapedRecipeSerializerGameTest implements FabricGameTest {
  private static final String BATCH = "bucket-returning-shaped-serializer";
  private static final Identifier FROST_RECIPE =
      Identifier.of(NotEnoughArrows.MOD_ID, "frost_arrow");

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theFrostRecipeSurvivesTheTripToAClient(TestContext context) {
    final BucketReturningShapedRecipe sent = frostRecipe(context);

    final BucketReturningShapedRecipe received = roundTrip(context, sent);

    context.assertTrue(
        received.getWidth() == sent.getWidth() && received.getHeight() == sent.getHeight(),
        "The grid should stay " + sent.getWidth() + "x" + sent.getHeight());
    context.assertTrue(
        sameIngredients(sent.getIngredients(), received.getIngredients()),
        "Every grid ingredient should arrive unchanged");
    context.assertTrue(
        ItemStack.areEqual(sent.getResult(null), received.getResult(null)),
        "The result should arrive as "
            + sent.getResult(null)
            + " but was "
            + received.getResult(null));
    context.assertTrue(
        received.getGroup().equals(sent.getGroup()) && received.getCategory() == sent.getCategory(),
        "The recipe book group and category should arrive unchanged");
    context.complete();
  }

  private static BucketReturningShapedRecipe frostRecipe(final TestContext context) {
    return (BucketReturningShapedRecipe)
        context.getWorld().getRecipeManager().get(FROST_RECIPE).orElseThrow().value();
  }

  private static BucketReturningShapedRecipe roundTrip(
      final TestContext context, final BucketReturningShapedRecipe recipe) {
    final RegistryByteBuf buffer =
        new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
    ModRecipes.BUCKET_RETURNING_SHAPED_SERIALIZER.packetCodec().encode(buffer, recipe);
    return ModRecipes.BUCKET_RETURNING_SHAPED_SERIALIZER.packetCodec().decode(buffer);
  }

  private static boolean sameIngredients(
      final DefaultedList<Ingredient> sent, final DefaultedList<Ingredient> received) {
    if (sent.size() != received.size()) {
      return false;
    }
    for (int slot = 0; slot < sent.size(); slot++) {
      if (!sent.get(slot).equals(received.get(slot))) {
        return false;
      }
    }
    return true;
  }
}
