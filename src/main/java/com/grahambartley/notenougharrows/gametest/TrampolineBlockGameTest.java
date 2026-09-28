package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class TrampolineBlockGameTest implements FabricGameTest {
  private static final String BATCH = "trampoline-block";
  private static final String STILL_BATCH = "trampoline-block-still";
  private static final BlockPos PAD = new BlockPos(10, 2, 8);
  private static final BlockPos BARE_FLOOR = new BlockPos(14, 2, 8);
  private static final int DROP_HEIGHT = 12;
  private static final int LANDED_TICK = 40;
  private static final double LAUNCH_SPEED = 1.0;
  private static final double STILL_SPEED = 0.1;

  private NotEnoughArrowsConfig beforeStill;

  @BeforeBatch(batchId = STILL_BATCH)
  public void switchLaunchesOffBeforeBatch(ServerWorld world) {
    beforeStill = ServerConfigService.get();
    ServerConfigHolder.set(
        beforeStill.withTraversal(
            beforeStill
                .traversal()
                .withTrampoline(beforeStill.traversal().trampoline().withStrength(0.0f))));
  }

  @AfterBatch(batchId = STILL_BATCH)
  public void restoreLaunchesAfterBatch(ServerWorld world) {
    ServerConfigHolder.set(beforeStill);
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 60)
  public void aMobLandingOnThePadIsLaunchedBackUp(TestContext context) {
    TraversalTestSupport.keepEntitiesTicking(context);
    widePad(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, PAD.up(4));
    final double[] fastestRise = {0.0};

    context.runAtEveryTick(
        () -> fastestRise[0] = Math.max(fastestRise[0], cow.getVelocity().getY()));
    context.runAtTick(
        LANDED_TICK,
        () -> {
          context.assertTrue(
              fastestRise[0] > LAUNCH_SPEED,
              "The cow should have been thrown back up, fastest rise " + fastestRise[0]);
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 80)
  public void theLandingThatTriggersALaunchDoesNoFallDamage(TestContext context) {
    TraversalTestSupport.keepEntitiesTicking(context);
    widePad(context);
    final CowEntity padded = context.spawnEntity(EntityType.COW, PAD.up(DROP_HEIGHT));
    final CowEntity bare = context.spawnEntity(EntityType.COW, BARE_FLOOR.up(DROP_HEIGHT));

    context.runAtTick(
        LANDED_TICK,
        () -> {
          context.assertTrue(
              bare.getHealth() < bare.getMaxHealth(),
              "The same drop onto bare floor hurts, or this test proves nothing");
          context.assertEquals(
              padded.getMaxHealth(), padded.getHealth(), "The pad takes the sting out");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aPadDropsNothingWhenBroken(TestContext context) {
    widePad(context);

    context.getWorld().breakBlock(context.getAbsolutePos(PAD), true);

    context.expectBlock(Blocks.AIR, PAD);
    context.assertTrue(
        context
            .getWorld()
            .getEntitiesByClass(ItemEntity.class, context.getTestBox(), drop -> true)
            .isEmpty(),
        "A trampoline has no item form, so breaking one drops nothing");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = STILL_BATCH, tickLimit = 60)
  public void aPadWithNoStrengthStopsTheFallWithoutBouncing(TestContext context) {
    TraversalTestSupport.keepEntitiesTicking(context);
    widePad(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, PAD.up(DROP_HEIGHT));
    final double[] fastestRise = {0.0};

    context.runAtEveryTick(
        () -> fastestRise[0] = Math.max(fastestRise[0], cow.getVelocity().getY()));
    context.runAtTick(
        LANDED_TICK,
        () -> {
          context.assertTrue(fastestRise[0] < STILL_SPEED, "Nothing should bounce");
          context.assertEquals(cow.getMaxHealth(), cow.getHealth(), "Still no fall damage");
          context.complete();
        });
  }

  private static void widePad(final TestContext context) {
    for (int dx = -1; dx <= 1; dx++) {
      for (int dz = -1; dz <= 1; dz++) {
        context.setBlockState(PAD.add(dx, 0, dz), ModBlocks.TRAMPOLINE);
      }
    }
  }
}
