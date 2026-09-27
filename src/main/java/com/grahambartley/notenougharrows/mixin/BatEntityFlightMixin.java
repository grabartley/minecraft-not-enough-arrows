package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.control.BatFlight;
import net.minecraft.entity.passive.BatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatEntity.class)
public abstract class BatEntityFlightMixin {

  @Inject(method = "mobTick", at = @At("HEAD"), cancellable = true)
  private void notEnoughArrows$flyWhereAControlArrowSends(final CallbackInfo ci) {
    final BatEntity bat = (BatEntity) (Object) this;
    if (BatFlight.isSteered(bat)) {
      BatFlight.flyToward(bat, bat.hangingPosition);
      ci.cancel();
    }
  }
}
