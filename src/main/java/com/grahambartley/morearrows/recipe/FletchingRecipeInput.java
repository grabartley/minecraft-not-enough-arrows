package com.grahambartley.morearrows.recipe;

import java.util.List;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record FletchingRecipeInput(List<ItemStack> stacks) implements RecipeInput {

  public FletchingRecipeInput {
    stacks = List.copyOf(Objects.requireNonNull(stacks, "stacks"));
  }

  public static FletchingRecipeInput of(final ItemStack... stacks) {
    return new FletchingRecipeInput(List.of(stacks));
  }

  @Override
  public ItemStack getStackInSlot(final int slot) {
    return slot < 0 || slot >= stacks.size() ? ItemStack.EMPTY : stacks.get(slot);
  }

  @Override
  public int getSize() {
    return stacks.size();
  }
}
