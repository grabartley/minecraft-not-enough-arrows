package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.DrillArrowConfig;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class DrillTool {

  private DrillTool() {}

  public static ItemStack forTier(final int toolTier) {
    return switch (toolTier) {
      case DrillArrowConfig.TOOL_TIER_WOOD -> new ItemStack(Items.WOODEN_PICKAXE);
      case DrillArrowConfig.TOOL_TIER_STONE -> new ItemStack(Items.STONE_PICKAXE);
      default -> new ItemStack(Items.IRON_PICKAXE);
    };
  }

  public static boolean canHarvest(final ItemStack tool, final BlockState state) {
    return !state.isToolRequired() || tool.isSuitableFor(state);
  }
}
