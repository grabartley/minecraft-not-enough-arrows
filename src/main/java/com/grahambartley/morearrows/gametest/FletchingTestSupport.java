package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.recipe.FletchingIngredient;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

final class FletchingTestSupport {
  static final int TICK_LIMIT = 10;

  private FletchingTestSupport() {}

  static FletchingRecipe fourArrowsAndOneTntGiveEightArrows() {
    return new FletchingRecipe(
        "",
        List.of(
            new FletchingIngredient(Ingredient.ofItems(Items.ARROW), 4),
            new FletchingIngredient(Ingredient.ofItems(Items.TNT), 1)),
        new ItemStack(Items.ARROW, 8));
  }
}
