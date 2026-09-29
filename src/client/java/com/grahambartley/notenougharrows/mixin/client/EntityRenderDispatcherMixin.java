package com.grahambartley.notenougharrows.mixin.client;

import com.grahambartley.notenougharrows.disguise.DisguiseStandIns;
import java.util.Optional;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  private <E extends Entity> void notEnoughArrows$drawTheDisguiseInstead(
      final E entity,
      final double x,
      final double y,
      final double z,
      final float yaw,
      final float tickDelta,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final int light,
      final CallbackInfo ci) {
    final Optional<LivingEntity> standIn = DisguiseStandIns.standInFor(entity);
    if (standIn.isPresent()) {
      ((EntityRenderDispatcher) (Object) this)
          .render(standIn.get(), x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
      ci.cancel();
    }
  }
}
