package com.grahambartley.notenougharrows.server;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.RecipeEntry;

public final class RecipeSync {
  private RecipeSync() {}

  public static Packet<?> forConnection(final Packet<?> packet, final BooleanSupplier hasTheMod) {
    if (!(packet instanceof SynchronizeRecipesS2CPacket sync) || hasTheMod.getAsBoolean()) {
      return packet;
    }
    return new SynchronizeRecipesS2CPacket(withoutModRecipes(sync.getRecipes()));
  }

  private static List<RecipeEntry<?>> withoutModRecipes(final Collection<RecipeEntry<?>> recipes) {
    return recipes.stream().filter(recipe -> !isModRecipe(recipe)).toList();
  }

  public static boolean isModRecipe(final RecipeEntry<?> recipe) {
    return recipe.id().getNamespace().equals(NotEnoughArrows.MOD_ID);
  }
}
