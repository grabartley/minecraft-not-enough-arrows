package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.entity.CourierArrowEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityDeliveryMixin {

  @WrapOperation(
      method = "tick",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/player/PlayerEntity;shouldDamagePlayer(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
  private boolean notEnoughArrows$deliverToAPlayerEvenWithoutPvp(
      final PlayerEntity shooter, final PlayerEntity struck, final Operation<Boolean> original) {
    return CourierArrowEntity.reachesEveryPlayer((PersistentProjectileEntity) (Object) this)
        || original.call(shooter, struck);
  }
}
