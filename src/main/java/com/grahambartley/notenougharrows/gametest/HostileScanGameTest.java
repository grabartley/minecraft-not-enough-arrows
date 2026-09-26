package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.HostileScan;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class HostileScanGameTest implements FabricGameTest {
  private static final String BATCH = "hostile-scan";
  private static final BlockPos CENTRE_STAND = new BlockPos(3, 3, 3);
  private static final BlockPos NEAR_STAND = new BlockPos(3, 3, 4);
  private static final BlockPos FAR_STAND = new BlockPos(3, 3, 6);
  private static final double A_SHORT_REACH = 2.0;

  private static Vec3d centreOf(final TestContext context) {
    return Vec3d.ofCenter(context.getAbsolutePos(CENTRE_STAND));
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void findsAHostileInsideTheReach(TestContext context) {
    final ZombieEntity near = ControlTestSupport.stillZombieAt(context, NEAR_STAND);

    context.assertTrue(
        HostileScan.hostilesAround(context.getWorld(), centreOf(context), A_SHORT_REACH)
            .contains(near),
        "A hostile inside the reach should be found");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void leavesAHostileBeyondTheReachAlone(TestContext context) {
    final ZombieEntity far = ControlTestSupport.stillZombieAt(context, FAR_STAND);

    context.assertFalse(
        HostileScan.hostilesAround(context.getWorld(), centreOf(context), A_SHORT_REACH)
            .contains(far),
        "A hostile beyond the reach should be left alone");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void leavesSomethingThatWasNeverHostileAlone(TestContext context) {
    final CowEntity bystander = ControlTestSupport.stillCowAt(context, NEAR_STAND);

    context.assertFalse(
        HostileScan.hostilesAround(context.getWorld(), centreOf(context), A_SHORT_REACH)
            .contains(bystander),
        "Something that was never hostile should be left alone");
    context.assertFalse(HostileScan.isHostile(bystander), "A cow is not a hostile");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void countsOnlyTheHostilesAlreadyFightingSomethingAsEngaged(TestContext context) {
    final ZombieEntity calm = ControlTestSupport.stillZombieAt(context, NEAR_STAND);

    context.assertFalse(HostileScan.isEngaged(calm), "A hostile fighting nobody is not engaged");
    context.assertTrue(
        HostileScan.engagedHostilesAround(context.getWorld(), centreOf(context), A_SHORT_REACH)
            .isEmpty(),
        "A scan for engaged hostiles should skip one that is fighting nobody");

    calm.setTarget(ControlTestSupport.stillCowAt(context, FAR_STAND));

    context.assertTrue(HostileScan.isEngaged(calm), "A hostile with a target is engaged");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void findsNothingWithNoReachAtAll(TestContext context) {
    ControlTestSupport.stillZombieAt(context, NEAR_STAND);

    context.assertTrue(
        HostileScan.hostilesAround(context.getWorld(), centreOf(context), 0.0).isEmpty(),
        "A scan with no reach should find nothing");
    context.complete();
  }
}
