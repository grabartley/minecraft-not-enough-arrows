package com.grahambartley.notenougharrows.mixin.client;

import com.grahambartley.notenougharrows.render.RidingPlayers;
import com.grahambartley.notenougharrows.render.ZiplineRidePose;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin {

  @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
  private void notEnoughArrows$hangFromTheZipline(
      final LivingEntity entity,
      final float limbAngle,
      final float limbDistance,
      final float animationProgress,
      final float headYaw,
      final float headPitch,
      final CallbackInfo ci) {
    if (!RidingPlayers.isRiding(entity.getId())) {
      return;
    }
    final PlayerEntityModel<?> model = (PlayerEntityModel<?>) (Object) this;
    final ZiplineRidePose pose = ZiplineRidePose.at(animationProgress);
    model.rightArm.pitch = pose.armPitch();
    model.rightArm.yaw = 0.0f;
    model.rightArm.roll = pose.armRoll();
    model.leftArm.pitch = pose.armPitch();
    model.leftArm.yaw = 0.0f;
    model.leftArm.roll = -pose.armRoll();
    model.rightLeg.pitch = pose.rightLegPitch();
    model.rightLeg.roll = pose.legRoll();
    model.leftLeg.pitch = pose.leftLegPitch();
    model.leftLeg.roll = -pose.legRoll();
    model.rightSleeve.copyTransform(model.rightArm);
    model.leftSleeve.copyTransform(model.leftArm);
    model.rightPants.copyTransform(model.rightLeg);
    model.leftPants.copyTransform(model.leftLeg);
  }
}
