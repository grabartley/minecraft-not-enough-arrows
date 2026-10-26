package com.grahambartley.volley.render;

import com.grahambartley.volley.entity.BaseArrowEntity;
import java.util.Objects;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class BaseArrowEntityRenderer<E extends BaseArrowEntity>
    extends ProjectileEntityRenderer<E> {
  private final Identifier texture;

  public BaseArrowEntityRenderer(
      final EntityRendererFactory.Context context, final Identifier texture) {
    super(context);
    this.texture = Objects.requireNonNull(texture, "texture");
  }

  @Override
  public Identifier getTexture(final E entity) {
    return texture;
  }
}
