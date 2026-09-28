package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.EnderArrowConfig;
import com.grahambartley.notenougharrows.config.TowArrowConfig;
import com.grahambartley.notenougharrows.grapple.GrappleService;
import com.grahambartley.notenougharrows.tow.TowService;
import com.grahambartley.notenougharrows.zipline.RideService;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class TowServiceGameTest implements FabricGameTest {
  private static final String FORGET_BATCH = "tow-forget";
  private static final String BATCH = "tow";
  private static final Vec3d SHOOTER_STAND = new Vec3d(4.5, 3.0, 8.5);
  private static final BlockPos FAR_TARGET = new BlockPos(20, 3, 8);
  private static final BlockPos NEAR_TARGET = new BlockPos(7, 3, 8);
  private static final int WALL_X = 12;
  private static final int TRENCH_X = 14;
  private static final int TRENCH_WIDTH = 2;
  private static final int SHORT_BUDGET_TICKS = 5;
  private static final int SHORT_RANGE_BLOCKS = 5;
  private static final int FLOATING_TICKS = 40;
  private static final int ROOMY_TICKS = 30;

  @BeforeBatch(batchId = BATCH)
  public void forgetTowsBeforeBatch(ServerWorld world) {
    TowService.forget();
    GrappleService.forget();
    RideService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aTowTakesHoldOfALivingTargetInRange(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);

    context.assertTrue(start(context, shooter, cow), "A cow in range should be towed");
    context.assertTrue(
        TowService.towOf(context.getWorld(), cow.getUuid()) != null, "The tow is tracked");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 40)
  public void aTowedMobIsDraggedAcrossTheGroundTowardTheShooterUnderItsOwnAi(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    final double startX = cow.getX();
    start(context, shooter, cow);

    context.runAtTick(
        15,
        () -> {
          context.assertTrue(
              cow.getX() < startX - 3.0,
              "The cow should have been dragged toward the shooter, x=" + cow.getX());
          context.assertTrue(cow.getHealth() == cow.getMaxHealth(), "Towing does not hurt");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 60)
  public void aTowEndsOnceTheTargetReachesTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, NEAR_TARGET);
    start(context, shooter, cow);

    context.runAtTick(
        ROOMY_TICKS,
        () -> {
          context.assertTrue(
              TowService.towOf(context.getWorld(), cow.getUuid()) == null,
              "A target at the shooter's feet should be let go");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 80)
  public void aWallInTheWayEndsTheTowRatherThanPullingThroughIt(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    for (int z = 1; z < 15; z++) {
      for (int y = 3; y < 6; y++) {
        context.setBlockState(new BlockPos(WALL_X, y, z), Blocks.STONE);
      }
    }
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    start(context, shooter, cow);

    context.runAtTick(
        60,
        () -> {
          context.assertTrue(
              TowService.towOf(context.getWorld(), cow.getUuid()) == null,
              "An obstructed tow should end rather than stall");
          context.assertTrue(
              context.getRelative(cow.getPos()).getX() > WALL_X,
              "The cow is still on the far side of the wall");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 80)
  public void aTowedMobFallsIntoTheGroundBetweenRatherThanTeleportingOverIt(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    for (int x = TRENCH_X; x < TRENCH_X + TRENCH_WIDTH; x++) {
      for (int z = 1; z < 15; z++) {
        context.setBlockState(new BlockPos(x, 2, z), Blocks.AIR);
      }
    }
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    final double[] lowest = {cow.getY()};
    start(context, shooter, cow);

    context.runAtEveryTick(() -> lowest[0] = Math.min(lowest[0], cow.getY()));
    context.runAtTick(
        60,
        () -> {
          context.assertTrue(
              lowest[0] < context.getAbsolutePos(FAR_TARGET).getY() - 0.5,
              "The cow should have dropped into the trench it was dragged over");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aTowRunsOutOfTicksRatherThanPullingForever(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    TowService.start(
        context.getWorld(),
        shooter,
        cow,
        new TowArrowConfig(
            TowArrowConfig.DEFAULT_RANGE_BLOCKS, SHORT_BUDGET_TICKS, TowArrowConfig.DEFAULT_SPEED),
        EnderArrowConfig.defaults());

    context.runAtTick(
        SHORT_BUDGET_TICKS + 3,
        () -> {
          context.assertTrue(
              TowService.towOf(context.getWorld(), cow.getUuid()) == null,
              "The tow should let go when its budget is spent");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aTargetThatDiesIsLetGo(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    start(context, shooter, cow);

    context.runAtTick(3, cow::kill);
    context.runAtTick(
        5,
        () -> {
          context.assertTrue(
              TowService.towOf(context.getWorld(), cow.getUuid()) == null,
              "A dead target is not towed");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aShooterWhoDiesOrLeavesLetsGoOfEverythingTheyTow(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity first = context.spawnEntity(EntityType.COW, FAR_TARGET);
    final CowEntity second = context.spawnEntity(EntityType.COW, FAR_TARGET.south(3));
    start(context, shooter, first);
    start(context, shooter, second);

    ServerLivingEntityEvents.AFTER_DEATH
        .invoker()
        .afterDeath(shooter, context.getWorld().getDamageSources().generic());

    context.assertTrue(
        TowService.towOf(context.getWorld(), first.getUuid()) == null
            && TowService.towOf(context.getWorld(), second.getUuid()) == null,
        "Every tow the shooter started should end with them");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aPlayerIsNotTowedWhereTheRecallSettingKeepsPlayersPut(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final ServerPlayerEntity target = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, target, Vec3d.ofBottomCenter(FAR_TARGET));

    context.assertFalse(
        TowService.start(
            context.getWorld(),
            shooter,
            target,
            TowArrowConfig.defaults(),
            EnderArrowConfig.defaults().withRecallAffectsPlayers(false)),
        "The server said recall moves no players, so neither does a tow");
    context.assertTrue(
        TowService.towOf(context.getWorld(), target.getUuid()) == null, "Nothing is tracked");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aPlayerIsTowedWhereRecallMovesPlayersAndLosesAnyGrappleOrRide(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final ServerPlayerEntity target = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, target, Vec3d.ofBottomCenter(FAR_TARGET));
    context.setBlockState(FAR_TARGET.up(3), Blocks.STONE);
    GrappleService.start(
        context.getWorld(), target, context.getAbsolutePos(FAR_TARGET.up(3)), null);

    context.assertTrue(
        TowService.start(
            context.getWorld(),
            shooter,
            target,
            TowArrowConfig.defaults(),
            EnderArrowConfig.defaults().withRecallAffectsPlayers(true)),
        "A server that lets recall move players lets a tow move them");
    context.assertTrue(
        GrappleService.sessionOf(context.getWorld(), target.getUuid()) == null,
        "The tow replaces the target's own grapple");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aTowedPlayerIsNotCountedAsFloating(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final ServerPlayerEntity target = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, target, Vec3d.ofBottomCenter(FAR_TARGET));
    TowService.start(
        context.getWorld(),
        shooter,
        target,
        TowArrowConfig.defaults(),
        EnderArrowConfig.defaults().withRecallAffectsPlayers(true));

    context.runAtTick(3, () -> target.networkHandler.floatingTicks = FLOATING_TICKS);
    context.runAtTick(
        4,
        () -> {
          context.assertEquals(0, target.networkHandler.floatingTicks, "Floating ticks");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aTargetBeyondTheRangeIsNotTowed(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);

    context.assertFalse(
        TowService.start(
            context.getWorld(),
            shooter,
            cow,
            TowArrowConfig.defaults().withRangeBlocks(SHORT_RANGE_BLOCKS),
            EnderArrowConfig.defaults()),
        "A cow further than the tow range stays put");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aSecondTowOnTheSameTargetReplacesTheFirst(TestContext context) {
    final ServerPlayerEntity first = shooter(context);
    final ServerPlayerEntity second = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    start(context, first, cow);

    start(context, second, cow);

    context.assertEquals(
        second.getUuid(),
        TowService.towOf(context.getWorld(), cow.getUuid()).shooterId(),
        "The newer tow holds the target");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aSeatedTargetIsPulledOutOfItsSeat(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final BoatEntity boat = context.spawnEntity(EntityType.BOAT, FAR_TARGET);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    cow.startRiding(boat, true);

    start(context, shooter, cow);

    context.assertFalse(cow.hasVehicle(), "A towed cow leaves the boat it sat in");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = FORGET_BATCH, tickLimit = 20)
  public void forgettingEveryTowLeavesNothingPulled(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, FAR_TARGET);
    start(context, shooter, cow);

    TowService.forget();

    context.assertTrue(
        TowService.towOf(context.getWorld(), cow.getUuid()) == null,
        "A stopped server leaves nothing being towed");
    context.complete();
  }

  private static ServerPlayerEntity shooter(final TestContext context) {
    return TraversalTestSupport.playerAt(context, SHOOTER_STAND);
  }

  private static boolean start(
      final TestContext context, final ServerPlayerEntity shooter, final CowEntity cow) {
    return TowService.start(
        context.getWorld(), shooter, cow, TowArrowConfig.defaults(), EnderArrowConfig.defaults());
  }
}
