package com.grahambartley.notenougharrows.reveal;

import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;

public final class TracerPathRenderer {
  private static final int COLOR = 0xE060FFE0;
  private static final float HALF_WIDTH = 0.04f;

  private TracerPathRenderer() {}

  public static void register() {
    WorldRenderEvents.LAST.register(
        context -> {
          final List<List<Vec3d>> paths = RevealSync.paths();
          if (paths.isEmpty()) {
            return;
          }
          final Vec3d eye = context.camera().getPos();
          RevealPass.draw(
              context.positionMatrix(),
              false,
              builder -> paths.forEach(points -> strip(builder, points, eye)));
        });
  }

  private static void strip(
      final VertexConsumer consumer, final List<Vec3d> points, final Vec3d eye) {
    for (int index = 1; index < points.size(); index++) {
      Ribbons.segment(consumer, points.get(index - 1), points.get(index), eye, HALF_WIDTH, COLOR);
    }
  }
}
