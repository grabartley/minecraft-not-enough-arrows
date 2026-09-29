package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.ChickenRelease;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class ChickenReleaseGameTest implements FabricGameTest {
  private static final String BATCH = "chicken-release";
  private static final BlockPos OPEN_GROUND = new BlockPos(8, 3, 8);
  private static final int HIGH_UP = 6;
  private static final int FALL_TICKS = 160;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void releasesAnOrdinaryAdultChicken(TestContext context) {
    final ChickenEntity chicken =
        ChickenRelease.release(context.getWorld(), at(context, OPEN_GROUND), null).orElseThrow();

    context.assertFalse(chicken.isBaby(), "The chicken should be an adult");
    context.assertTrue(chicken.getBreedingAge() == 0, "The chicken should be ready to breed");
    context.assertTrue(chicken.isAlive(), "The chicken should be alive");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = FALL_TICKS + 20)
  public void aChickenReleasedHighUpTakesNoFallDamage(TestContext context) {
    final ChickenEntity chicken =
        ChickenRelease.release(context.getWorld(), at(context, OPEN_GROUND.up(HIGH_UP)), null)
            .orElseThrow();

    context.runAtTick(
        FALL_TICKS,
        () -> {
          context.assertTrue(chicken.isOnGround(), "The chicken should have landed");
          context.assertEquals(chicken.getMaxHealth(), chicken.getHealth(), "Chicken health");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesAChickenInsideAWall(TestContext context) {
    context.setBlockState(OPEN_GROUND, Blocks.STONE);

    context.assertTrue(
        ChickenRelease.release(context.getWorld(), at(context, OPEN_GROUND), null).isEmpty(),
        "No chicken inside stone");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesAChickenForAShooterWhoMayNotBuild(TestContext context) {
    final PlayerEntity visitor = MockPlayerSupport.mortalPlayerAt(context, OPEN_GROUND.west(2));
    visitor.getAbilities().allowModifyWorld = false;

    context.assertFalse(
        ChickenRelease.allows(context.getWorld(), at(context, OPEN_GROUND), visitor),
        "A shooter who may not change the world may not release a chicken either");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void allowsAChickenForASurvivalShooter(TestContext context) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(
        ChickenRelease.allows(context.getWorld(), at(context, OPEN_GROUND), shooter),
        "A survival shooter may release a chicken in open ground");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesAChickenBeyondTheWorldBorder(TestContext context) {
    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                ChickenRelease.release(
                        context.getWorld(), at(context, TerrainTestSupport.CENTER), null)
                    .isEmpty(),
                "No chicken beyond the world border"));
    context.complete();
  }

  private static Vec3d at(final TestContext context, final BlockPos relative) {
    return Vec3d.ofBottomCenter(context.getAbsolutePos(relative));
  }
}
