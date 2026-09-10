package com.grahambartley.morearrows.mixin;

import com.grahambartley.morearrows.fletching.FletchingStationInteraction;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FletchingTableBlock.class)
public abstract class FletchingTableBlockMixin {

  @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
  private void openFletchingStation(
      final BlockState state,
      final World world,
      final BlockPos pos,
      final PlayerEntity player,
      final BlockHitResult hit,
      final CallbackInfoReturnable<ActionResult> info) {
    final ActionResult result = FletchingStationInteraction.use(world, pos, player);
    if (result != ActionResult.PASS) {
      info.setReturnValue(result);
    }
  }
}
