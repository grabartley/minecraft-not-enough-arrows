package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.BeeSwarmRelease;
import com.grahambartley.notenougharrows.config.BeeArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class BeeSwarmReleaseGameTest implements FabricGameTest {
  private static final String BATCH = "bee-swarm-release";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 2, 3);
  private static final Vec3d RELEASE = new Vec3d(3.5, 3.0, 3.5);
  private static final int LONG_LIFE = 600;
  private static final int PROVOKED_TICKS = 100;

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void releasesTheConfiguredNumberOfBees(TestContext context) {
    final List<BeeEntity> bees = release(context, null, null, new BeeArrowConfig(5, LONG_LIFE));

    context.assertEquals(bees.size(), 5, "Bees released");
    context.assertEquals(
        context
            .getWorld()
            .getEntitiesByClass(BeeEntity.class, context.getTestBox(), BeeEntity::isAlive)
            .size(),
        5,
        "Bees in the world");
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void theBeesAreAngryAtWhatTheArrowStruck(TestContext context) {
    final CowEntity struck = ControlTestSupport.stillCowAt(context, TARGET_STAND);

    for (final BeeEntity bee : release(context, null, struck, new BeeArrowConfig(3, LONG_LIFE))) {
      context.assertTrue(bee.getTarget() == struck, "Each bee should go for the cow");
      context.assertTrue(bee.hasAngerTime(), "Each bee should be angry");
    }
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSwarmReleasedOnTheGroundHasNoTarget(TestContext context) {
    for (final BeeEntity bee : release(context, null, null, new BeeArrowConfig(3, LONG_LIFE))) {
      context.assertTrue(bee.getTarget() == null, "Nothing was struck to be angry at");
    }
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBeeStillStingsSomeoneElse(TestContext context) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);
    final CowEntity struck = ControlTestSupport.stillCowAt(context, TARGET_STAND);
    final float health = struck.getHealth();
    final BeeEntity bee =
        release(context, shooter, struck, new BeeArrowConfig(1, LONG_LIFE)).get(0);

    bee.tryAttack(struck);

    context.assertTrue(struck.getHealth() < health, "The cow should be stung");
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aReleasedBeeCanNeitherHideInAHiveNorBreed(TestContext context) {
    final BeeEntity bee = release(context, null, null, new BeeArrowConfig(1, LONG_LIFE)).get(0);

    final NbtCompound saved = new NbtCompound();
    bee.writeNbt(saved);

    context.assertEquals(
        saved.getInt("CannotEnterHiveTicks"), LONG_LIFE, "Ticks the bee is kept out of hives");
    context.assertTrue(bee.getBreedingAge() > 0, "The bee should not be ready to breed");
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = PROVOKED_TICKS + 20)
  public void aSwarmLeftToItsOwnAiStingsTheCowItWasSentAt(TestContext context) {
    final CowEntity struck = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float cowHealth = struck.getHealth();
    release(context, null, struck, new BeeArrowConfig(3, LONG_LIFE));

    context.runAtTick(
        PROVOKED_TICKS,
        () -> {
          context.assertTrue(struck.getHealth() < cowHealth, "The bees should sting the cow");
          context.complete();
        });
  }

  private static List<BeeEntity> release(
      final TestContext context,
      final PlayerEntity shooter,
      final net.minecraft.entity.Entity struck,
      final BeeArrowConfig config) {
    return BeeSwarmRelease.release(
        context.getWorld(), context.getAbsolute(RELEASE), shooter, struck, config);
  }
}
