package com.grahambartley.morearrows.compat.jei;

import com.grahambartley.morearrows.ModRecipes;
import com.grahambartley.morearrows.compat.layout.FletchingRecipeLayout;
import com.grahambartley.morearrows.fletching.FletchingStationInteraction;
import com.grahambartley.morearrows.recipe.FletchingIngredient;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class FletchingJeiCategory implements IRecipeCategory<RecipeEntry<FletchingRecipe>> {

  public static final RecipeType<RecipeEntry<FletchingRecipe>> RECIPE_TYPE =
      RecipeType.createRecipeHolderType(ModRecipes.FLETCHING_ID);

  private static final int BOUNDS_INPUTS = FletchingRecipe.MAX_INPUTS;

  private final IDrawable icon;
  private final IDrawableStatic arrow;

  public FletchingJeiCategory(final IGuiHelper guiHelper) {
    Objects.requireNonNull(guiHelper, "guiHelper");
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.FLETCHING_TABLE));
    this.arrow = guiHelper.getRecipeArrow();
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
    return FletchingRecipeLayout.width(BOUNDS_INPUTS);
  }

  @Override
  public int getHeight() {
    return FletchingRecipeLayout.height(BOUNDS_INPUTS);
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
    final FletchingRecipeLayout layout = layoutFor(inputs.size());

    for (int index = 0; index < inputs.size(); index++) {
      builder
          .addInputSlot(layout.inputX(index), layout.inputY(index))
          .addItemStacks(stacksOf(inputs.get(index)))
          .setStandardSlotBackground();
    }
    builder
        .addOutputSlot(layout.outputX(), layout.outputY())
        .addItemStack(entry.value().result())
        .setOutputSlotBackground();
  }

  @Override
  public void draw(
      final RecipeEntry<FletchingRecipe> entry,
      final IRecipeSlotsView slots,
      final DrawContext context,
      final double mouseX,
      final double mouseY) {
    final FletchingRecipeLayout layout = layoutFor(entry.value().inputs().size());
    arrow.draw(context, layout.arrowX(), layout.arrowY());
  }

  private static FletchingRecipeLayout layoutFor(final int inputCount) {
    return FletchingRecipeLayout.centredIn(inputCount, BOUNDS_INPUTS);
  }

  private static List<ItemStack> stacksOf(final FletchingIngredient input) {
    return Arrays.stream(input.ingredient().getMatchingStacks())
        .map(stack -> stack.copyWithCount(input.count()))
        .toList();
  }
}
