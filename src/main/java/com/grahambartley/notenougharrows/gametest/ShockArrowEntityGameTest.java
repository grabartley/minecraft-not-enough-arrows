package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class ShockArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "shock-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos NEIGHBOUR_STAND = new BlockPos(5, 3, 5);
  private static final int SETTLED = 5;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void realLightningDoesSetACowAlightHere(TestContext context) {
    final CowEntity control = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);

    context.spawnEntity(EntityType.LIGHTNING_BOLT, TARGET_STAND);

    context.runAtTick(
        SETTLED,
        () -> {
          context.assertTrue(
              control.isOnFire(),
              "Ordinary lightning must set a cow alight here, or the shock arrow's no-fire test"
                  + " proves nothing");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowLandsAnOrdinaryHitWithoutSettingAnythingAlight(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final float before = target.getHealth();
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SHOCK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK + SETTLED,
        () -> {
          context.assertTrue(
              target.getHealth() < before,
              "A shock arrow should hurt what it strikes, health stayed at " + before);
          context.assertTrue(
              !target.isOnFire(), "A shock arrow's bolt must never set anything alight");
          context.assertTrue(
              !CombatTestSupport.hasCosmeticLightningLeftFire(context),
              "A shock arrow must leave no fire behind it");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void theBoltJumpsToTheNearestNeighbour(TestContext context) {
    FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final CowEntity neighbour = FiringRangeSupport.liveTargetOnPedestalAt(context, NEIGHBOUR_STAND);
    final float neighbourBefore = neighbour.getHealth();
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SHOCK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK + SETTLED,
        () -> {
          context.assertTrue(
              neighbour.getHealth() < neighbourBefore,
              "The bolt should jump to the nearest living thing beside the impact");
          context.assertTrue(
              !neighbour.isOnFire(), "The jump must not set the neighbour alight either");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowThatHitsOnlyABlockStillJumpsToTheNearestLivingThing(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander = FiringRangeSupport.liveTargetOnPedestalAt(context, NEIGHBOUR_STAND);
    final float before = bystander.getHealth();
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SHOCK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK + SETTLED,
        () -> {
          context.assertTrue(
              bystander.getHealth() < before,
              "The bolt lands where the arrow lands, so a wall hit still jumps to the nearest"
                  + " living thing in reach");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void theBoltNeverJumpsBackAtTheShooter(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final float before = shooter.getHealth();
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.SHOCK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK + SETTLED,
        () -> {
          context.assertTrue(
              shooter.getHealth() == before,
              "The bolt must never jump back at the shooter, they went from "
                  + before
                  + " to "
                  + shooter.getHealth());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowThatHitsOnlyABlockLeavesTheBlockAlone(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SHOCK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK + SETTLED,
        () -> {
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.dontExpectEntity(ModArrows.SHOCK_ARROW.entityType());
          context.complete();
        });
  }
}
