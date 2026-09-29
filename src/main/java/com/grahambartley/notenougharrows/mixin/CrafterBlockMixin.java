package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.social.CourierCrafting;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Optional;
import net.minecraft.block.CrafterBlock;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {

  @ModifyReturnValue(method = "getCraftingRecipe", at = @At("RETURN"))
  private static Optional<RecipeEntry<CraftingRecipe>> notEnoughArrows$neverLoadACourierArrow(
      final Optional<RecipeEntry<CraftingRecipe>> matched) {
    return CourierCrafting.offeredToCrafter(matched);
  }
}
