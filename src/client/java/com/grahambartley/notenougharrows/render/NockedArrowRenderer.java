package com.grahambartley.notenougharrows.render;

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
      @Nullable final World world,
      final int seed,
      final NockOverlayTarget target) {
    NockedArrowLookup.drawnOn(weapon, holder)
        .ifPresent(
            nocked ->
                draw(
                    itemRenderer,
                    nocked,
                    itemRenderer.getModel(weapon, world, holder, seed),
                    itemRenderer.getModel(nocked.arrow(), world, holder, seed),
                    target));
  }

  public static void renderChargedCrossbow(
      final ItemRenderer itemRenderer,
      final ItemStack weapon,
      final BakedModel weaponModel,
      final NockOverlayTarget target) {
    NockedArrowLookup.chargedInto(weapon)
        .ifPresent(
            nocked ->
                draw(
                    itemRenderer,
                    nocked,
                    weaponModel,
                    itemRenderer.getModels().getModel(nocked.arrow()),
                    target));
  }

  private static void draw(
      final ItemRenderer itemRenderer,
      final NockedArrow nocked,
      final BakedModel weaponModel,
      final BakedModel arrowModel,
      final NockOverlayTarget target) {
    final MatrixStack matrices = target.matrices();

    matrices.push();
    weaponModel
        .getTransformation()
        .getTransformation(target.mode())
        .apply(target.leftHanded(), matrices);
    nocked.placement().applyTo(matrices);
    itemRenderer.renderItem(
        nocked.arrow(),
        ModelTransformationMode.NONE,
        target.leftHanded(),
        matrices,
        target.vertexConsumers(),
        target.light(),
        target.overlay(),
        arrowModel);
    matrices.pop();
  }
}
