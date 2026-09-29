package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.control.SlimeSteering;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$RandomLookGoal")
public abstract class SlimeRandomLookGoalMixin {
  @Shadow @Final private SlimeEntity slime;

  @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
  private void notEnoughArrows$keepTheHeadingAControlArrowChose(
      final CallbackInfoReturnable<Boolean> cir) {
    if (SlimeSteering.isSteered(slime.getUuid(), slime.getWorld().getTime())) {
      cir.setReturnValue(false);
    }
  }
}
