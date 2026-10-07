package com.grahambartley.notenougharrows.mixin.client;

import com.grahambartley.notenougharrows.ModEntities;
import com.grahambartley.notenougharrows.sound.ShockBoltSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin {

  @ModifyArg(
      method = "tick()V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"),
      index = 3,
      require = 2,
      allow = 2)
  private SoundEvent notEnoughArrows$voiceShockBolt(final SoundEvent sound) {
    return ShockBoltSounds.voice(isShockBolt(), sound);
  }

  private boolean isShockBolt() {
    return ((Entity) (Object) this).getType() == ModEntities.SHOCK_BOLT;
  }
}
