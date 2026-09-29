package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CourierLoadingRecipe extends SpecialCraftingRecipe {
  private static final int ONE_ARROW = 1;

  public CourierLoadingRecipe(final CraftingRecipeCategory category) {
    super(category);
  }

  @Override
  public boolean matches(final CraftingRecipeInput input, final World world) {
    return CourierLoad.of(input.getStacks(), config()).isPresent();
  }

  @Override
  public ItemStack craft(
      final CraftingRecipeInput input, final RegistryWrapper.WrapperLookup registries) {
    return CourierLoad.of(input.getStacks(), config())
        .map(CourierLoad::loaded)
        .orElse(ItemStack.EMPTY);
  }

  public int consumedFrom(final ItemStack stack) {
    return CourierPayloads.isCourier(stack)
        ? ONE_ARROW
        : CourierRefusals.loadableCount(stack, config());
  }

  @Override
  public DefaultedList<ItemStack> getRemainder(final CraftingRecipeInput input) {
    return DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);
  }

  @Override
  public boolean fits(final int width, final int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.COURIER_LOADING_SERIALIZER;
  }

  private static CourierArrowConfig config() {
    return ServerConfigService.get().social().courier();
  }
}
