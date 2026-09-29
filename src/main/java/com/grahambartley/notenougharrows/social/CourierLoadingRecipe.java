package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class CourierLoadingRecipe extends SpecialCraftingRecipe {
  private static final int LOADING_STACKS = 2;
  private static final int ONE_ARROW = 1;

  public CourierLoadingRecipe(final CraftingRecipeCategory category) {
    super(category);
  }

  @Override
  public boolean matches(final CraftingRecipeInput input, final World world) {
    return loading(input).isPresent();
  }

  @Override
  public ItemStack craft(
      final CraftingRecipeInput input, final RegistryWrapper.WrapperLookup registries) {
    return loading(input)
        .map(
            loading ->
                CourierPayloads.loaded(
                    loading.arrow(),
                    loading.payload().copyWithCount(consumedFrom(loading.payload()))))
        .orElse(ItemStack.EMPTY);
  }

  public int consumedFrom(final ItemStack stack) {
    return CourierPayloads.isCourier(stack)
        ? ONE_ARROW
        : CourierRefusals.loadableCount(stack, config());
  }

  @Override
  public boolean fits(final int width, final int height) {
    return width * height >= LOADING_STACKS;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.COURIER_LOADING_SERIALIZER;
  }

  private Optional<Loading> loading(final CraftingRecipeInput input) {
    final List<ItemStack> stacks = input.getStacks().stream().filter(it -> !it.isEmpty()).toList();
    if (stacks.size() != LOADING_STACKS) {
      return Optional.empty();
    }
    final ItemStack first = stacks.get(0);
    final ItemStack second = stacks.get(1);
    final ItemStack arrow = CourierPayloads.isEmptyCourier(first) ? first : second;
    final ItemStack payload = arrow == first ? second : first;
    if (!CourierPayloads.isEmptyCourier(arrow)
        || CourierRefusals.refusesToCarry(payload, config())) {
      return Optional.empty();
    }
    return Optional.of(new Loading(arrow, payload));
  }

  private static CourierArrowConfig config() {
    return ServerConfigService.get().social().courier();
  }

  private record Loading(ItemStack arrow, ItemStack payload) {}
}
