package com.grahambartley.morearrows.compat.jei;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.compat.info.FletchingRecipeLayout;
import com.grahambartley.morearrows.fletching.FletchingStationInteraction;
import com.grahambartley.morearrows.recipe.FletchingIngredient;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class FletchingJeiCategory implements IRecipeCategory<RecipeEntry<FletchingRecipe>> {

  @SuppressWarnings("unchecked")
  public static final RecipeType<RecipeEntry<FletchingRecipe>> RECIPE_TYPE =
      RecipeType.create(
          MoreArrows.MOD_ID,
          "fletching",
          (Class<RecipeEntry<FletchingRecipe>>) (Class<?>) RecipeEntry.class);

  private static final int MAX_INPUTS = FletchingRecipe.MAX_INPUTS;

  private final IDrawable icon;

  public FletchingJeiCategory(final IGuiHelper guiHelper) {
    Objects.requireNonNull(guiHelper, "guiHelper");
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.FLETCHING_TABLE));
  }

  @Override
  public RecipeType<RecipeEntry<FletchingRecipe>> getRecipeType() {
    return RECIPE_TYPE;
  }

  @Override
  public Text getTitle() {
    return Text.translatable(FletchingStationInteraction.TITLE_KEY);
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public int getWidth() {
    return FletchingRecipeLayout.width(MAX_INPUTS);
  }

  @Override
  public int getHeight() {
    return FletchingRecipeLayout.height(MAX_INPUTS);
  }

  @Override
  public Identifier getRegistryName(final RecipeEntry<FletchingRecipe> entry) {
    return entry.id();
  }

  @Override
  public void setRecipe(
      final IRecipeLayoutBuilder builder,
      final RecipeEntry<FletchingRecipe> entry,
      final IFocusGroup focuses) {
    final List<FletchingIngredient> inputs = entry.value().inputs();
    final int count = inputs.size();

    for (int index = 0; index < count; index++) {
      builder
          .addInputSlot(
              FletchingRecipeLayout.inputX(index), FletchingRecipeLayout.inputY(index, count))
          .addItemStacks(stacksOf(inputs.get(index)))
          .setStandardSlotBackground();
    }
    builder
        .addOutputSlot(FletchingRecipeLayout.outputX(count), FletchingRecipeLayout.outputY(count))
        .addItemStack(entry.value().result())
        .setOutputSlotBackground();
  }

  private static List<ItemStack> stacksOf(final FletchingIngredient input) {
    return Arrays.stream(input.ingredient().getMatchingStacks())
        .map(stack -> stack.copyWithCount(input.count()))
        .toList();
  }
}
