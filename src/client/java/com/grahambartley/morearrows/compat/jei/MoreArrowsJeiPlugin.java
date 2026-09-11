package com.grahambartley.morearrows.compat.jei;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.compat.info.InfoEntry;
import com.grahambartley.morearrows.compat.info.RecipeViewerInfo;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.StationRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@JeiPlugin
public final class MoreArrowsJeiPlugin implements IModPlugin {

  private static final Identifier PLUGIN_ID = Identifier.of(MoreArrows.MOD_ID, "jei_plugin");

  private static final String[] MOD_ALIASES = {"arrows", "morearrows", "ma"};

  @Override
  public Identifier getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void registerModInfo(final IModInfoRegistration registration) {
    registration.addModAliases(MoreArrows.MOD_ID, MOD_ALIASES);
  }

  @Override
  public void registerCategories(final IRecipeCategoryRegistration registration) {
    registration.addRecipeCategories(
        new FletchingJeiCategory(registration.getJeiHelpers().getGuiHelper()));
  }

  @Override
  public void registerRecipes(final IRecipeRegistration registration) {
    registration.addRecipes(FletchingJeiCategory.RECIPE_TYPE, loadedStationRecipes());

    for (final InfoEntry entry : RecipeViewerInfo.arrowEntries()) {
      registration.addItemStackInfo(stacksOf(entry), entry.texts().toArray(Text[]::new));
    }
  }

  @Override
  public void registerRecipeCatalysts(final IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(
        new ItemStack(Blocks.FLETCHING_TABLE), FletchingJeiCategory.RECIPE_TYPE);
  }

  private static List<RecipeEntry<FletchingRecipe>> loadedStationRecipes() {
    final ClientWorld world = MinecraftClient.getInstance().world;
    if (world == null) {
      MoreArrows.LOGGER.warn("No world is loaded, so JEI is being given no station recipes");
      return List.of();
    }
    return StationRecipes.from(world.getRecipeManager());
  }

  private static List<ItemStack> stacksOf(final InfoEntry entry) {
    return entry.itemIds().stream().map(id -> new ItemStack(Registries.ITEM.get(id))).toList();
  }
}
