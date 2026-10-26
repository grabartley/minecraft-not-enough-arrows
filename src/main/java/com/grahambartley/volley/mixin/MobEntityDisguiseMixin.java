package com.grahambartley.volley.mixin;

import com.grahambartley.volley.disguise.DisguiseService;
import com.grahambartley.volley.disguise.DisguisedBehaviour;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityDisguiseMixin {

  @Inject(method = "tickNewAi", at = @At("HEAD"), cancellable = true)
  private void volley$behaveAsTheDisguise(final CallbackInfo ci) {
    final MobEntity mob = (MobEntity) (Object) this;
    if (DisguiseService.isDisguised(mob)) {
      DisguisedBehaviour.tick(mob);
      ci.cancel();
    }
  }

  @Inject(method = "playAmbientSound", at = @At("HEAD"), cancellable = true)
  private void volley$keepTheDisguiseQuiet(final CallbackInfo ci) {
    if (DisguiseService.isDisguised((MobEntity) (Object) this)) {
      ci.cancel();
    }
  }
}
