package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.SmokeArrowConfig;
import com.grahambartley.notenougharrows.control.SmokeCloudService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class SmokeCloudServiceGameTest implements FabricGameTest {
  private static final String BATCH = "smoke-clouds";
  private static final BlockPos CLOUD_STAND = new BlockPos(3, 3, 3);
  private static final BlockPos FAR_STAND = new BlockPos(3, 3, 6);
  private static final BlockPos ARROW_START = new BlockPos(1, 4, 3);
  private static final int BRIEF_CLOUD_TICKS = 5;
  private static final int AFTER_A_BRIEF_CLOUD = 20;
  private static final int LONG_AFTER_A_BRIEF_CLOUD = 40;
  private static final int SETTLING_TICKS = 4;
  private static final double A_BLOCK_OF_TRAVEL = 1.0;

  private static SmokeArrowConfig cloudFor(final float radius, final int ticks) {
    return new SmokeArrowConfig(radius, ticks);
  }

  private static Vec3d cloudCentre(final TestContext context) {
    return Vec3d.ofCenter(context.getAbsolutePos(CLOUD_STAND));
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aCloudBlindsWhatStandsInsideIt(TestContext context) {
    final CowEntity inside = ControlTestSupport.stillCowAt(context, CLOUD_STAND);
    SmokeCloudService.open(context.getWorld(), cloudCentre(context), cloudFor(3.0f, 200));

    context.runAtTick(
        SETTLING_TICKS,
        () -> {
          context.expectEntityHasEffect(inside, StatusEffects.BLINDNESS, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aCloudLeavesWhatStandsOutsideItAlone(TestContext context) {
    final CowEntity outside = ControlTestSupport.stillCowAt(context, FAR_STAND);
    SmokeCloudService.open(context.getWorld(), cloudCentre(context), cloudFor(1.5f, 200));

    context.runAtTick(
        SETTLING_TICKS,
        () -> {
          context.assertTrue(
              outside.getStatusEffect(StatusEffects.BLINDNESS) == null,
              "A smoke cloud should blind only what stands inside it");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void aCloudStopsBlindingOnceItsDurationRunsOut(TestContext context) {
    final CowEntity inside = ControlTestSupport.stillCowAt(context, CLOUD_STAND);
    SmokeCloudService.open(
        context.getWorld(), cloudCentre(context), cloudFor(3.0f, BRIEF_CLOUD_TICKS));

    context.runAtTick(AFTER_A_BRIEF_CLOUD, inside::clearStatusEffects);

    context.runAtTick(
        LONG_AFTER_A_BRIEF_CLOUD,
        () -> {
          context.assertTrue(
              inside.getStatusEffect(StatusEffects.BLINDNESS) == null,
              "An expired smoke cloud should stop blinding what is still standing in it");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aCloudStopsNoArrowFlyingThroughIt(TestContext context) {
    SmokeCloudService.open(context.getWorld(), cloudCentre(context), cloudFor(3.0f, 200));
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, ARROW_START);
    final double startX = arrow.getX();
    arrow.setNoGravity(true);
    arrow.setVelocity(new Vec3d(1.0, 0.0, 0.0));

    context.runAtTick(
        SETTLING_TICKS,
        () -> {
          context.assertTrue(
              arrow.getX() > startX + A_BLOCK_OF_TRAVEL,
              "A smoke cloud is not a solid, so an arrow should fly on through it, but it reached "
                  + arrow.getX()
                  + " from "
                  + startX);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aCloudWithNoReachOrNoDurationIsNeverOpened(TestContext context) {
    final Vec3d centre = cloudCentre(context);

    context.assertFalse(
        SmokeCloudService.open(context.getWorld(), centre, cloudFor(0.0f, 200)),
        "A smoke cloud configured with no reach should never be opened");
    context.assertFalse(
        SmokeCloudService.open(context.getWorld(), centre, cloudFor(3.0f, 0)),
        "A smoke cloud configured to last no time should never be opened");
    context.complete();
  }
}
