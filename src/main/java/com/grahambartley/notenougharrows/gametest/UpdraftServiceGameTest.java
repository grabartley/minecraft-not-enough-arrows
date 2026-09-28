package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.UpdraftArrowConfig;
import com.grahambartley.notenougharrows.updraft.UpdraftService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class UpdraftServiceGameTest implements FabricGameTest {
  private static final String FORGET_BATCH = "updraft-forget";
  private static final String BATCH = "updraft";
  private static final BlockPos BASE = new BlockPos(10, 3, 8);
  private static final BlockPos BESIDE = new BlockPos(14, 3, 8);
  private static final int LIFTED_TICK = 5;
  private static final int SHORT_LIFETIME_TICKS = 5;
  private static final int LOW_HEIGHT_BLOCKS = 2;
  private static final int FLOATING_TICKS = 40;
  private static final UpdraftArrowConfig DEFAULTS = UpdraftArrowConfig.defaults();

  @BeforeBatch(batchId = BATCH)
  public void forgetUpdraftsBeforeBatch(ServerWorld world) {
    UpdraftService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aColumnLiftsAMobStandingInIt(TestContext context) {
    final CowEntity cow = context.spawnEntity(EntityType.COW, BASE);
    final double start = cow.getY();

    open(context, DEFAULTS);

    context.runAtTick(
        LIFTED_TICK,
        () -> {
          context.assertTrue(cow.getY() > start + 0.5, "The cow should be rising, y=" + cow.getY());
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aColumnGrantsNothingOutsideItself(TestContext context) {
    final ArmorStandEntity beside = context.spawnEntity(EntityType.ARMOR_STAND, BESIDE);
    final double start = beside.getY();

    open(context, DEFAULTS);

    context.runAtTick(
        LIFTED_TICK,
        () -> {
          context.assertTrue(
              TraversalTestSupport.nearlyEqual(start, beside.getY()),
              "Something beside the column should stay on the ground");
          context.assertTrue(beside.getVelocity().getY() <= 0.0, "It is given no lift");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aColumnStopsLiftingAtItsHeight(TestContext context) {
    final BlockPos ledge = BASE.up(LOW_HEIGHT_BLOCKS + 1);
    context.setBlockState(ledge.down(), Blocks.STONE);
    final ArmorStandEntity above = context.spawnEntity(EntityType.ARMOR_STAND, ledge);
    final double start = above.getY();

    open(context, new UpdraftArrowConfig(LOW_HEIGHT_BLOCKS, 100, DEFAULTS.strength()));

    context.runAtTick(
        LIFTED_TICK,
        () -> {
          context.assertTrue(
              TraversalTestSupport.nearlyEqual(start, above.getY()),
              "Something above the column's height is not lifted");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 30)
  public void anExpiredColumnLiftsNothing(TestContext context) {
    open(context, new UpdraftArrowConfig(DEFAULTS.heightBlocks(), SHORT_LIFETIME_TICKS, 0.4f));
    final ArmorStandEntity[] late = new ArmorStandEntity[1];
    final double[] start = new double[1];

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 2,
        () -> {
          late[0] = context.spawnEntity(EntityType.ARMOR_STAND, BASE);
          start[0] = late[0].getY();
        });
    context.runAtTick(
        SHORT_LIFETIME_TICKS + 2 + LIFTED_TICK,
        () -> {
          context.assertTrue(
              TraversalTestSupport.nearlyEqual(start[0], late[0].getY()),
              "A column past its lifetime lifts nothing");
          final Vec3d base = Vec3d.ofBottomCenter(context.getAbsolutePos(BASE));
          context.assertTrue(
              UpdraftService.liveIn(context.getWorld()).stream()
                  .noneMatch(updraft -> updraft.column().base().equals(base)),
              "It stopped ticking");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 30)
  public void somethingThatLeavesTheColumnIsNotLiftedAgainOnReturning(TestContext context) {
    final ArmorStandEntity stand = context.spawnEntity(EntityType.ARMOR_STAND, BASE);
    open(context, DEFAULTS);

    context.runAtTick(2, () -> moveTo(context, stand, BESIDE));
    context.runAtTick(4, () -> moveTo(context, stand, BASE));
    context.runAtTick(
        5,
        () -> {
          context.assertTrue(
              stand.getVelocity().getY() < DEFAULTS.strength(),
              "A column never lifts again what already left it, so it cannot be hovered at");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void somethingWithNoGravityIsLeftAlone(TestContext context) {
    final ArmorStandEntity floating = context.spawnEntity(EntityType.ARMOR_STAND, BASE);
    floating.setNoGravity(true);

    open(context, DEFAULTS);

    context.runAtTick(
        LIFTED_TICK,
        () -> {
          context.assertEquals(0.0, floating.getVelocity().getY(), "No lift");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aPlayerInAColumnIsNotCountedAsFloatingNorFalling(TestContext context) {
    final ServerPlayerEntity player =
        TraversalTestSupport.playerAt(context, Vec3d.ofBottomCenter(BASE));
    player.getAbilities().flying = false;
    open(context, DEFAULTS);

    context.runAtTick(
        3,
        () -> {
          player.networkHandler.floatingTicks = FLOATING_TICKS;
          player.fallDistance = 9.0f;
        });
    context.runAtTick(
        4,
        () -> {
          context.assertEquals(0, player.networkHandler.floatingTicks, "Floating ticks");
          context.assertEquals(0.0f, player.fallDistance, "Fall distance");
          context.assertTrue(player.getVelocity().getY() > 0.0, "The player is lifted too");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = BATCH, tickLimit = 20)
  public void aColumnWithNoLifetimeOrNoStrengthNeverOpens(TestContext context) {
    context.assertFalse(
        UpdraftService.open(
            context.getWorld(),
            Vec3d.ofBottomCenter(context.getAbsolutePos(BASE)),
            new UpdraftArrowConfig(DEFAULTS.heightBlocks(), 0, DEFAULTS.strength())),
        "Zero lifetime");
    context.assertFalse(
        UpdraftService.open(
            context.getWorld(),
            Vec3d.ofBottomCenter(context.getAbsolutePos(BASE)),
            new UpdraftArrowConfig(DEFAULTS.heightBlocks(), DEFAULTS.lifetimeTicks(), 0.0f)),
        "Zero strength");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.ARENA, batchId = FORGET_BATCH, tickLimit = 20)
  public void forgettingEveryColumnLeavesNothingLifting(TestContext context) {
    open(context, DEFAULTS);

    UpdraftService.forget();

    context.assertTrue(UpdraftService.liveIn(context.getWorld()).isEmpty(), "Nothing is live");
    context.complete();
  }

  private static void open(final TestContext context, final UpdraftArrowConfig updraft) {
    TraversalTestSupport.keepEntitiesTicking(context);
    context.assertTrue(
        UpdraftService.open(
            context.getWorld(), Vec3d.ofBottomCenter(context.getAbsolutePos(BASE)), updraft),
        "The column should open");
  }

  private static void moveTo(
      final TestContext context, final ArmorStandEntity stand, final BlockPos relative) {
    final Vec3d target = Vec3d.ofBottomCenter(context.getAbsolutePos(relative));
    stand.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0.0f, 0.0f);
    stand.setVelocity(Vec3d.ZERO);
  }
}
