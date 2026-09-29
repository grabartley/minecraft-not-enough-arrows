package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.ModRecipes;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CourierUnloadingRecipe extends SpecialCraftingRecipe {

  public CourierUnloadingRecipe(final CraftingRecipeCategory category) {
    super(category);
  }

  @Override
  public boolean matches(final CraftingRecipeInput input, final World world) {
    return loadedArrowSlot(input).isPresent();
  }

  @Override
  public ItemStack craft(
      final CraftingRecipeInput input, final RegistryWrapper.WrapperLookup registries) {
    final OptionalInt slot = loadedArrowSlot(input);
    if (slot.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return CourierPayloads.payloadOf(input.getStackInSlot(slot.getAsInt())).orElse(ItemStack.EMPTY);
  }

  @Override
  public DefaultedList<ItemStack> getRemainder(final CraftingRecipeInput input) {
    final DefaultedList<ItemStack> remainder =
        DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);
    loadedArrowSlot(input)
        .ifPresent(
            slot -> remainder.set(slot, CourierPayloads.emptied(input.getStackInSlot(slot))));
    return remainder;
  }

  @Override
  public boolean fits(final int width, final int height) {
    return width * height >= 1;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.COURIER_UNLOADING_SERIALIZER;
  }

  private static OptionalInt loadedArrowSlot(final CraftingRecipeInput input) {
    if (input.getStackCount() != 1) {
      return OptionalInt.empty();
    }
    return IntStream.range(0, input.getSize())
        .filter(slot -> CourierPayloads.isLoaded(input.getStackInSlot(slot)))
        .findFirst();
  }
}
