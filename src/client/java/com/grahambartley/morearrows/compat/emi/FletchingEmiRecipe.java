package com.grahambartley.morearrows.compat.emi;

import com.grahambartley.morearrows.compat.info.FletchingRecipeLayout;
import com.grahambartley.morearrows.recipe.FletchingIngredient;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import java.util.Objects;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public final class FletchingEmiRecipe implements EmiRecipe {

  private final EmiRecipeCategory category;
  private final Identifier id;
  private final List<EmiIngredient> inputs;
  private final EmiStack output;

  public FletchingEmiRecipe(
      final EmiRecipeCategory category, final RecipeEntry<FletchingRecipe> entry) {
    this.category = Objects.requireNonNull(category, "category");
    Objects.requireNonNull(entry, "entry");
    this.id = entry.id();
    this.inputs = entry.value().inputs().stream().map(FletchingEmiRecipe::ingredientOf).toList();
    this.output = EmiStack.of(entry.value().result());
  }

  private static EmiIngredient ingredientOf(final FletchingIngredient input) {
    return EmiIngredient.of(input.ingredient(), input.count());
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return category;
  }

  @Override
  public Identifier getId() {
    return id;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return inputs;
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(output);
  }

  @Override
  public int getDisplayWidth() {
    return FletchingRecipeLayout.width(inputs.size());
  }

  @Override
  public int getDisplayHeight() {
    return FletchingRecipeLayout.height(inputs.size());
  }

  @Override
  public void addWidgets(final WidgetHolder widgets) {
    Objects.requireNonNull(widgets, "widgets");
    final int count = inputs.size();

    for (int index = 0; index < count; index++) {
      widgets.addSlot(
          inputs.get(index),
          FletchingRecipeLayout.inputX(index),
          FletchingRecipeLayout.inputY(index, count));
    }
    widgets.addTexture(
        EmiTexture.EMPTY_ARROW,
        FletchingRecipeLayout.arrowX(count),
        FletchingRecipeLayout.arrowY(count));
    widgets
        .addSlot(output, FletchingRecipeLayout.outputX(count), FletchingRecipeLayout.outputY(count))
        .recipeContext(this);
  }
}
