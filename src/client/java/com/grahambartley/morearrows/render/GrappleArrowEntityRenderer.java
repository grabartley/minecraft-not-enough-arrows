package com.grahambartley.morearrows.render;

import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import java.util.OptionalInt;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GrappleArrowEntityRenderer extends BaseArrowEntityRenderer<GrappleArrowEntity> {

  public GrappleArrowEntityRenderer(
      final EntityRendererFactory.Context context, final Identifier texture) {
    super(context, texture);
  }

  @Override
  public void render(
      final GrappleArrowEntity arrow,
      final float yaw,
      final float tickDelta,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final int light) {
    super.render(arrow, yaw, tickDelta, matrices, vertexConsumers, light);
    hauledPlayer(arrow)
        .ifPresent(
            player -> renderLine(arrow, player, tickDelta, matrices, vertexConsumers, light));
  }

  @Override
  public boolean shouldRender(
      final GrappleArrowEntity arrow,
      final net.minecraft.client.render.Frustum frustum,
      final double x,
      final double y,
      final double z) {
    return super.shouldRender(arrow, frustum, x, y, z) || arrow.hauledPlayerId().isPresent();
  }

  private java.util.Optional<PlayerEntity> hauledPlayer(final GrappleArrowEntity arrow) {
    final OptionalInt hauled = arrow.hauledPlayerId();
    if (hauled.isEmpty() || arrow.getWorld() == null) {
      return java.util.Optional.empty();
    }
    final Entity entity = arrow.getWorld().getEntityById(hauled.getAsInt());
    return entity instanceof PlayerEntity player
        ? java.util.Optional.of(player)
        : java.util.Optional.empty();
  }

  private void renderLine(
      final GrappleArrowEntity arrow,
      final PlayerEntity player,
      final float tickDelta,
      final MatrixStack matrices,
      final VertexConsumerProvider vertexConsumers,
      final int light) {
    final Vec3d arrowAt = arrow.getLerpedPos(tickDelta);
    final Vec3d holdingAt = handOf(player, tickDelta);
    GrappleLineRenderer.render(
        matrices, vertexConsumers, holdingAt.subtract(arrowAt), light, getLight(player, tickDelta));
  }

  private Vec3d handOf(final PlayerEntity player, final float tickDelta) {
    final float bodyYaw =
        MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw)
            * MathHelper.RADIANS_PER_DEGREE;
    final double sin = Math.sin(bodyYaw);
    final double cos = Math.cos(bodyYaw);
    final double side = player.getMainArm() == net.minecraft.util.Arm.RIGHT ? -0.35 : 0.35;
    return player
        .getLerpedPos(tickDelta)
        .add(side * cos, player.getStandingEyeHeight() - 0.3, side * sin);
  }

  private int getLight(final PlayerEntity player, final float tickDelta) {
    return net.minecraft.client.MinecraftClient.getInstance()
        .getEntityRenderDispatcher()
        .getLight(player, tickDelta);
  }
}
