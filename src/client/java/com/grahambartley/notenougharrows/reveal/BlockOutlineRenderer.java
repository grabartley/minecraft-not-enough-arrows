package com.grahambartley.notenougharrows.reveal;

import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BlockOutlineRenderer {
  private static final int COLOR = 0xF0FFD24A;
  private static final float HALF_WIDTH = 0.025f;
  private static final double INSET = 0.002;

  private BlockOutlineRenderer() {}

  public static void register() {
    WorldRenderEvents.LAST.register(
        context -> {
          final List<List<BlockPos>> outlines = RevealSync.outlines();
          if (outlines.isEmpty()) {
            return;
          }
          final Vec3d eye = context.camera().getPos();
          RevealPass.draw(
              context.positionMatrix(),
              true,
              builder ->
                  outlines.forEach(blocks -> blocks.forEach(pos -> edges(builder, pos, eye))));
        });
  }

  private static void edges(final VertexConsumer consumer, final BlockPos pos, final Vec3d eye) {
    final double minX = pos.getX() - INSET;
    final double minY = pos.getY() - INSET;
    final double minZ = pos.getZ() - INSET;
    final double maxX = pos.getX() + 1 + INSET;
    final double maxY = pos.getY() + 1 + INSET;
    final double maxZ = pos.getZ() + 1 + INSET;
    for (final double y : new double[] {minY, maxY}) {
      edge(consumer, minX, y, minZ, maxX, y, minZ, eye);
      edge(consumer, maxX, y, minZ, maxX, y, maxZ, eye);
      edge(consumer, maxX, y, maxZ, minX, y, maxZ, eye);
      edge(consumer, minX, y, maxZ, minX, y, minZ, eye);
    }
    for (final double x : new double[] {minX, maxX}) {
      for (final double z : new double[] {minZ, maxZ}) {
        edge(consumer, x, minY, z, x, maxY, z, eye);
      }
    }
  }

  private static void edge(
      final VertexConsumer consumer,
      final double fromX,
      final double fromY,
      final double fromZ,
      final double toX,
      final double toY,
      final double toZ,
      final Vec3d eye) {
    Ribbons.segment(
        consumer, new Vec3d(fromX, fromY, fromZ), new Vec3d(toX, toY, toZ), eye, HALF_WIDTH, COLOR);
  }
}
