package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.disguise.DisguiseService;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityDisguiseMixin {

  @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
  private void notEnoughArrows$aDisguisedSlimeDoesNotHurtByTouch(
      final CallbackInfoReturnable<Boolean> cir) {
    if (DisguiseService.isDisguised((SlimeEntity) (Object) this)) {
      cir.setReturnValue(false);
    }
  }
}
