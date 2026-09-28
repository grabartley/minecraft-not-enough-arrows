package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.server.RecipeSync;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class RecipeSyncGameTest implements FabricGameTest {
  private static final String BATCH = "recipe-sync";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aVanillaClientIsSentNoRecipeOfThisMod(TestContext context) {
    final List<RecipeEntry<?>> everyRecipe = everyRecipe(context);
    context.assertTrue(
        everyRecipe.stream().anyMatch(RecipeSync::isModRecipe),
        "The server should hold this mod's recipes, or the filter proves nothing");

    final List<RecipeEntry<?>> sent =
        recipesIn(RecipeSync.forConnection(syncOf(everyRecipe), () -> false));

    context.assertFalse(
        sent.stream().anyMatch(RecipeSync::isModRecipe),
        "A vanilla client cannot decode this mod's recipes, so it must not be sent any");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aVanillaClientStillGetsEveryVanillaRecipe(TestContext context) {
    final List<RecipeEntry<?>> everyRecipe = everyRecipe(context);

    final List<RecipeEntry<?>> sent =
        recipesIn(RecipeSync.forConnection(syncOf(everyRecipe), () -> false));

    context.assertEquals(
        vanillaCraftingCount(everyRecipe),
        vanillaCraftingCount(sent),
        "Crafting table recipes sent to a vanilla client");
    context.assertEquals(
        everyRecipe.size() - (int) everyRecipe.stream().filter(RecipeSync::isModRecipe).count(),
        sent.size(),
        "Recipes sent to a vanilla client");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aModdedClientIsSentTheSyncUntouched(TestContext context) {
    final Packet<?> sync = syncOf(everyRecipe(context));

    context.assertTrue(
        RecipeSync.forConnection(sync, () -> true) == sync,
        "A client with this mod should be sent every recipe, station ones included");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anyOtherPacketPassesThroughWithoutAskingAboutTheClient(TestContext context) {
    final Packet<?> other = new UpdateSelectedSlotS2CPacket(0);

    final Packet<?> sent =
        RecipeSync.forConnection(
            other,
            () -> {
              throw new AssertionError("Only the recipe sync should ask about the client");
            });

    context.assertTrue(sent == other, "A packet other than the recipe sync must pass unchanged");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aStationRecipeAndACraftingRecipeOfThisModAreBothTheModsOwn(TestContext context) {
    final List<RecipeEntry<?>> everyRecipe = everyRecipe(context);

    context.assertTrue(
        everyRecipe.stream()
            .filter(recipe -> recipe.value().getType() == RecipeType.CRAFTING)
            .anyMatch(RecipeSync::isModRecipe),
        "This mod's crafting recipes make this mod's items, which a vanilla client cannot decode");
    context.assertTrue(
        everyRecipe.stream()
            .filter(recipe -> recipe.value().getType() != RecipeType.CRAFTING)
            .anyMatch(RecipeSync::isModRecipe),
        "This mod's station recipes use this mod's serializer");
    context.assertFalse(
        everyRecipe.stream()
            .filter(recipe -> !recipe.id().getNamespace().equals(NotEnoughArrows.MOD_ID))
            .anyMatch(RecipeSync::isModRecipe),
        "No recipe from another namespace belongs to this mod");
    context.complete();
  }

  private static List<RecipeEntry<?>> everyRecipe(final TestContext context) {
    return List.copyOf(context.getWorld().getServer().getRecipeManager().sortedValues());
  }

  private static Packet<?> syncOf(final List<RecipeEntry<?>> recipes) {
    return new SynchronizeRecipesS2CPacket(recipes);
  }

  private static List<RecipeEntry<?>> recipesIn(final Packet<?> packet) {
    return ((SynchronizeRecipesS2CPacket) packet).getRecipes();
  }

  private static long vanillaCraftingCount(final List<RecipeEntry<?>> recipes) {
    return recipes.stream()
        .filter(recipe -> recipe.id().getNamespace().equals("minecraft"))
        .filter(recipe -> recipe.value().getType() == RecipeType.CRAFTING)
        .count();
  }
}
