package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.EnderArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.ender.RecallService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RecallServiceGameTest implements FabricGameTest {
  private static final String BATCH = "ender-recall";
  private static final BlockPos SHOOTER_STAND = new BlockPos(0, 3, 3);
  private static final BlockPos TARGET_STAND = new BlockPos(6, 3, 3);
  private static final int SHORT_RANGE = EnderArrowConfig.RECALL_MAX_RANGE_BLOCKS_MIN;
  private static final double ARRIVED_WITHIN = 2.0;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStruckMobIsBroughtToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final CowEntity struck = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);

    context.assertTrue(
        recall(context, shooter, struck, EnderArrowConfig.defaults()),
        "A mob within range should be recalled");
    context.assertTrue(
        struck.getPos().distanceTo(shooter.getPos()) <= ARRIVED_WITHIN,
        "The mob should arrive at the shooter, but stood at " + struck.getPos());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMobBeyondTheConfiguredRangeStaysWhereItIs(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final CowEntity struck = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final Vec3d stoodAt = struck.getPos();

    context.assertFalse(
        recall(context, shooter, struck, rangedTo(SHORT_RANGE)),
        "A mob beyond the configured range should not be recalled");
    context.assertTrue(
        struck.getPos().equals(stoodAt), "A refused recall should move nothing at all");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStruckPlayerIsLeftAloneByDefault(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final PlayerEntity struck = MockPlayerSupport.mortalPlayerAt(context, TARGET_STAND);
    final Vec3d stoodAt = struck.getPos();

    context.assertFalse(
        recall(context, shooter, struck, EnderArrowConfig.defaults()),
        "Players should be out of the recall arrow's reach until an operator says otherwise");
    context.assertTrue(
        struck.getPos().equals(stoodAt), "A player who may not be recalled should not move");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStruckPlayerIsMovedOnceAnOperatorAllowsIt(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final ServerPlayerEntity struck = MockPlayerSupport.playerAt(context, TARGET_STAND);

    context.assertTrue(
        recall(context, shooter, struck, allowingPlayers()),
        "A player should be recalled once an operator turns it on");
    context.assertTrue(
        struck.getPos().distanceTo(shooter.getPos()) <= ARRIVED_WITHIN,
        "The recalled player should arrive at the shooter, but stood at " + struck.getPos());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrowWithNobodyBehindItMovesNothing(TestContext context) {
    final CowEntity struck = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);

    context.assertFalse(
        recall(context, null, struck, allowingPlayers()),
        "A dispensed arrow has no shooter to recall anything to");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDroppedItemIsNeverRecalled(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnItem(Items.ARROW, TARGET_STAND);

    context.assertFalse(
        recall(context, shooter, struck, allowingPlayers()),
        "A dropped item is neither alive nor a vehicle and should never be recalled");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anExperienceOrbIsNeverRecalled(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnEntity(EntityType.EXPERIENCE_ORB, TARGET_STAND);

    context.assertFalse(
        recall(context, shooter, struck, allowingPlayers()),
        "An experience orb is neither alive nor a vehicle and should never be recalled");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBoatIsBroughtToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnEntity(EntityType.BOAT, TARGET_STAND);

    context.assertTrue(
        recall(context, shooter, struck, EnderArrowConfig.defaults()),
        "A boat is a vehicle and should be recalled");
    context.assertTrue(
        struck.getPos().distanceTo(shooter.getPos()) <= ARRIVED_WITHIN,
        "The boat should arrive at the shooter, but sat at " + struck.getPos());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMinecartIsBroughtToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnEntity(EntityType.MINECART, TARGET_STAND);

    context.assertTrue(
        recall(context, shooter, struck, EnderArrowConfig.defaults()),
        "A minecart is a vehicle and should be recalled");
    context.assertTrue(
        struck.getPos().distanceTo(shooter.getPos()) <= ARRIVED_WITHIN,
        "The minecart should arrive at the shooter, but sat at " + struck.getPos());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVehicleCarryingAPlayerObeysThePlayerSwitch(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnEntity(EntityType.BOAT, TARGET_STAND);
    final ServerPlayerEntity rider = MockPlayerSupport.playerAt(context, TARGET_STAND);
    rider.startRiding(struck, true);

    context.assertTrue(struck.hasPlayerRider(), "The rider should be aboard for this test");
    context.assertFalse(
        recall(context, shooter, struck, EnderArrowConfig.defaults()),
        "Recalling a boat must not move the player riding it while the switch is off");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aWitherIsNeverRecalled(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnEntity(EntityType.WITHER, TARGET_STAND);

    context.assertFalse(
        recall(context, shooter, struck, allowingPlayers()),
        "A boss is never recalled, whatever an operator has configured");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEnderDragonIsNeverRecalled(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final Entity struck = context.spawnEntity(EntityType.ENDER_DRAGON, TARGET_STAND);

    context.assertFalse(
        recall(context, shooter, struck, allowingPlayers()),
        "A boss is never recalled, whatever an operator has configured");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesWhetherPlayersMove(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final ServerPlayerEntity struck = MockPlayerSupport.playerAt(context, TARGET_STAND);
    final MoreArrowsConfig previous = ServerConfigHolder.get();
    final boolean recalled;
    try {
      ServerConfigHolder.set(MoreArrowsConfig.defaults().withEnder(allowingPlayers()));
      recalled = RecallService.recall(context.getWorld(), shooter, struck);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertTrue(recalled, "The live config should be what decides a player recall");
    context.complete();
  }

  private static boolean recall(
      final TestContext context,
      final PlayerEntity shooter,
      final Entity struck,
      final EnderArrowConfig config) {
    return RecallService.recall(context.getWorld(), shooter, struck, config);
  }

  private static EnderArrowConfig rangedTo(final int maxRangeBlocks) {
    return EnderArrowConfig.defaults().withRecallMaxRangeBlocks(maxRangeBlocks);
  }

  private static EnderArrowConfig allowingPlayers() {
    return EnderArrowConfig.defaults().withRecallAffectsPlayers(true);
  }
}
