package com.grahambartley.notenougharrows.reveal;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;

public final class Ribbons {

  private Ribbons() {}

  public static void segment(
      final VertexConsumer consumer,
      final Vec3d from,
      final Vec3d to,
      final Vec3d eye,
      final float halfWidth,
      final int color) {
    final Vec3d side = side(from, to, eye, halfWidth);
    if (side.lengthSquared() == 0.0) {
      return;
    }
    vertex(consumer, from.add(side), eye, color);
    vertex(consumer, from.subtract(side), eye, color);
    vertex(consumer, to.subtract(side), eye, color);
    vertex(consumer, to.add(side), eye, color);
  }

  public static Vec3d side(
      final Vec3d from, final Vec3d to, final Vec3d eye, final double halfWidth) {
    final Vec3d across = to.subtract(from).crossProduct(eye.subtract(from));
    final double length = across.length();
    return length < 1.0E-9 ? Vec3d.ZERO : across.multiply(halfWidth / length);
  }

  private static void vertex(
      final VertexConsumer consumer, final Vec3d at, final Vec3d eye, final int color) {
    consumer
        .vertex((float) (at.x - eye.x), (float) (at.y - eye.y), (float) (at.z - eye.z))
        .color(color);
  }
}
