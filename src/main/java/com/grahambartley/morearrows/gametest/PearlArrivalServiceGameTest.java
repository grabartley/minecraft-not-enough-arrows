package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.EnderArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.ender.PearlArrivalService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PearlArrivalServiceGameTest implements FabricGameTest {
  private static final String BATCH = "ender-pearl-arrival";
  private static final BlockPos SHOOTER_STAND = new BlockPos(1, 3, 3);
  private static final BlockPos NEARBY = new BlockPos(4, 3, 3);
  private static final int SHORT_RANGE = 4;
  private static final float NO_DAMAGE = 0.0f;
  private static final float A_HARD_LANDING = 6.0f;
  private static final double ARRIVED_WITHIN = 0.5;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDestinationInsideTheConfiguredRangeMovesTheShooter(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, SHOOTER_STAND);
    final Vec3d destination = context.getAbsolute(Vec3d.ofBottomCenter(NEARBY));

    context.assertTrue(
        arrive(context, shooter, destination, EnderArrowConfig.defaults()),
        "A destination within range should be reached");
    context.assertTrue(
        shooter.getPos().distanceTo(destination) <= ARRIVED_WITHIN,
        "The shooter should stand at the destination, but stood at " + shooter.getPos());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDestinationBeyondTheConfiguredRangeMovesNobody(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, SHOOTER_STAND);
    final Vec3d stoodAt = shooter.getPos();
    final Vec3d destination = stoodAt.add(SHORT_RANGE * 2.0, 0.0, 0.0);

    context.assertFalse(
        arrive(context, shooter, destination, rangedTo(SHORT_RANGE)),
        "A destination beyond the configured range should be refused");
    context.assertTrue(
        shooter.getPos().equals(stoodAt),
        "A refused teleport should leave the shooter where they were");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void arrivalCostsTheShooterTheConfiguredDamage(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, SHOOTER_STAND);
    final float unharmed = shooter.getHealth();

    arrive(context, shooter, destinationNearby(context), hurtingBy(A_HARD_LANDING));

    context.assertTrue(
        shooter.getHealth() < unharmed,
        "Arrival should cost the shooter health, but it stayed at " + shooter.getHealth());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrivalDamageOfZeroHurtsNobody(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, SHOOTER_STAND);
    final float unharmed = shooter.getHealth();

    arrive(context, shooter, destinationNearby(context), hurtingBy(NO_DAMAGE));

    context.assertEquals(
        shooter.getHealth(), unharmed, "An arrival damage of zero should hurt nobody");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrivalWhereTheShooterAlreadyStandsIsNotAnArrival(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, SHOOTER_STAND);
    final float unharmed = shooter.getHealth();

    context.assertFalse(
        arrive(context, shooter, shooter.getPos(), EnderArrowConfig.defaults()),
        "An arrow that came back down on its own shooter has nowhere to send them");
    context.assertEquals(
        shooter.getHealth(),
        unharmed,
        "A teleport that moved nobody should charge nobody arrival damage");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrowWithNobodyBehindItMovesNobody(TestContext context) {
    context.assertFalse(
        arrive(context, null, destinationNearby(context), EnderArrowConfig.defaults()),
        "A dispensed arrow has no shooter to move");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesTheRange(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.mortalPlayerAt(context, SHOOTER_STAND);
    final Vec3d destination = shooter.getPos().add(SHORT_RANGE * 2.0, 0.0, 0.0);
    final MoreArrowsConfig previous = ServerConfigHolder.get();
    final boolean arrived;
    try {
      ServerConfigHolder.set(MoreArrowsConfig.defaults().withEnder(rangedTo(SHORT_RANGE)));
      arrived = PearlArrivalService.arrive(context.getWorld(), shooter, destination);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertFalse(arrived, "A configured short range should refuse a distant destination");
    context.complete();
  }

  private static boolean arrive(
      final TestContext context,
      final PlayerEntity shooter,
      final Vec3d destination,
      final EnderArrowConfig config) {
    return PearlArrivalService.arrive(context.getWorld(), shooter, destination, config);
  }

  private static Vec3d destinationNearby(final TestContext context) {
    return context.getAbsolute(Vec3d.ofBottomCenter(NEARBY));
  }

  private static EnderArrowConfig rangedTo(final int maxRangeBlocks) {
    return EnderArrowConfig.defaults().withPearlMaxRangeBlocks(maxRangeBlocks);
  }

  private static EnderArrowConfig hurtingBy(final float arrivalDamage) {
    return EnderArrowConfig.defaults().withPearlArrivalDamage(arrivalDamage);
  }
}
