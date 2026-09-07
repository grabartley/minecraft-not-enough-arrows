package com.grahambartley.morearrows.render;

import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class GrappleArrowEntityRenderer extends BaseArrowEntityRenderer<GrappleArrowEntity> {

  public GrappleArrowEntityRenderer(
      final EntityRendererFactory.Context context, final Identifier texture) {
    super(context, texture);
  }

  @Override
  public boolean shouldRender(
      final GrappleArrowEntity arrow,
      final Frustum frustum,
      final double cameraX,
      final double cameraY,
      final double cameraZ) {
    return super.shouldRender(arrow, frustum, cameraX, cameraY, cameraZ) || arrow.isLeashed();
  }
}
