package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.control.TargetGate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityTargetMixin {

  @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
  private void notEnoughArrows$holdTheTargetAControlArrowChose(
      final LivingEntity target, final CallbackInfo ci) {
    if (!TargetGate.permits((MobEntity) (Object) this, target)) {
      ci.cancel();
    }
  }
}
