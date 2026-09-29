package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.disguise.DisguiseService;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityDisguiseMixin {
  private static final int DEFUSED = -1;

  @ModifyArg(
      method = "tick",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/entity/mob/CreeperEntity;setFuseSpeed(I)V"),
      index = 0)
  private int notEnoughArrows$holdALitDisguisedCreepersFuse(final int fuseSpeed) {
    return DisguiseService.isDisguised((CreeperEntity) (Object) this) ? DEFUSED : fuseSpeed;
  }
}
