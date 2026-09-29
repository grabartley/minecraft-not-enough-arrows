package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.social.CourierCrafting;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {

  @WrapOperation(
      method = "onTakeItem",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/inventory/RecipeInputInventory;removeStack(II)Lnet/minecraft/item/ItemStack;"))
  private ItemStack notEnoughArrows$takeTheWholeCourierPayload(
      final RecipeInputInventory grid,
      final int slot,
      final int amount,
      final Operation<ItemStack> original) {
    final int taken =
        ((Slot) (Object) this).inventory instanceof RecipeUnlocker result
            ? CourierCrafting.takenFromGrid(result.getLastRecipe(), grid.getStack(slot), amount)
            : amount;
    return original.call(grid, slot, taken);
  }
}
