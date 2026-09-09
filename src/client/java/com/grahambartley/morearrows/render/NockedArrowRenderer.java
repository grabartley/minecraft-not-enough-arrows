package com.grahambartley.morearrows.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class NockedArrowRenderer {

  private NockedArrowRenderer() {}

  public static void renderDrawnBow(
      final ItemRenderer itemRenderer,
      @Nullable final LivingEntity holder,
      final ItemStack weapon,
      final ModelTransformationMode mode,
      final boolean leftHanded,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      @Nullable final World world,
      final int light,
      final int overlay,
      final int seed) {
    NockedArrowLookup.drawnOn(weapon, holder)
        .ifPresent(
            nocked ->
                draw(
                    itemRenderer,
                    nocked,
                    itemRenderer.getModel(weapon, world, holder, seed),
                    itemRenderer.getModel(nocked.arrow(), world, holder, seed),
                    mode,
                    leftHanded,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay));
  }

  public static void renderChargedCrossbow(
      final ItemRenderer itemRenderer,
      final ItemStack weapon,
      final BakedModel weaponModel,
      final ModelTransformationMode mode,
      final boolean leftHanded,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final int light,
      final int overlay) {
    NockedArrowLookup.chargedInto(weapon)
        .ifPresent(
            nocked ->
                draw(
                    itemRenderer,
                    nocked,
                    weaponModel,
                    itemRenderer.getModels().getModel(nocked.arrow()),
                    mode,
                    leftHanded,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay));
  }

  private static void draw(
      final ItemRenderer itemRenderer,
      final NockedArrow nocked,
      final BakedModel weaponModel,
      final BakedModel arrowModel,
      final ModelTransformationMode mode,
      final boolean leftHanded,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final int light,
      final int overlay) {
    matrices.push();
    weaponModel.getTransformation().getTransformation(mode).apply(leftHanded, matrices);
    nocked.placement().applyTo(matrices);
    itemRenderer.renderItem(
        nocked.arrow(),
        ModelTransformationMode.NONE,
        leftHanded,
        matrices,
        vertexConsumers,
        light,
        overlay,
        arrowModel);
    matrices.pop();
  }
}
