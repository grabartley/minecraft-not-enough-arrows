package com.grahambartley.morearrows.mixin.client;

import com.grahambartley.morearrows.render.NockedArrowRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

  @Inject(
      method =
          "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;"
              + "Lnet/minecraft/client/render/model/json/ModelTransformationMode;Z"
              + "Lnet/minecraft/client/util/math/MatrixStack;"
              + "Lnet/minecraft/client/render/VertexConsumerProvider;"
              + "Lnet/minecraft/world/World;III)V",
      at = @At("TAIL"))
  private void moreArrows$renderNockedArrow(
      final LivingEntity holder,
      final ItemStack weapon,
      final ModelTransformationMode mode,
      final boolean leftHanded,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final World world,
      final int light,
      final int overlay,
      final int seed,
      final CallbackInfo ci) {
    NockedArrowRenderer.render(
        (ItemRenderer) (Object) this,
        holder,
        weapon,
        mode,
        leftHanded,
        matrices,
        vertexConsumers,
        world,
        light,
        overlay,
        seed);
  }
}
