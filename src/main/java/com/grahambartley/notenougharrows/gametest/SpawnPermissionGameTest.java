package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.SpawnPermission;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class SpawnPermissionGameTest implements FabricGameTest {
  private static final String BATCH = "spawn-permission";
  private static final BlockPos OPEN_GROUND = new BlockPos(8, 3, 8);

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void allowsASpawnInOpenGround(TestContext context) {
    context.assertTrue(
        SpawnPermission.allows(
            context.getWorld(), EntityType.SNOW_GOLEM, at(context, OPEN_GROUND), null),
        "Open ground");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesASpawnInsideAWall(TestContext context) {
    context.setBlockState(OPEN_GROUND, Blocks.STONE);

    context.assertFalse(
        SpawnPermission.allows(
            context.getWorld(), EntityType.CHICKEN, at(context, OPEN_GROUND), null),
        "Inside stone");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesATallCreatureUnderALowCeiling(TestContext context) {
    context.setBlockState(OPEN_GROUND.up(), Blocks.STONE);

    context.assertTrue(
        SpawnPermission.allows(
            context.getWorld(), EntityType.CHICKEN, at(context, OPEN_GROUND), null),
        "A chicken fits under one block of headroom");
    context.assertFalse(
        SpawnPermission.allows(
            context.getWorld(), EntityType.SNOW_GOLEM, at(context, OPEN_GROUND), null),
        "A snow golem does not");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesAShooterWhoMayNotBuild(TestContext context) {
    final PlayerEntity visitor = MockPlayerSupport.mortalPlayerAt(context, OPEN_GROUND.west(2));
    visitor.getAbilities().allowModifyWorld = false;

    context.assertFalse(
        SpawnPermission.allows(
            context.getWorld(), EntityType.SNOW_GOLEM, at(context, OPEN_GROUND), visitor),
        "A shooter who may not change the world");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void allowsASurvivalShooter(TestContext context) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(
        SpawnPermission.allows(
            context.getWorld(), EntityType.SNOW_GOLEM, at(context, OPEN_GROUND), shooter),
        "A survival shooter in open ground");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesASpawnBeyondTheWorldBorder(TestContext context) {
    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertFalse(
                SpawnPermission.allows(
                    context.getWorld(),
                    EntityType.SNOW_GOLEM,
                    at(context, TerrainTestSupport.CENTER),
                    null),
                "Beyond the world border"));
    context.complete();
  }

  private static Vec3d at(final TestContext context, final BlockPos relative) {
    return Vec3d.ofBottomCenter(context.getAbsolutePos(relative));
  }
}
