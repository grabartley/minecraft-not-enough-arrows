package com.grahambartley.notenougharrows.recipe;

import com.grahambartley.notenougharrows.ModRecipes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record FletchingRecipe(String group, List<FletchingIngredient> inputs, ItemStack result)
    implements Recipe<FletchingRecipeInput> {

  public static final int MIN_INPUTS = 1;
  public static final int MAX_INPUTS = 9;

  public FletchingRecipe {
    Objects.requireNonNull(group, "group");
    Objects.requireNonNull(result, "result");
    inputs = List.copyOf(Objects.requireNonNull(inputs, "inputs"));

    if (inputs.size() < MIN_INPUTS || inputs.size() > MAX_INPUTS) {
      throw new IllegalArgumentException(
          "Fletching recipe must declare between "
              + MIN_INPUTS
              + " and "
              + MAX_INPUTS
              + " inputs but declared "
              + inputs.size());
    }
    if (result.isEmpty()) {
      throw new IllegalArgumentException("Fletching recipe result must not be empty");
    }
  }

  @Override
  public boolean matches(final FletchingRecipeInput input, final World world) {
    return slotAssignment(input).isPresent();
  }

  public Optional<List<Integer>> slotAssignment(final FletchingRecipeInput input) {
    final List<Integer> occupiedSlots = occupiedSlots(input);
    final List<List<Integer>> candidateSlots =
        inputs.stream()
            .map(
                ingredient ->
                    occupiedSlots.stream()
                        .filter(slot -> ingredient.test(input.getStackInSlot(slot)))
                        .toList())
            .toList();
    return FletchingSlotMatcher.assign(candidateSlots, occupiedSlots);
  }

  @Override
  public ItemStack craft(
      final FletchingRecipeInput input, final RegistryWrapper.WrapperLookup registries) {
    return result.copy();
  }

  @Override
  public boolean fits(final int width, final int height) {
    return true;
  }

  @Override
  public ItemStack getResult(final RegistryWrapper.WrapperLookup registries) {
    return result;
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public DefaultedList<Ingredient> getIngredients() {
    final DefaultedList<Ingredient> ingredients =
        DefaultedList.ofSize(inputs.size(), Ingredient.EMPTY);
    for (int index = 0; index < inputs.size(); index++) {
      ingredients.set(index, inputs.get(index).ingredient());
    }
    return ingredients;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.FLETCHING_SERIALIZER;
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.FLETCHING;
  }

  private static List<Integer> occupiedSlots(final FletchingRecipeInput input) {
    final List<Integer> occupied = new ArrayList<>();
    for (int slot = 0; slot < input.getSize(); slot++) {
      if (!input.getStackInSlot(slot).isEmpty()) {
        occupied.add(slot);
      }
    }
    return occupied;
  }
}
