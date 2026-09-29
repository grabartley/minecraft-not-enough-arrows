package com.grahambartley.notenougharrows.social;

import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

public final class CourierCrafting {

  private CourierCrafting() {}

  public static int takenFromGrid(
      @Nullable final RecipeEntry<?> crafted, final ItemStack stack, final int amount) {
    return crafted != null && crafted.value() instanceof CourierLoadingRecipe loading
        ? loading.consumedFrom(stack)
        : amount;
  }

  public static Optional<RecipeEntry<CraftingRecipe>> offeredToCrafter(
      final Optional<RecipeEntry<CraftingRecipe>> matched) {
    return matched.filter(entry -> !(entry.value() instanceof CourierLoadingRecipe));
  }
}
