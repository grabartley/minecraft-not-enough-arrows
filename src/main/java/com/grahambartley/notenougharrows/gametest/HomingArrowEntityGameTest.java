package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.entity.HomingArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;

public final class HomingArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "homing-arrow";
  private static final double A_REAL_TURN = 0.05;

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void anArrowCurvesTowardAHostileMobAheadOfIt(TestContext context) {
    CombatTestSupport.stillZombieAt(context, CombatTestSupport.LONG_RANGE_FAR_STAND);
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.HOMING_ARROW.item());

    context.runAtTick(
        CombatTestSupport.MID_FLIGHT_TICK,
        () -> {
          final HomingArrowEntity arrow = arrowInFlight(context);
          context.assertTrue(
              arrow.getVelocity().z > A_REAL_TURN,
              "The arrow should have curved toward the zombie, velocity was "
                  + arrow.getVelocity());
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void anArrowNeverCurvesTowardAPlayer(TestContext context) {
    final ServerPlayerEntity bystander =
        MockPlayerSupport.playerAt(context, CombatTestSupport.LONG_RANGE_FAR_STAND);
    bystander.setVelocity(Vec3d.ZERO);
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.HOMING_ARROW.item());

    context.runAtTick(
        CombatTestSupport.MID_FLIGHT_TICK,
        () -> {
          final HomingArrowEntity arrow = arrowInFlight(context);
          context.assertTrue(
              Math.abs(arrow.getVelocity().z) < A_REAL_TURN,
              "A homing arrow must never curve toward a player, velocity was "
                  + arrow.getVelocity());
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void anArrowNeverCurvesTowardAHarmlessAnimal(TestContext context) {
    CombatTestSupport.stillCowAt(context, CombatTestSupport.LONG_RANGE_FAR_STAND);
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.HOMING_ARROW.item());

    context.runAtTick(
        CombatTestSupport.MID_FLIGHT_TICK,
        () -> {
          final HomingArrowEntity arrow = arrowInFlight(context);
          context.assertTrue(
              Math.abs(arrow.getVelocity().z) < A_REAL_TURN,
              "A homing arrow should ignore a cow, velocity was " + arrow.getVelocity());
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void anArrowWithNothingEligibleAheadFliesStraightOn(TestContext context) {
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.HOMING_ARROW.item());

    context.runAtTick(
        CombatTestSupport.MID_FLIGHT_TICK,
        () -> {
          final HomingArrowEntity arrow = arrowInFlight(context);
          context.assertTrue(
              Math.abs(arrow.getVelocity().z) < A_REAL_TURN,
              "With nothing eligible ahead the arrow should fly straight, velocity was "
                  + arrow.getVelocity());
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void anArrowIgnoresAHostileMobBehindIt(TestContext context) {
    CombatTestSupport.stillZombieAt(context, new net.minecraft.util.math.BlockPos(1, 3, 5));
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.HOMING_ARROW.item());

    context.runAtTick(
        CombatTestSupport.MID_FLIGHT_TICK,
        () -> {
          final HomingArrowEntity arrow = arrowInFlight(context);
          context.assertTrue(
              Math.abs(arrow.getVelocity().z) < A_REAL_TURN,
              "A homing arrow should not turn back for something behind it, velocity was "
                  + arrow.getVelocity());
          context.complete();
        });
  }

  private static HomingArrowEntity arrowInFlight(final TestContext context) {
    final HomingArrowEntity arrow = FiringRangeSupport.firedArrow(context, HomingArrowEntity.class);
    context.assertTrue(arrow != null, "The homing arrow should still be down the range");
    return arrow;
  }
}
