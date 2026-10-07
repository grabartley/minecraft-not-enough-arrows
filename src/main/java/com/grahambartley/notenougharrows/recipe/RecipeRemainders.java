package com.grahambartley.notenougharrows.recipe;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public final class RecipeRemainders {

  private RecipeRemainders() {}

  public static Optional<Item> remainderOf(final Item ingredient) {
    Objects.requireNonNull(ingredient, "ingredient");
    if (ingredient == Items.POWDER_SNOW_BUCKET) {
      return Optional.of(Items.BUCKET);
    }
    return Optional.ofNullable(ingredient.getRecipeRemainder());
  }
}
