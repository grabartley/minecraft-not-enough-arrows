package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.arrow.ArrowRecipeAudit;
import com.grahambartley.notenougharrows.arrow.ArrowRecipeAudit.ArrowRecipe;
import com.grahambartley.notenougharrows.arrow.ArrowRecipeAudit.Collision;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.recipe.FletchingIngredient;
import com.grahambartley.notenougharrows.recipe.FletchingRecipe;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class ArrowRecipeAuditGameTest implements FabricGameTest {
  private static final String BATCH = "release-audit-recipes";
  private static final int GRID = 3;
  private static final int CENTRE = 4;

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void noTwoCraftingTableRecipesShareACentreOverOneBase(TestContext context) {
    final List<Collision> collisions =
        ArrowRecipeAudit.sharedCentres(craftingTableRecipes(context));

    context.assertTrue(
        collisions.isEmpty(), "Crafting table recipes sharing a centre (REL-14): " + collisions);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void noTwoStationRecipesShareACentreOverOneBase(TestContext context) {
    final List<Collision> collisions = ArrowRecipeAudit.sharedCentres(stationRecipes(context));

    context.assertTrue(
        collisions.isEmpty(), "Station recipes sharing a centre (REL-14): " + collisions);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theAuditSeesARecipeForEveryArrow(TestContext context) {
    final Set<Identifier> tableResults =
        craftingTableRecipes(context).stream().map(ArrowRecipe::result).collect(Collectors.toSet());
    final Set<Identifier> stationResults =
        stationRecipes(context).stream().map(ArrowRecipe::result).collect(Collectors.toSet());

    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      context.assertTrue(
          tableResults.contains(arrow.id()),
          "The recipe audit found no ring-shaped crafting table recipe for " + arrow.id());
      context.assertTrue(
          stationResults.contains(arrow.id()),
          "The recipe audit found no base-and-centre station recipe for " + arrow.id());
    }
    context.complete();
  }

  private static List<ArrowRecipe> craftingTableRecipes(final TestContext context) {
    return context.getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream()
        .map(entry -> ringRecipe(context, entry))
        .flatMap(Optional::stream)
        .toList();
  }

  private static Optional<ArrowRecipe> ringRecipe(
      final TestContext context, final RecipeEntry<?> entry) {
    if (!(entry.value() instanceof ShapedRecipe shaped)
        || shaped.getWidth() != GRID
        || shaped.getHeight() != GRID) {
      return Optional.empty();
    }
    final Identifier result = itemId(shaped.getResult(context.getWorld().getRegistryManager()));
    if (!NotEnoughArrows.MOD_ID.equals(result.getNamespace())) {
      return Optional.empty();
    }
    final List<Ingredient> grid = shaped.getIngredients();
    final Set<Identifier> base = items(grid.get(0));
    final boolean uniformRing =
        IntStream.range(0, grid.size())
            .filter(cell -> cell != CENTRE)
            .allMatch(cell -> items(grid.get(cell)).equals(base));
    if (!uniformRing) {
      return Optional.empty();
    }
    return Optional.of(new ArrowRecipe(entry.id(), result, base, items(grid.get(CENTRE))));
  }

  private static List<ArrowRecipe> stationRecipes(final TestContext context) {
    return context.getWorld().getRecipeManager().listAllOfType(ModRecipes.FLETCHING).stream()
        .map(ArrowRecipeAuditGameTest::baseAndCentre)
        .flatMap(Optional::stream)
        .toList();
  }

  private static Optional<ArrowRecipe> baseAndCentre(final RecipeEntry<FletchingRecipe> entry) {
    final List<FletchingIngredient> inputs = entry.value().inputs();
    if (inputs.size() != 2) {
      return Optional.empty();
    }
    final Optional<FletchingIngredient> base =
        inputs.stream()
            .filter(input -> input.count() == FletchingTestSupport.SHIPPED_BASE_ARROWS)
            .findFirst();
    final Optional<FletchingIngredient> centre =
        inputs.stream()
            .filter(input -> input.count() != FletchingTestSupport.SHIPPED_BASE_ARROWS)
            .findFirst();
    if (base.isEmpty() || centre.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        new ArrowRecipe(
            entry.id(),
            itemId(entry.value().result()),
            items(base.get().ingredient()),
            items(centre.get().ingredient())));
  }

  private static Set<Identifier> items(final Ingredient ingredient) {
    return Arrays.stream(ingredient.getMatchingStacks())
        .map(ArrowRecipeAuditGameTest::itemId)
        .collect(Collectors.toSet());
  }

  private static Identifier itemId(final ItemStack stack) {
    return Registries.ITEM.getId(stack.getItem());
  }
}
