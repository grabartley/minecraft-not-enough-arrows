package com.grahambartley.notenougharrows.mixin.client;

import com.grahambartley.notenougharrows.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin {

  private boolean isShockBolt() {
    return ((Entity) (Object) this).getType() == ModEntities.SHOCK_BOLT;
  }
}
