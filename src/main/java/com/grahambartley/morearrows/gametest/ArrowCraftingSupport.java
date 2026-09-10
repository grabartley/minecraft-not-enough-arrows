package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.MoreArrows;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

final class ArrowCraftingSupport {
  static final Identifier ROPE_ARROW_RECIPE_ID = Identifier.of(MoreArrows.MOD_ID, "rope_arrow");

  private static final int GRID_WIDTH = 3;
  private static final int GRID_HEIGHT = 3;
  private static final int CENTRE_SLOT = 4;

  private ArrowCraftingSupport() {}

  static boolean ropeArrowStillCrafts(final ServerWorld world) {
    return world
        .getRecipeManager()
        .getFirstMatch(RecipeType.CRAFTING, ropeArrowGrid(), world)
        .filter(entry -> entry.id().equals(ROPE_ARROW_RECIPE_ID))
        .isPresent();
  }

  private static CraftingRecipeInput ropeArrowGrid() {
    final List<ItemStack> stacks = new ArrayList<>(GRID_WIDTH * GRID_HEIGHT);
    for (int slot = 0; slot < GRID_WIDTH * GRID_HEIGHT; slot++) {
      stacks.add(new ItemStack(slot == CENTRE_SLOT ? Items.LEAD : Items.ARROW));
    }
    return CraftingRecipeInput.create(GRID_WIDTH, GRID_HEIGHT, stacks);
  }
}
