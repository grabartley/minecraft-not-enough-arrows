package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.StinkCloudService;
import com.grahambartley.notenougharrows.config.StinkArrowConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class StinkCloudServiceGameTest implements FabricGameTest {
  private static final String BATCH = "stink-cloud";
  private static final BlockPos CLOUD = new BlockPos(24, 3, 8);
  private static final BlockPos OUTSIDE = new BlockPos(40, 3, 8);
  private static final int LIFETIME = 40;
  private static final int CLOUD_REACH = 3;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aStinkCloudNauseatesAPlayerInsideAndNobodyOutside(TestContext context) {
    final ServerPlayerEntity inside = ChaosTestSupport.survivalPlayerAt(context, CLOUD);
    final ServerPlayerEntity outside = ChaosTestSupport.survivalPlayerAt(context, OUTSIDE);
    open(context, LIFETIME);

    context.runAtTick(
        10,
        () -> {
          context.assertTrue(
              inside.hasStatusEffect(StatusEffects.NAUSEA), "A player inside feels sick");
          context.assertFalse(
              outside.hasStatusEffect(StatusEffects.NAUSEA), "A player outside is fine");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aStinkCloudHurtsNobody(TestContext context) {
    final ServerPlayerEntity player = ChaosTestSupport.survivalPlayerAt(context, CLOUD);
    final float health = player.getHealth();
    open(context, LIFETIME);

    context.runAtTick(
        30,
        () -> {
          context.assertEquals(health, player.getHealth(), "The player's health");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aStinkCloudDoesNotNauseateAMob(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, CLOUD);
    zombie.setAiDisabled(true);
    open(context, LIFETIME);

    context.runAtTick(
        10,
        () -> {
          context.assertFalse(
              zombie.hasStatusEffect(StatusEffects.NAUSEA), "Nausea is for players");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = LIFETIME + 20)
  public void aStinkCloudEndsOnTimeAndLeavesNoBlockBehind(TestContext context) {
    open(context, LIFETIME);
    context.assertTrue(
        StinkCloudService.isInCloud(context.getWorld(), centre(context)), "The cloud is up");

    context.runAtTick(
        LIFETIME + 5,
        () -> {
          context.assertFalse(
              StinkCloudService.isInCloud(context.getWorld(), centre(context)),
              "The cloud should be gone");
          for (int dx = -CLOUD_REACH; dx <= CLOUD_REACH; dx++) {
            for (int dy = 0; dy <= CLOUD_REACH; dy++) {
              for (int dz = -CLOUD_REACH; dz <= CLOUD_REACH; dz++) {
                context.expectBlock(Blocks.AIR, CLOUD.add(dx, dy, dz));
              }
            }
          }
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aZeroLifetimeOrADisabledArrowOpensNoCloud(TestContext context) {
    final Vec3d at = centre(context);
    context.assertFalse(
        StinkCloudService.open(
            context.getWorld(), at, StinkArrowConfig.defaults().withCloudLifetimeTicks(0)),
        "No cloud with no lifetime");
    context.assertFalse(
        StinkCloudService.open(
            context.getWorld(), at, StinkArrowConfig.defaults().withEnabled(false)),
        "No cloud from a switched off arrow");
    context.complete();
  }

  private static void open(final TestContext context, final int lifetime) {
    context.assertTrue(
        StinkCloudService.open(
            context.getWorld(),
            centre(context),
            StinkArrowConfig.defaults().withCloudLifetimeTicks(lifetime)),
        "The cloud should open");
  }

  private static Vec3d centre(final TestContext context) {
    return Vec3d.ofCenter(context.getAbsolutePos(CLOUD));
  }
}
