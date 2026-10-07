package com.grahambartley.notenougharrows.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.item.ItemStack;

public record FletchingWithdrawal(int slot, int count) {

  public FletchingWithdrawal {
    if (slot < 0) {
      throw new IllegalArgumentException("Withdrawal slot must not be negative but was " + slot);
    }
    if (count < FletchingIngredient.MIN_COUNT) {
      throw new IllegalArgumentException(
          "Withdrawal count must be at least "
              + FletchingIngredient.MIN_COUNT
              + " but was "
              + count);
    }
  }

  public ItemStack containerLeftBy(final ItemStack drawnFrom) {
    Objects.requireNonNull(drawnFrom, "drawnFrom");
    if (drawnFrom.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return RecipeRemainders.remainderOf(drawnFrom.getItem())
        .map(left -> new ItemStack(left, count))
        .orElse(ItemStack.EMPTY);
  }

  public static List<FletchingWithdrawal> plan(
      final FletchingRecipe recipe, final FletchingRecipeInput input) {
    Objects.requireNonNull(recipe, "recipe");
    Objects.requireNonNull(input, "input");

    return recipe
        .slotAssignment(input)
        .map(assignment -> withdrawals(recipe.inputs(), assignment))
        .orElseGet(List::of);
  }

  private static List<FletchingWithdrawal> withdrawals(
      final List<FletchingIngredient> ingredients, final List<Integer> assignment) {
    final List<FletchingWithdrawal> withdrawals = new ArrayList<>(assignment.size());
    for (int index = 0; index < assignment.size(); index++) {
      withdrawals.add(
          new FletchingWithdrawal(assignment.get(index), ingredients.get(index).count()));
    }
    return List.copyOf(withdrawals);
  }
}
