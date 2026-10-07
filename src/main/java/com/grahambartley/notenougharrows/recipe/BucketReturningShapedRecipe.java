package com.grahambartley.notenougharrows.recipe;

import com.grahambartley.notenougharrows.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;

public class BucketReturningShapedRecipe extends ShapedRecipe {

  public BucketReturningShapedRecipe(final ShapedRecipe shaped) {
    super(
        shaped.getGroup(),
        shaped.getCategory(),
        shaped.raw,
        shaped.result,
        shaped.showNotification());
  }

  @Override
  public DefaultedList<ItemStack> getRemainder(final CraftingRecipeInput input) {
    final DefaultedList<ItemStack> remainder =
        DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);
    for (int slot = 0; slot < remainder.size(); slot++) {
      final int filled = slot;
      RecipeRemainders.remainderOf(input.getStackInSlot(slot).getItem())
          .ifPresent(left -> remainder.set(filled, new ItemStack(left)));
    }
    return remainder;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.BUCKET_RETURNING_SHAPED_SERIALIZER;
  }
}
