package com.grahambartley.notenougharrows.reveal;

import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.util.math.Vec3d;

public final class BlockOutlineRenderer {
  private static final int COLOR = 0xF0FFD24A;
  private static final float HALF_WIDTH = 0.025f;

  private BlockOutlineRenderer() {}

  public static void register() {
    WorldRenderEvents.LAST.register(
        context -> {
          final List<List<BlockEdges.Edge>> outlines = RevealSync.outlines();
          if (outlines.isEmpty()) {
            return;
          }
          final Vec3d eye = context.camera().getPos();
          RevealPass.draw(
              context.positionMatrix(),
              true,
              builder ->
                  outlines.forEach(
                      edges ->
                          edges.forEach(
                              edge ->
                                  Ribbons.segment(
                                      builder, edge.from(), edge.to(), eye, HALF_WIDTH, COLOR))));
        });
  }
}
