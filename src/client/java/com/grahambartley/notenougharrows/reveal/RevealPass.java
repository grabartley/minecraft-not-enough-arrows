package com.grahambartley.notenougharrows.reveal;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class RevealPass {

  private RevealPass() {}

  public static void draw(
      final Matrix4f view, final boolean throughTerrain, final Consumer<BufferBuilder> quads) {
    final Matrix4fStack modelView = RenderSystem.getModelViewStack();
    modelView.pushMatrix();
    modelView.identity();
    modelView.mul(view);
    RenderSystem.applyModelViewMatrix();
    if (throughTerrain) {
      RenderSystem.disableDepthTest();
    }
    RenderSystem.disableCull();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);

    final BufferBuilder builder =
        Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    quads.accept(builder);
    final BuiltBuffer built = builder.endNullable();
    if (built != null) {
      BufferRenderer.drawWithGlobalProgram(built);
    }

    RenderSystem.disableBlend();
    RenderSystem.enableCull();
    RenderSystem.enableDepthTest();
    modelView.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }
}
