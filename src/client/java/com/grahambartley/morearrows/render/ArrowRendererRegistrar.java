package com.grahambartley.morearrows.render;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.entity.BaseArrowEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;

public final class ArrowRendererRegistrar {
  private static final String TEXTURE_DIRECTORY = "textures/entity/arrow/";

  private ArrowRendererRegistrar() {}

  public static void registerAll() {
    final Identifier grappleArrowId = ModArrows.GRAPPLE_ARROW.id();
    EntityRendererRegistry.register(
        ModArrows.GRAPPLE_ARROW.entityType(),
        context -> new GrappleArrowEntityRenderer(context, textureFor(grappleArrowId)));

    ModArrows.registered().stream()
        .filter(arrow -> !arrow.id().equals(grappleArrowId))
        .forEach(ArrowRendererRegistrar::register);
  }

  public static Identifier textureFor(final Identifier arrowId) {
    return Identifier.of(arrowId.getNamespace(), TEXTURE_DIRECTORY + arrowId.getPath() + ".png");
  }

  private static <E extends BaseArrowEntity> void register(final RegisteredArrow<E> arrow) {
    final Identifier texture = textureFor(arrow.id());
    EntityRendererRegistry.register(
        arrow.entityType(), context -> new BaseArrowEntityRenderer<>(context, texture));
  }
}
