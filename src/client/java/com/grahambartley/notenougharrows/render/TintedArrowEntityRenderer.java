package com.grahambartley.notenougharrows.render;

import com.grahambartley.notenougharrows.entity.TintedArrowEntity;
import java.util.Objects;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class TintedArrowEntityRenderer<E extends TintedArrowEntity>
    extends BaseArrowEntityRenderer<E> {
  private final Identifier tintTexture;
  private boolean drawingTint;
  private int vertexColor = Colors.WHITE;

  public TintedArrowEntityRenderer(
      final EntityRendererFactory.Context context,
      final Identifier texture,
      final Identifier tintTexture) {
    super(context, texture);
    this.tintTexture = Objects.requireNonNull(tintTexture, "tintTexture");
  }

  @Override
  public void render(
      final E entity,
      final float yaw,
      final float tickDelta,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final int light) {
    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    drawingTint = true;
    vertexColor = TintedArrowColors.opaque(entity.tint());
    try {
      super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    } finally {
      drawingTint = false;
      vertexColor = Colors.WHITE;
    }
  }

  @Override
  public Identifier getTexture(final E entity) {
    return drawingTint ? tintTexture : super.getTexture(entity);
  }

  @Override
  protected boolean hasLabel(final E entity) {
    return !drawingTint && super.hasLabel(entity);
  }

  @Override
  public void vertex(
      final MatrixStack.Entry matrix,
      final VertexConsumer vertexConsumer,
      final int x,
      final int y,
      final int z,
      final float u,
      final float v,
      final int normalX,
      final int normalZ,
      final int normalY,
      final int light) {
    vertexConsumer
        .vertex(matrix, (float) x, (float) y, (float) z)
        .color(vertexColor)
        .texture(u, v)
        .overlay(OverlayTexture.DEFAULT_UV)
        .light(light)
        .normal(matrix, normalX, normalY, normalZ);
  }
}
