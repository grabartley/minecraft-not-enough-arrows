package com.grahambartley.volley.compat.jei;

import com.grahambartley.volley.Volley;
import com.grahambartley.volley.compat.info.InfoEntry;
import com.grahambartley.volley.compat.info.RecipeViewerInfo;
import com.grahambartley.volley.item.TintedArrowItem;
import com.grahambartley.volley.item.TintedArrowItems;
import com.grahambartley.volley.recipe.FletchingRecipe;
import com.grahambartley.volley.recipe.StationRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@JeiPlugin
public final class VolleyJeiPlugin implements IModPlugin {

  private static final Identifier PLUGIN_ID = Identifier.of(Volley.MOD_ID, "jei_plugin");

  private static final String[] MOD_ALIASES = {"arrows", "volley", "ma"};

  @Override
  public Identifier getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void registerModInfo(final IModInfoRegistration registration) {
    registration.addModAliases(Volley.MOD_ID, MOD_ALIASES);
  }

  @Override
  public void registerItemSubtypes(final ISubtypeRegistration registration) {
    for (final TintedArrowItem item : TintedArrowItems.registered()) {
      registration.registerSubtypeInterpreter(item, TintedArrowSubtypes.of(item));
    }
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
      registration.addItemStackInfo(
          RecipeViewerInfo.stacksOf(entry.itemIds()), entry.texts().toArray(Text[]::new));
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
      Volley.LOGGER.warn("No world is loaded, so JEI is being given no station recipes");
      return List.of();
    }
    return StationRecipes.from(world.getRecipeManager());
  }
}
