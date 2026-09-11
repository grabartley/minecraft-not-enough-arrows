package com.grahambartley.morearrows.compat.emi;

import com.grahambartley.morearrows.ModRecipes;
import com.grahambartley.morearrows.fletching.FletchingStationInteraction;
import com.grahambartley.morearrows.recipe.StationRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;

public final class EmiStationRecipes implements EmiStationRegistrar {

  private final EmiRecipeCategory category =
      new EmiRecipeCategory(ModRecipes.FLETCHING_ID, EmiStack.of(Blocks.FLETCHING_TABLE)) {
        @Override
        public Text getName() {
          return Text.translatable(FletchingStationInteraction.TITLE_KEY);
        }
      };

  @Override
  public void register(final EmiRegistry registry) {
    registry.addCategory(category);
    registry.addWorkstation(category, EmiStack.of(Blocks.FLETCHING_TABLE));
    StationRecipes.from(registry.getRecipeManager()).stream()
        .map(entry -> new FletchingEmiRecipe(category, entry))
        .forEach(registry::addRecipe);
  }
}
