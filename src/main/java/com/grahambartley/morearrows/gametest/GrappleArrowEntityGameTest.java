package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.grapple.GrappleSession;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class GrappleArrowEntityGameTest implements FabricGameTest {
  private static final String TEMPLATE = "more-arrows:fire_pad";

  private static final BlockPos SHOOTER_STAND = new BlockPos(0, 3, 0);
  private static final BlockPos ARROW_START = new BlockPos(5, 5, 5);
  private static final BlockPos EXPECTED_ANCHOR = new BlockPos(5, 2, 5);
  private static final Vec3d DOWNWARD = new Vec3d(0.0, -0.6, 0.0);
  private static final int LANDING_TICK = 20;

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 60)
  public void anArrowThatLandsInABlockGrapplesItsShooterToThatBlock(TestContext context) {
    final ServerPlayerEntity shooter = GrappleTestSupport.playerAt(context, SHOOTER_STAND);
    final GrappleArrowEntity arrow = arrowAt(context, ARROW_START);
    arrow.setOwner(shooter);

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleSession session =
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid());

          context.assertTrue(
              session != null, "An arrow landing in a block should grapple the player who shot it");
          context.assertEquals(
              session.anchor(),
              context.getAbsolutePos(EXPECTED_ANCHOR),
              "Block the arrow grappled onto");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = GrappleTestSupport.BATCH, tickLimit = 60)
  public void anArrowWithNoPlayerBehindItGrapplesNobody(TestContext context) {
    final PigEntity shooter = context.spawnEntity(EntityType.PIG, SHOOTER_STAND);
    shooter.setAiDisabled(true);
    final GrappleArrowEntity arrow = arrowAt(context, ARROW_START);
    arrow.setOwner(shooter);

    context.runAtTick(
        LANDING_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) == null,
              "An arrow with no player behind it should pull nothing");
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), shooter.getUuid()) == null,
              "An arrow with no player behind it should take no anchor");
          context.complete();
        });
  }

  private static GrappleArrowEntity arrowAt(final TestContext context, final BlockPos relativePos) {
    final GrappleArrowEntity arrow =
        context.spawnEntity(ModArrows.GRAPPLE_ARROW.entityType(), relativePos);
    arrow.setVelocity(DOWNWARD);
    return arrow;
  }
}
