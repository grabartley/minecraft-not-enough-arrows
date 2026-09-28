package com.grahambartley.notenougharrows.render;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import com.grahambartley.notenougharrows.entity.TintedArrowEntity;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public final class ArrowRendererRegistrar {
  private static final String TEXTURE_DIRECTORY = "textures/entity/arrow/";
  private static final String TINT_SUFFIX = "_tint";

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

  public static Identifier tintTextureFor(final Identifier arrowId) {
    return Identifier.of(
        arrowId.getNamespace(), TEXTURE_DIRECTORY + arrowId.getPath() + TINT_SUFFIX + ".png");
  }

  private static <E extends BaseArrowEntity> void register(final RegisteredArrow<E> arrow) {
    final Identifier texture = textureFor(arrow.id());
    if (arrow.item() instanceof TintedArrowItem) {
      registerTinted(arrow, texture);
      return;
    }
    EntityRendererRegistry.register(
        arrow.entityType(), context -> new BaseArrowEntityRenderer<>(context, texture));
  }

  @SuppressWarnings("unchecked")
  private static <E extends TintedArrowEntity> void registerTinted(
      final RegisteredArrow<?> arrow, final Identifier texture) {
    final Identifier tintTexture = tintTextureFor(arrow.id());
    EntityRendererRegistry.register(
        (EntityType<E>) arrow.entityType(),
        context -> new TintedArrowEntityRenderer<>(context, texture, tintTexture));
  }
}
