package com.grahambartley.notenougharrows.compat.emi;

import com.grahambartley.notenougharrows.ModRecipes;
import com.grahambartley.notenougharrows.fletching.FletchingStationInteraction;
import com.grahambartley.notenougharrows.recipe.StationRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;

public final class EmiStationRecipes implements EmiStationRegistrar {

  private static final EmiRecipeCategory CATEGORY =
      new EmiRecipeCategory(ModRecipes.FLETCHING_ID, EmiStack.of(Blocks.FLETCHING_TABLE)) {
        @Override
        public Text getName() {
          return Text.translatable(FletchingStationInteraction.TITLE_KEY);
        }
      };

  @Override
  public void register(final EmiRegistry registry) {
    registry.addCategory(CATEGORY);
    registry.addWorkstation(CATEGORY, EmiStack.of(Blocks.FLETCHING_TABLE));
    StationRecipes.from(registry.getRecipeManager()).stream()
        .map(entry -> new FletchingEmiRecipe(CATEGORY, entry))
        .forEach(registry::addRecipe);
  }
}
