package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.PhysicsArrowConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.RicochetArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RicochetArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "ricochet-arrow";
  private static final BlockPos CORRIDOR_SHOOTER_STAND = new BlockPos(2, 2, 3);
  private static final BlockPos WEST_WALL = new BlockPos(0, 3, 3);
  private static final BlockPos TARGET_IN_THE_LANE = new BlockPos(4, 3, 3);
  private static final int ONE_BOUNCE = 1;
  private static final int RESTING_TICK = 25;
  private static final int STILL_RESTING_TICK = 35;
  private static final double STILL = 1.0e-6;
  private static final double EMBEDDED_IN_THE_WALL = 1.0;

  @BeforeBatch(batchId = BATCH)
  public void allowOneBounceOnly(ServerWorld world) {
    ServerConfigHolder.set(
        NotEnoughArrowsConfig.defaults()
            .withPhysics(PhysicsArrowConfig.defaults().withRicochetBounceCount(ONE_BOUNCE)));
  }

  @AfterBatch(batchId = BATCH)
  public void restoreDefaultSettings(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowGlancesOffAWallInsteadOfEmbedding(TestContext context) {
    raiseWalls(context);
    fireEast(context);

    context.runAtTick(
        PhysicsArrowTestSupport.IMPACT_TICK,
        () -> {
          final RicochetArrowEntity arrow = firedArrow(context);

          context.assertTrue(arrow != null, "A bouncing arrow should still be in the world");
          context.assertEquals(arrow.bounces(), ONE_BOUNCE, "Bounces used off the first wall");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aBounceSendsTheArrowBackTheWayItCame(TestContext context) {
    raiseWalls(context);
    fireEast(context);

    context.runAtTick(
        PhysicsArrowTestSupport.IMPACT_TICK,
        () -> {
          final RicochetArrowEntity arrow = firedArrow(context);

          context.assertTrue(arrow != null, "A bouncing arrow should still be in the world");
          context.assertTrue(
              arrow.getVelocity().getX() < 0.0,
              "An arrow fired east into a wall should come back west, velocity was "
                  + arrow.getVelocity());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowOutOfBouncesEmbedsInTheNextWallItMeets(TestContext context) {
    raiseWalls(context);
    fireEast(context);

    final Vec3d[] restingPlace = {null};
    context.runAtTick(
        RESTING_TICK,
        () -> {
          final RicochetArrowEntity arrow = firedArrow(context);
          context.assertTrue(arrow != null, "An arrow out of bounces should still be in the world");
          context.assertEquals(
              arrow.bounces(), ONE_BOUNCE, "An arrow should never bounce past its budget");
          restingPlace[0] = arrow.getPos();
        });
    context.runAtTick(
        STILL_RESTING_TICK,
        () -> {
          final RicochetArrowEntity arrow = firedArrow(context);

          context.assertTrue(arrow != null, "An embedded arrow should stay recoverable");
          context.assertTrue(
              arrow.getPos().squaredDistanceTo(restingPlace[0]) < STILL,
              "An arrow out of bounces should embed rather than carry on, it moved from "
                  + restingPlace[0]
                  + " to "
                  + arrow.getPos());

          final Vec3d wallFace = context.getAbsolute(Vec3d.ofCenter(WEST_WALL)).add(0.5, 0.0, 0.0);
          context.assertTrue(
              arrow.getPos().getX() - wallFace.getX() < EMBEDDED_IN_THE_WALL,
              "An arrow out of bounces should come to rest against the wall it met, not on the"
                  + " floor short of it, resting X was "
                  + arrow.getPos().getX()
                  + " against a wall face at "
                  + wallFace.getX());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatSurvivesAReloadDoesNotGetItsBouncesBack(TestContext context) {
    raiseWalls(context);
    fireEast(context);

    context.runAtTick(
        PhysicsArrowTestSupport.IMPACT_TICK,
        () -> {
          final RicochetArrowEntity bounced = firedArrow(context);
          context.assertTrue(bounced != null, "A bouncing arrow should still be in the world");
          context.assertEquals(bounced.bounces(), ONE_BOUNCE, "Bounces used before the reload");

          final NbtCompound saved = new NbtCompound();
          bounced.writeCustomDataToNbt(saved);

          final RicochetArrowEntity reloaded =
              context.spawnEntity(
                  ModArrows.RICOCHET_ARROW.entityType(), CORRIDOR_SHOOTER_STAND.up());
          context.assertEquals(
              reloaded.bounces(), 0, "A freshly built arrow starts with all of its bounces");

          reloaded.readCustomDataFromNbt(saved);
          context.assertEquals(
              reloaded.bounces(),
              ONE_BOUNCE,
              "An arrow that came back from a reload should remember the bounces it spent");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aBounceCarriesTheArrowsDamageWhenTheSettingsSaySo(TestContext context) {
    raiseWalls(context);
    fireEast(context);

    final double[] damageInFlight = {0.0};
    context.runAtTick(
        1,
        () -> {
          final RicochetArrowEntity arrow = firedArrow(context);
          context.assertTrue(arrow != null, "The fired arrow should be in the world");
          context.assertEquals(arrow.bounces(), 0, "The arrow should not have bounced yet");
          damageInFlight[0] = arrow.getDamage();
        });
    context.runAtTick(
        PhysicsArrowTestSupport.IMPACT_TICK,
        () -> {
          final RicochetArrowEntity arrow = firedArrow(context);

          context.assertTrue(arrow != null, "A bouncing arrow should still be in the world");
          context.assertEquals(arrow.bounces(), ONE_BOUNCE, "Bounces used off the first wall");
          context.assertTrue(
              arrow.getDamage() == damageInFlight[0],
              "With retention on a bounce should cost no damage, it went from "
                  + damageInFlight[0]
                  + " to "
                  + arrow.getDamage());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void hittingAnEntityIsANormalHitRatherThanABounce(TestContext context) {
    raiseWalls(context);
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_IN_THE_LANE);
    final float unharmed = target.getHealth();
    fireEast(context);

    context.runAtTick(
        PhysicsArrowTestSupport.IMPACT_TICK,
        () -> {
          context.assertTrue(
              target.getHealth() < unharmed,
              "An arrow that strikes a mob should hurt it rather than glance off it");
          final RicochetArrowEntity arrow = firedArrow(context);
          context.assertTrue(
              arrow == null || arrow.bounces() == 0,
              "Hitting an entity is never a bounce, but the arrow had bounced");
          context.complete();
        });
  }

  private static RicochetArrowEntity firedArrow(final TestContext context) {
    return FiringRangeSupport.firedArrow(context, RicochetArrowEntity.class);
  }

  private static void fireEast(final TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, CORRIDOR_SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.RICOCHET_ARROW.item());
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);
  }

  private static void raiseWalls(final TestContext context) {
    for (final BlockPos wall : new BlockPos[] {FiringRangeSupport.BACKSTOP, WEST_WALL}) {
      context.setBlockState(wall.down(), Blocks.STONE);
      context.setBlockState(wall, Blocks.STONE);
      context.setBlockState(wall.up(), Blocks.STONE);
    }
  }
}
