package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.anchor.AnchorService;
import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.GrappleArrowEntity;
import com.grahambartley.notenougharrows.grapple.GrappleArrowReturn;
import com.grahambartley.notenougharrows.grapple.GrappleFallGuard;
import com.grahambartley.notenougharrows.grapple.GrapplePull;
import com.grahambartley.notenougharrows.grapple.GrappleService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.function.UnaryOperator;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class GrappleEndingGameTest implements FabricGameTest {
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final String BATCH = "grapple-ending";
  private static final String NO_ARROW_RETURN_BATCH = "grapple-ending-no-arrow-return";
  private static final String NO_FALL_CANCEL_BATCH = "grapple-ending-no-fall-cancel";

  private static final BlockPos PLAYER_STAND = new BlockPos(0, 3, 0);
  private static final BlockPos FLOOR_UNDERFOOT = new BlockPos(0, 2, 0);
  private static final BlockPos HIGH_ANCHOR = new BlockPos(5, 5, 5);
  private static final BlockPos ARROW_REST = new BlockPos(2, 4, 2);
  private static final BlockPos CEILING_STAND = new BlockPos(3, 5, 3);
  private static final BlockPos FLOOR_BELOW = new BlockPos(3, 1, 3);

  private static final int SETTLE_TICK = 3;
  private static final int BREAK_TICK = 2;
  private static final int ASSERT_TICK = 6;
  private static final float A_LONG_FALL = 20.0f;
  private static final float NO_FALL = 0.0f;
  private static final double TOLERANCE = 1.0e-4;
  private static final int RAMPED_UP_TICK = 10;
  private static final float A_HURTFUL_FALL = 5.0f;

  @BeforeBatch(batchId = BATCH)
  public void forgetEveryGrappleBeforeBatch(ServerWorld world) {
    forgetEveryGrapple();
  }

  @BeforeBatch(batchId = NO_ARROW_RETURN_BATCH)
  public void forgetEveryGrappleBeforeTheArrowReturnBatch(ServerWorld world) {
    forgetEveryGrapple();
  }

  @BeforeBatch(batchId = NO_FALL_CANCEL_BATCH)
  public void forgetEveryGrappleBeforeTheFallCancellationBatch(ServerWorld world) {
    forgetEveryGrapple();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void arrivingAtTheAnchorHandsTheArrowBackToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final GrappleArrowEntity arrow = plantedArrow(context);
    startGrappleUnderfoot(context, shooter, arrow);

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) == null,
              "A player standing on their anchor should have arrived");
          context.assertTrue(
              arrow.isRemoved(), "An arrow handed back to its shooter should leave the world");
          context.assertTrue(
              holdsAGrappleArrow(shooter),
              "A shooter the grapple carried home should be holding the arrow that carried them");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = NO_ARROW_RETURN_BATCH, tickLimit = 40)
  public void anOperatorWhoTurnsArrowReturnOffLeavesTheArrowWhereItLanded(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final GrappleArrowEntity arrow = plantedArrow(context);
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    ServerConfigHolder.set(grappleConfig(it -> it.withReturnArrowOnArrival(false)));
    startGrappleUnderfoot(context, shooter, arrow);

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          try {
            context.assertFalse(
                arrow.isRemoved(), "An arrow nobody asked back should stay where it landed");
            context.assertFalse(
                holdsAGrappleArrow(shooter),
                "A shooter should be given nothing when arrow return is off");
          } finally {
            ServerConfigHolder.set(previous);
          }
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void arrivingAtTheAnchorCancelsTheFallThePullCaused(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    startGrappleUnderfoot(context, shooter, null);
    shooter.fallDistance = A_LONG_FALL;

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          context.assertEquals(
              shooter.fallDistance, NO_FALL, "Fall a grappled player is still carrying on arrival");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = NO_FALL_CANCEL_BATCH, tickLimit = 40)
  public void anOperatorWhoTurnsFallCancellationOffLeavesTheFallStanding(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    ServerConfigHolder.set(grappleConfig(it -> it.withCancelFallDamageOnArrival(false)));
    startGrappleUnderfoot(context, shooter, null);
    shooter.fallDistance = A_LONG_FALL;

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          try {
            context.assertEquals(
                shooter.fallDistance,
                A_LONG_FALL,
                "Fall a grappled player keeps when the operator turned cancellation off");
            context.assertFalse(
                GrappleFallGuard.spares(shooter.getUuid()),
                "A player the operator left to their fall should not be spared it either");
          } finally {
            ServerConfigHolder.set(previous);
          }
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void losingTheAnchorLeavesThePlayerToTheirFallAndTheArrowWhereItIs(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final GrappleArrowEntity arrow = plantedArrow(context);
    GrappleService.start(
        context.getWorld(), shooter, context.getAbsolutePos(HIGH_ANCHOR), arrow.getUuid());
    shooter.fallDistance = A_LONG_FALL;

    context.runAtTick(BREAK_TICK, () -> context.setBlockState(HIGH_ANCHOR, Blocks.AIR));
    context.runAtTick(
        ASSERT_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) == null,
              "A grapple whose block was broken should stop pulling");
          context.assertEquals(
              shooter.fallDistance,
              A_LONG_FALL,
              "Fall a player drops with when the block was taken out from under their grapple");
          context.assertFalse(
              arrow.isRemoved(),
              "An arrow whose anchor was broken should still be there to pick up");
          context.assertFalse(
              holdsAGrappleArrow(shooter), "A grapple that never arrived should hand nothing back");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPlayerWhoDiesMidPullTakesNoArrowWithThem(TestContext context) {
    context.setBlockState(HIGH_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final GrappleArrowEntity arrow = plantedArrow(context);
    GrappleService.start(
        context.getWorld(), shooter, context.getAbsolutePos(HIGH_ANCHOR), arrow.getUuid());

    context.runAtTick(BREAK_TICK, shooter::kill);
    context.runAtTick(
        ASSERT_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) == null,
              "A player who died should be pulled no further");
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), shooter.getUuid()) == null,
              "A player who died should give up the block their grapple held");
          context.assertFalse(
              arrow.isRemoved(), "A player who died mid-pull should not take the arrow with them");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPullOntoAnAnchorBelowIsHeldToItsDescentCap(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, CEILING_STAND);
    context.assertTrue(
        GrappleService.start(context.getWorld(), shooter, context.getAbsolutePos(FLOOR_BELOW), null)
            != null,
        "A grapple onto the floor below should start a session");

    context.runAtTick(
        RAMPED_UP_TICK,
        () -> {
          final Vec3d pull = shooter.getVelocity();
          context.assertTrue(
              pull.y < 0.0,
              "A player pulled onto an anchor below should be pulled down, was " + pull);
          context.assertTrue(
              pull.y >= -GrapplePull.DESCENT_SPEED_CAP - TOLERANCE,
              "A descent should never outrun its cap, was " + pull);
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void arrivingAtTheAnchorSparesTheLandingStillAhead(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    startGrappleUnderfoot(context, shooter, null);

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          context.assertTrue(
              GrappleFallGuard.spares(shooter.getUuid()),
              "A grapple that carried a player owes them the landing still ahead of them");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aSparedPlayerWhoLandsSafelyIsOwedNothingFurther(TestContext context) {
    final ServerPlayerEntity grounded = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    grounded.setOnGround(true);
    GrappleFallGuard.spare(grounded.getUuid());

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          context.assertFalse(
              GrappleFallGuard.spares(grounded.getUuid()),
              "A player who put their feet down has taken the landing the grapple owed them");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrowFiredInCreativeIsNeverHandedBack(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final GrappleArrowEntity arrow = plantedArrow(context);
    arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;

    context.assertFalse(
        GrappleArrowReturn.toShooter(shooter, arrow),
        "An arrow a player was never going to recover should not be handed back");
    context.assertFalse(
        arrow.isRemoved(), "An arrow nobody may pick up should stay planted where it is");
    context.assertFalse(holdsAGrappleArrow(shooter), "A creative shooter should be given nothing");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrowAlreadyGoneIsNeverHandedBackTwice(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final GrappleArrowEntity arrow = plantedArrow(context);
    arrow.discard();

    context.assertFalse(
        GrappleArrowReturn.toShooter(shooter, arrow),
        "An arrow that has already left the world cannot be handed back again");
    context.assertFalse(
        holdsAGrappleArrow(shooter), "An arrow handed back twice would be an arrow duplicated");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSparedPlayerWalksAwayFromTheFallTheirPullCaused(TestContext context) {
    final PlayerEntity faller = MockPlayerSupport.mortalPlayerAt(context, PLAYER_STAND);
    final float before = faller.getHealth();
    GrappleFallGuard.spare(faller.getUuid());

    faller.damage(context.getWorld().getDamageSources().fall(), A_HURTFUL_FALL);

    context.assertEquals(
        faller.getHealth(), before, "Health a player keeps after the fall their grapple caused");
    context.assertFalse(
        GrappleFallGuard.spares(faller.getUuid()),
        "A grapple pays for one landing, not for every landing afterwards");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerNobodySparedStillTakesTheirFall(TestContext context) {
    final PlayerEntity faller = MockPlayerSupport.mortalPlayerAt(context, PLAYER_STAND);
    final float before = faller.getHealth();

    faller.damage(context.getWorld().getDamageSources().fall(), A_HURTFUL_FALL);

    context.assertTrue(
        faller.getHealth() < before,
        "A player no grapple carried should land their own fall, health was " + faller.getHealth());
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSparedPlayerIsSparedTheirLandingAndNothingElse(TestContext context) {
    final PlayerEntity spared = MockPlayerSupport.mortalPlayerAt(context, PLAYER_STAND);
    final float before = spared.getHealth();
    GrappleFallGuard.spare(spared.getUuid());

    try {
      spared.damage(context.getWorld().getDamageSources().generic(), A_HURTFUL_FALL);

      context.assertTrue(
          spared.getHealth() < before,
          "A grapple pays for a landing, not for everything else that can hurt, health was "
              + spared.getHealth());
    } finally {
      GrappleFallGuard.release(spared.getUuid());
    }
    context.complete();
  }

  private static void startGrappleUnderfoot(
      final TestContext context, final ServerPlayerEntity shooter, final GrappleArrowEntity arrow) {
    context.assertTrue(
        GrappleService.start(
                context.getWorld(),
                shooter,
                context.getAbsolutePos(FLOOR_UNDERFOOT),
                arrow == null ? null : arrow.getUuid())
            != null,
        "A grapple onto the block underfoot should still start a session");
  }

  private static GrappleArrowEntity plantedArrow(final TestContext context) {
    final GrappleArrowEntity arrow =
        context.spawnEntity(ModArrows.GRAPPLE_ARROW.entityType(), ARROW_REST);
    arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
    arrow.setNoGravity(true);
    arrow.setVelocity(Vec3d.ZERO);
    return arrow;
  }

  private static boolean holdsAGrappleArrow(final ServerPlayerEntity shooter) {
    return shooter.getInventory().contains(stack -> stack.isOf(ModArrows.GRAPPLE_ARROW.item()));
  }

  private static void forgetEveryGrapple() {
    GrappleService.forget();
    AnchorService.forget();
    GrappleFallGuard.forget();
  }

  private static NotEnoughArrowsConfig grappleConfig(
      final UnaryOperator<GrappleArrowConfig> change) {
    return NotEnoughArrowsConfig.defaults()
        .withGrapple(change.apply(GrappleArrowConfig.defaults()));
  }
}
