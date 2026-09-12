package com.grahambartley.notenougharrows.hud;

import com.grahambartley.notenougharrows.client.state.ClientStateService;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class CountdownRingRenderer {
  private static final float OUTER_RADIUS = 0.22f;
  private static final float INNER_RADIUS = 0.14f;
  private static final float BESIDE_OFFSET = 0.42f;
  private static final float ABOVE_OFFSET = 0.18f;

  private static final int TRACK_COLOR = 0x66000000;
  private static final int ARC_COLOR = 0xFFE0C060;
  private static final int URGENT_ARC_COLOR = 0xFFFF5A5A;

  private CountdownRingRenderer() {}

  public static void register() {
    WorldRenderEvents.AFTER_ENTITIES.register(
        context -> {
          final MinecraftClient client = MinecraftClient.getInstance();
          if (!ClientStateService.get().showCountdownRing() || client.world == null) {
            return;
          }

          final float tickDelta = context.tickCounter().getTickDelta(false);
          final Camera camera = context.camera();
          final Vec3d eye = camera.getPos();
          final Vec3d look = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());

          boolean drewAny = false;
          for (final Countdown countdown : CountdownSync.burning()) {
            final Entity carrier = client.world.getEntityById(countdown.carrierId());
            if (carrier == null) {
              continue;
            }

            final Vec3d center = carrier.getLerpedPos(tickDelta).add(0.0, ABOVE_OFFSET, 0.0);
            if (!CountdownGaze.isLookingAt(eye, look, center)) {
              continue;
            }

            drawRing(
                context.matrixStack(), context.consumers(), camera, center, countdown, tickDelta);
            drewAny = true;
          }

          if (drewAny
              && context.consumers() instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw(RenderLayer.getDebugQuads());
          }
        });
  }

  private static void drawRing(
      final MatrixStack matrices,
      final VertexConsumerProvider consumers,
      final Camera camera,
      final Vec3d center,
      final Countdown countdown,
      final float tickDelta) {
    final Vec3d cameraPos = camera.getPos();
    final float scale = ClientStateService.get().countdownRingScale();

    matrices.push();
    matrices.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);
    matrices.multiply(camera.getRotation());
    matrices.scale(scale, scale, scale);
    matrices.translate(BESIDE_OFFSET, 0.0f, 0.0f);

    final Matrix4f matrix = matrices.peek().getPositionMatrix();
    final VertexConsumer consumer = consumers.getBuffer(RenderLayer.getDebugQuads());

    arc(consumer, matrix, CountdownArc.FULL_SWEEP, TRACK_COLOR);

    final float fraction =
        CountdownArc.fractionLeft(countdown.remainingTicks(), countdown.delayTicks(), tickDelta);
    final boolean urgent = CountdownArc.isUrgent(countdown.remainingTicks(), tickDelta);
    arc(
        consumer,
        matrix,
        CountdownArc.sweepRadians(fraction),
        urgent ? URGENT_ARC_COLOR : ARC_COLOR);

    matrices.pop();
  }

  private static void arc(
      final VertexConsumer consumer,
      final Matrix4f matrix,
      final float sweepRadians,
      final int color) {
    final int segments = CountdownArc.segmentsFor(sweepRadians);
    for (int segment = 0; segment < segments; segment++) {
      final float from = segment * CountdownArc.SEGMENT_RADIANS;
      final float to = CountdownArc.segmentEnd(segment, sweepRadians);

      vertex(consumer, matrix, from, INNER_RADIUS, color);
      vertex(consumer, matrix, from, OUTER_RADIUS, color);
      vertex(consumer, matrix, to, OUTER_RADIUS, color);
      vertex(consumer, matrix, to, INNER_RADIUS, color);
    }
  }

  private static void vertex(
      final VertexConsumer consumer,
      final Matrix4f matrix,
      final float angle,
      final float radius,
      final int color) {
    consumer
        .vertex(matrix, (float) Math.sin(angle) * radius, (float) Math.cos(angle) * radius, 0.0f)
        .color(color);
  }
}
