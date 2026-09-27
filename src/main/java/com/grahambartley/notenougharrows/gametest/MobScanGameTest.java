package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.MobScan;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MobScanGameTest implements FabricGameTest {
  private static final String BATCH = "mob-scan";
  private static final BlockPos CENTRE_STAND = new BlockPos(3, 3, 3);
  private static final BlockPos NEAR_STAND = new BlockPos(3, 3, 4);
  private static final BlockPos FAR_STAND = new BlockPos(3, 3, 6);
  private static final double A_SHORT_REACH = 2.0;

  private static Vec3d centreOf(final TestContext context) {
    return Vec3d.ofCenter(context.getAbsolutePos(CENTRE_STAND));
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void findsAMobInsideTheReach(TestContext context) {
    final ZombieEntity near = ControlTestSupport.stillZombieAt(context, NEAR_STAND);

    context.assertTrue(
        MobScan.mobsAround(context.getWorld(), centreOf(context), A_SHORT_REACH).contains(near),
        "A mob inside the reach should be found");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void leavesAMobBeyondTheReachAlone(TestContext context) {
    final ZombieEntity far = ControlTestSupport.stillZombieAt(context, FAR_STAND);

    context.assertFalse(
        MobScan.mobsAround(context.getWorld(), centreOf(context), A_SHORT_REACH).contains(far),
        "A mob beyond the reach should be left alone");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void findsEveryKindOfMobNotOnlyMonsters(TestContext context) {
    final CowEntity bystander = ControlTestSupport.stillCowAt(context, NEAR_STAND);

    context.assertTrue(
        MobScan.mobsAround(context.getWorld(), centreOf(context), A_SHORT_REACH)
            .contains(bystander),
        "Control arrows reach every mob, so a cow inside the reach should be found");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void countsOnlyTheMobsAlreadyFightingSomethingAsEngaged(TestContext context) {
    final ZombieEntity calm = ControlTestSupport.stillZombieAt(context, NEAR_STAND);

    context.assertFalse(MobScan.isEngaged(calm), "A mob fighting nobody is not engaged");
    context.assertTrue(
        MobScan.engagedMobsAround(context.getWorld(), centreOf(context), A_SHORT_REACH).isEmpty(),
        "A scan for engaged mobs should skip one that is fighting nobody");

    calm.setTarget(ControlTestSupport.stillCowAt(context, FAR_STAND));

    context.assertTrue(MobScan.isEngaged(calm), "A mob with a target is engaged");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void findsNothingWithNoReachAtAll(TestContext context) {
    ControlTestSupport.stillZombieAt(context, NEAR_STAND);

    context.assertTrue(
        MobScan.mobsAround(context.getWorld(), centreOf(context), 0.0).isEmpty(),
        "A scan with no reach should find nothing");
    context.complete();
  }
}
