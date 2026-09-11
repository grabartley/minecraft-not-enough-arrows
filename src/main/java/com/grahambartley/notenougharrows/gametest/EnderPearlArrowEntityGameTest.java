package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.entity.EnderPearlArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class EnderPearlArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "ender-pearl-arrow";
  private static final BlockPos DISPENSER_STAND = new BlockPos(1, 2, 3);
  private static final BlockPos TARGET_IN_THE_LANE = new BlockPos(4, 3, 3);
  private static final double ARRIVED_WITHIN = 2.0;
  private static final int DISPENSED_LANDING_TICK = 20;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anArrowFiredFromABowPutsTheShooterWhereItLands(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter = fireEast(context);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          final Vec3d impactFace =
              context.getAbsolute(Vec3d.ofCenter(FiringRangeSupport.IMPACT_FACE));

          context.assertTrue(
              shooter.getPos().distanceTo(impactFace) <= ARRIVED_WITHIN,
              "The shooter should arrive where the arrow landed, but stood at "
                  + shooter.getPos()
                  + " against an impact face at "
                  + impactFace);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anArrowIsSpentByTheTeleportItPerformed(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    fireEast(context);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.dontExpectEntity(ModArrows.ENDER_PEARL_ARROW.entityType());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anArrowThatStrikesAMobPutsTheShooterWhereThatMobStands(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_IN_THE_LANE);
    final Vec3d stoodAt = target.getPos();
    final ServerPlayerEntity shooter = fireEast(context);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              shooter.getPos().distanceTo(stoodAt) <= ARRIVED_WITHIN,
              "The shooter should arrive at the mob the arrow struck, but stood at "
                  + shooter.getPos()
                  + " against a mob at "
                  + stoodAt);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowWithNobodyBehindItTeleportsNobodyAndEmbeds(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(context, DISPENSER_STAND, ModArrows.ENDER_PEARL_ARROW.item());

    context.runAtTick(
        DISPENSED_LANDING_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, EnderPearlArrowEntity.class) != null,
              "An arrow that teleported nobody should embed and stay recoverable");
          context.complete();
        });
  }

  private static ServerPlayerEntity fireEast(final TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.ENDER_PEARL_ARROW.item());
    return shooter;
  }
}
