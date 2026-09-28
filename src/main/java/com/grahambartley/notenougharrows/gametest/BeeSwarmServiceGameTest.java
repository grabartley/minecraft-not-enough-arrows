package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.BeeSwarm;
import com.grahambartley.notenougharrows.agriculture.BeeSwarmService;
import com.grahambartley.notenougharrows.config.BeeArrowConfig;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class BeeSwarmServiceGameTest implements FabricGameTest {
  private static final String BATCH = "bee-swarm";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 2, 3);
  private static final Vec3d RELEASE = new Vec3d(3.5, 3.0, 3.5);
  private static final int SHORT_LIFE = BeeArrowConfig.LIFETIME_TICKS_MIN;
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
  public void aBeeCannotHurtItsShooterButStillHurtsAnotherPlayer(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, TARGET_STAND);
    final PlayerEntity bystander = MockPlayerSupport.mortalPlayerAt(context, TARGET_STAND.north());
    final float shooterHealth = shooter.getHealth();
    final float bystanderHealth = bystander.getHealth();
    final List<BeeEntity> bees = release(context, shooter, null, new BeeArrowConfig(2, LONG_LIFE));

    bees.get(0).tryAttack(shooter);
    bees.get(1).tryAttack(bystander);

    context.assertTrue(
        bystander.getHealth() < bystanderHealth,
        "A sting should hurt a player here, or the shooter's health proves nothing");
    context.assertEquals(shooter.getHealth(), shooterHealth, "The shooter's health");
    context.assertFalse(bees.get(0).hasStung(), "A sting the shooter never felt does not count");
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
  public void aBeeTurnedOnItsShooterIsCalledOffAndSentBackToItsTarget(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, TARGET_STAND.west(2));
    final CowEntity struck = ControlTestSupport.stillCowAt(context, TARGET_STAND);
    final BeeEntity bee =
        release(context, shooter, struck, new BeeArrowConfig(1, LONG_LIFE)).get(0);

    bee.setTarget(shooter);
    bee.setAngryAt(shooter.getUuid());

    context.runAtTick(
        2,
        () -> {
          context.assertTrue(bee.getTarget() != shooter, "The bee should drop the shooter");
          context.assertFalse(shooter.getUuid().equals(bee.getAngryAt()), "Nor stay angry at them");
          context.assertTrue(bee.getTarget() == struck, "It should go back for the cow");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SHORT_LIFE + 20)
  public void noBeeOutlivesItsLifetime(TestContext context) {
    final List<BeeEntity> bees = release(context, null, null, new BeeArrowConfig(3, SHORT_LIFE));

    context.runAtTick(
        SHORT_LIFE - 2,
        () -> context.assertTrue(bees.stream().allMatch(BeeEntity::isAlive), "Alive until then"));
    context.runAtTick(
        SHORT_LIFE + 2,
        () -> {
          context.assertTrue(
              bees.stream().allMatch(BeeEntity::isRemoved), "Every bee should be gone");
          context.assertEquals(
              context
                  .getWorld()
                  .getEntitiesByClass(BeeEntity.class, context.getTestBox(), BeeEntity::isAlive)
                  .size(),
              0,
              "Bees left in the world");
          context.complete();
        });
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
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBeeWhoseLifetimeRanOutWhileUnloadedIsRemovedOnLoad(TestContext context) {
    final BeeEntity bee = EntityType.BEE.create(context.getWorld());
    bee.refreshPositionAndAngles(context.getAbsolute(RELEASE), 0.0f, 0.0f);
    bee.setAttached(
        BeeSwarmService.SWARM,
        new BeeSwarm(context.getWorld().getTime() - 1, Optional.empty(), Optional.empty()));

    context.getWorld().spawnEntity(bee);

    context.assertTrue(bee.isRemoved(), "An expired bee should not come back");
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBeeReloadedBeforeItsLifetimeEndsIsTrackedAgain(TestContext context) {
    final BeeEntity bee = EntityType.BEE.create(context.getWorld());
    bee.refreshPositionAndAngles(context.getAbsolute(RELEASE), 0.0f, 0.0f);
    bee.setAttached(
        BeeSwarmService.SWARM,
        new BeeSwarm(context.getWorld().getTime() + LONG_LIFE, Optional.empty(), Optional.empty()));

    context.getWorld().spawnEntity(bee);

    context.assertTrue(bee.isAlive(), "A live bee stays");
    context.assertTrue(
        BeeSwarmService.isTracked(context.getWorld(), bee.getUuid()),
        "A reloaded swarm bee should be watched for its lifetime again");
    context.complete();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void anOrdinaryBeeIsNeverTouched(TestContext context) {
    final BeeEntity wild = context.spawnEntity(EntityType.BEE, TARGET_STAND);

    context.assertFalse(
        BeeSwarmService.isTracked(context.getWorld(), wild.getUuid()), "A wild bee is not ours");
    context.runAtTick(
        5,
        () -> {
          context.assertTrue(wild.isAlive(), "A wild bee is left alone");
          context.complete();
        });
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
    return BeeSwarmService.release(
        context.getWorld(), context.getAbsolute(RELEASE), shooter, struck, config);
  }
}
