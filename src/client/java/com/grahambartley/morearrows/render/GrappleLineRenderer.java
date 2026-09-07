package com.grahambartley.morearrows.render;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class GrappleLineRenderer {
  private static final int SEGMENTS = 24;
  private static final float THICKNESS = 0.025f;
  private static final float RED = 0.5f;
  private static final float GREEN = 0.4f;
  private static final float BLUE = 0.3f;
  private static final float SHADED = 0.7f;

  private GrappleLineRenderer() {}

  public static void render(
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final Vec3d toward,
      final int lightAtArrow,
      final int lightAtPlayer) {
    matrices.push();
    final VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLeash());
    final Matrix4f matrix = matrices.peek().getPositionMatrix();

    final float dx = (float) toward.x;
    final float dy = (float) toward.y;
    final float dz = (float) toward.z;
    final float spread = MathHelper.inverseSqrt(dx * dx + dz * dz) * THICKNESS / 2.0f;
    final float acrossX = dz * spread;
    final float acrossZ = dx * spread;

    for (int segment = 0; segment <= SEGMENTS; segment++) {
      segment(
          buffer,
          matrix,
          dx,
          dy,
          dz,
          lightAtArrow,
          lightAtPlayer,
          THICKNESS,
          0.0f,
          acrossX,
          acrossZ,
          segment,
          false);
    }
    for (int segment = SEGMENTS; segment >= 0; segment--) {
      segment(
          buffer,
          matrix,
          dx,
          dy,
          dz,
          lightAtArrow,
          lightAtPlayer,
          0.0f,
          THICKNESS,
          acrossX,
          acrossZ,
          segment,
          true);
    }
    matrices.pop();
  }

  private static void segment(
      final VertexConsumer buffer,
      final Matrix4f matrix,
      final float dx,
      final float dy,
      final float dz,
      final int lightAtArrow,
      final int lightAtPlayer,
      final float top,
      final float bottom,
      final float acrossX,
      final float acrossZ,
      final int segment,
      final boolean underside) {
    final float along = (float) segment / SEGMENTS;
    final int light =
        LightmapTextureManager.pack(
            (int)
                MathHelper.lerp(
                    along,
                    LightmapTextureManager.getBlockLightCoordinates(lightAtArrow),
                    LightmapTextureManager.getBlockLightCoordinates(lightAtPlayer)),
            (int)
                MathHelper.lerp(
                    along,
                    LightmapTextureManager.getSkyLightCoordinates(lightAtArrow),
                    LightmapTextureManager.getSkyLightCoordinates(lightAtPlayer)));
    final float shade = segment % 2 == (underside ? 1 : 0) ? SHADED : 1.0f;
    final float x = dx * along;
    final float y = dy > 0.0f ? dy * along * along : dy - dy * (1.0f - along) * (1.0f - along);
    final float z = dz * along;

    buffer
        .vertex(matrix, x - acrossX, y + bottom, z + acrossZ)
        .color(RED * shade, GREEN * shade, BLUE * shade, 1.0f)
        .light(light);
    buffer
        .vertex(matrix, x + acrossX, y + top - bottom, z - acrossZ)
        .color(RED * shade, GREEN * shade, BLUE * shade, 1.0f)
        .light(light);
  }
}
