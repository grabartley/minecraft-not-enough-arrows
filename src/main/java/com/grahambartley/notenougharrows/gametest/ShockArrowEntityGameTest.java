package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
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
    final CowEntity control = CombatTestSupport.stillCowAt(context, TARGET_STAND);

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
  public void anArrowHurtsWhatItStrikesWithoutSettingItAlight(TestContext context) {
    final CowEntity target = CombatTestSupport.stillCowAt(context, TARGET_STAND);
    final float before = target.getHealth();
    MockPlayerSupport.fireEastFromBow(
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
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void theBoltJumpsToTheNearestNeighbour(TestContext context) {
    CombatTestSupport.stillCowAt(context, TARGET_STAND);
    final CowEntity neighbour = CombatTestSupport.stillCowAt(context, NEIGHBOUR_STAND);
    final float neighbourBefore = neighbour.getHealth();
    MockPlayerSupport.fireEastFromBow(
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
  public void anArrowThatHitsOnlyABlockLeavesTheBlockAlone(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.SHOCK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK + SETTLED,
        () -> {
          context.expectBlock(net.minecraft.block.Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.dontExpectEntity(ModArrows.SHOCK_ARROW.entityType());
          context.complete();
        });
  }
}
