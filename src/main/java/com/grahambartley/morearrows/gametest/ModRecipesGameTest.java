package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModRecipes;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ModRecipesGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theFletchingRecipeTypeResolvesInTheRecipeTypeRegistry(TestContext context) {
    context.assertTrue(
        Registries.RECIPE_TYPE.containsId(ModRecipes.FLETCHING_ID),
        "Recipe type " + ModRecipes.FLETCHING_ID + " should resolve in the recipe type registry");
    context.assertTrue(
        Registries.RECIPE_TYPE.get(ModRecipes.FLETCHING_ID) == ModRecipes.FLETCHING,
        "Recipe type " + ModRecipes.FLETCHING_ID + " should resolve to the registered instance");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theFletchingSerializerResolvesInTheRecipeSerializerRegistry(TestContext context) {
    context.assertTrue(
        Registries.RECIPE_SERIALIZER.containsId(ModRecipes.FLETCHING_ID),
        "Serializer "
            + ModRecipes.FLETCHING_ID
            + " should resolve in the recipe serializer registry");
    context.assertTrue(
        Registries.RECIPE_SERIALIZER.get(ModRecipes.FLETCHING_ID)
            == ModRecipes.FLETCHING_SERIALIZER,
        "Serializer " + ModRecipes.FLETCHING_ID + " should resolve to the registered instance");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aFletchingRecipeReportsTheRegisteredTypeAndSerializer(TestContext context) {
    final var recipe = FletchingTestSupport.fourArrowsAndOneTntGiveEightArrows();

    context.assertTrue(
        recipe.getType() == ModRecipes.FLETCHING,
        "A fletching recipe should report the registered recipe type");
    context.assertTrue(
        recipe.getSerializer() == ModRecipes.FLETCHING_SERIALIZER,
        "A fletching recipe should report the registered serializer");
    context.complete();
  }
}
