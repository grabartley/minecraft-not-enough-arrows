package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.blast.BlastService;
import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.entity.ExplosiveArrowEntity;
import com.grahambartley.morearrows.fuse.Fuse;
import com.grahambartley.morearrows.fuse.FuseService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ExplosiveCarrierGameTest implements FabricGameTest {
  private static final String BATCH = "explosive-carrier";

  private static final BlockPos WALKED_TO = new BlockPos(2, 3, 3);
  private static final BlockPos NEIGHBOUR = new BlockPos(5, 3, 4);
  private static final BlockPos SECOND_SHOOTER_STAND = new BlockPos(1, 2, 4);
  private static final int SECOND_ARMING_TICK = 25;
  private static final int BOTH_ARMED_TICK = SECOND_ARMING_TICK + 10;
  private static final int CHAINED_CHECK_TICK = SECOND_ARMING_TICK + 25;
  private static final int NEARBY = 1;
  private static final int THE_COLUMN_ITSELF = 0;
  private static final int HANDOVER_TICK = UtilityArrowTestSupport.LANDING_TICK + 10;
  private static final int BLAST_TICK =
      UtilityArrowTestSupport.LANDING_TICK
          + ExplosiveArrowConfig.DEFAULT_FIRE_CHARGE.delayTicks()
          + 20;

  @BeforeBatch(batchId = BATCH)
  public void forgetFusesBeforeBatch(ServerWorld world) {
    FuseService.forget();
    BlastService.forget();
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 120)
  public void anArrowThatStrikesAMobHandsItsFuseToThatMob(TestContext context) {
    final CowEntity target = fireIntoACow(context);
    final float healthBefore = target.getHealth();

    context.runAtTick(
        HANDOVER_TICK,
        () -> {
          final Fuse carried = FuseService.fuseOn(context.getWorld(), target.getUuid());
          FuseService.extinguish(context.getWorld(), target.getUuid());

          context.assertTrue(
              carried != null, "The mob that was struck should be the one carrying the fuse");
          context.assertTrue(
              noArrowLeftIn(context),
              "The arrow should be consumed once it has handed its fuse over");
          context.assertEquals(
              target.getHealth(),
              healthBefore,
              "The blast is the whole payload, so the arrow deals no damage on contact");
          context.complete();
        });
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 140)
  public void aChargeGoesOffWhereItsCarrierEndedUpRatherThanWhereItStruck(TestContext context) {
    final CowEntity target = fireIntoACow(context);
    context.setBlockState(WALKED_TO.down(), Blocks.STONE);

    context.runAtTick(
        HANDOVER_TICK,
        () -> {
          context.assertTrue(
              FuseService.fuseOn(context.getWorld(), target.getUuid()) != null,
              "This test needs the mob carrying the fuse before it walks");
          moveTo(context, target, WALKED_TO);
        });

    context.runAtTick(
        BLAST_TICK,
        () -> {
          context.assertTrue(
              fireWithin(context, WALKED_TO, NEARBY),
              "The charge should go off where its carrier ended up");
          context.assertFalse(
              fireWithin(context, UtilityArrowTestSupport.IMPACT_FACE, THE_COLUMN_ITSELF),
              "The charge should leave nothing behind where the arrow struck");
          context.complete();
        });
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 140)
  public void aCarrierKilledMidCountdownTakesItsChargeWithIt(TestContext context) {
    final CowEntity target = fireIntoACow(context);

    context.runAtTick(
        HANDOVER_TICK,
        () -> {
          context.assertTrue(
              FuseService.fuseOn(context.getWorld(), target.getUuid()) != null,
              "This test needs the mob carrying the fuse before it dies");
          target.kill();
        });

    context.runAtTick(
        BLAST_TICK,
        () -> {
          context.assertFalse(
              fireWithin(context, UtilityArrowTestSupport.IMPACT_FACE, NEARBY),
              "A carrier killed mid countdown should take its charge with it");
          context.assertTrue(
              FuseService.fuseOn(context.getWorld(), target.getUuid()) == null,
              "A dead carrier should leave no fuse burning");
          context.complete();
        });
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 140)
  public void aSecondArrowDoesNotRestartAFuseAlreadyBurningOnTheSameMob(TestContext context) {
    final CowEntity target = fireIntoACow(context);

    context.runAtTick(
        HANDOVER_TICK,
        () -> {
          final Fuse first = FuseService.fuseOn(context.getWorld(), target.getUuid());
          context.assertTrue(first != null, "This test needs a fuse already burning on the mob");
          fireAtTheCow(context);

          context.runAtTick(
              HANDOVER_TICK + 12,
              () -> {
                final Fuse second = FuseService.fuseOn(context.getWorld(), target.getUuid());
                FuseService.extinguish(context.getWorld(), target.getUuid());

                context.assertTrue(second != null, "The original fuse should still be burning");
                context.assertTrue(
                    second.remainingTicks() < first.remainingTicks(),
                    "A second arrow should leave the countdown running down rather than restart"
                        + " it, but it went from "
                        + first.remainingTicks()
                        + " to "
                        + second.remainingTicks());
                context.complete();
              });
        });
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 160)
  public void aCarrierKilledByAnotherChargeDoesNotStillGoOff(TestContext context) {
    final CowEntity first = fireIntoACow(context);
    final CowEntity second = UtilityArrowTestSupport.liveTargetOnPedestalAt(context, NEIGHBOUR);

    context.runAtTick(
        SECOND_ARMING_TICK,
        () ->
            MockPlayerSupport.fireEastFromBow(
                context,
                MockPlayerSupport.playerAt(context, SECOND_SHOOTER_STAND),
                ModArrows.FIRE_CHARGE_ARROW.item()));

    context.runAtTick(
        BOTH_ARMED_TICK,
        () ->
            context.assertTrue(
                FuseService.fuseOn(context.getWorld(), second.getUuid()) != null,
                "This test needs both mobs carrying a charge before the first one goes off"));

    context.runAtTick(
        CHAINED_CHECK_TICK,
        () -> {
          context.assertTrue(
              first.isRemoved() || !first.isAlive(),
              "This test needs the first charge to have gone off already");
          context.assertTrue(
              second.isRemoved() || !second.isAlive(),
              "This test needs the first charge to have killed the second carrier");
          context.assertTrue(
              FuseService.fuseOn(context.getWorld(), second.getUuid()) == null,
              "A carrier killed by another charge should take its own charge with it rather than"
                  + " having it put back while the countdown is still being walked");
          context.complete();
        });
  }

  private static CowEntity fireIntoACow(final TestContext context) {
    UtilityArrowTestSupport.raiseBackstop(context);
    final CowEntity target =
        UtilityArrowTestSupport.liveTargetOnPedestalAt(
            context, UtilityArrowTestSupport.IMPACT_FACE);
    fireAtTheCow(context);
    return target;
  }

  private static void fireAtTheCow(final TestContext context) {
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, UtilityArrowTestSupport.SHOOTER_STAND),
        ModArrows.FIRE_CHARGE_ARROW.item());
  }

  private static void moveTo(
      final TestContext context, final CowEntity target, final BlockPos relativePos) {
    final BlockPos absolute = context.getAbsolutePos(relativePos);
    target.refreshPositionAndAngles(
        absolute.getX() + 0.5, absolute.getY(), absolute.getZ() + 0.5, 0.0f, 0.0f);
    target.setVelocity(Vec3d.ZERO);
  }

  private static boolean noArrowLeftIn(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(ExplosiveArrowEntity.class, context.getTestBox(), candidate -> true)
        .isEmpty();
  }

  private static boolean fireWithin(
      final TestContext context, final BlockPos relativePos, final int radius) {
    for (int offsetX = -radius; offsetX <= radius; offsetX++) {
      for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
        for (int offsetY = -1; offsetY <= 1; offsetY++) {
          if (context.getBlockState(relativePos.add(offsetX, offsetY, offsetZ)).isOf(Blocks.FIRE)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
