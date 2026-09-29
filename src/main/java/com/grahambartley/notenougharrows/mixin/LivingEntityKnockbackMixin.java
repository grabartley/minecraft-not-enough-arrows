package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.chaos.PufferInflation;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityKnockbackMixin {

  @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private double notEnoughArrows$knockAnInflatedTargetFurther(final double strength) {
    return PufferInflation.knockbackFor((LivingEntity) (Object) this, strength);
  }
}
