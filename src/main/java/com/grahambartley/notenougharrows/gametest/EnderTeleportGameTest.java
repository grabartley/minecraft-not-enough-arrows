package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ender.EnderTeleport;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class EnderTeleportGameTest implements FabricGameTest {
  private static final String BATCH = "ender-teleport";
  private static final BlockPos DEPARTURE = new BlockPos(1, 3, 3);
  private static final BlockPos ARRIVAL = new BlockPos(5, 3, 3);
  private static final float A_LONG_FALL = 12.0f;
  private static final double ARRIVED_WITHIN = 0.5;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityIsPutWhereItWasSent(TestContext context) {
    final CowEntity subject = FiringRangeSupport.liveTargetOnPedestalAt(context, DEPARTURE);
    final Vec3d destination = context.getAbsolute(Vec3d.ofBottomCenter(ARRIVAL));

    context.assertTrue(
        EnderTeleport.move(context.getWorld(), subject, destination), "The move should be made");
    context.assertTrue(
        subject.getPos().distanceTo(destination) <= ARRIVED_WITHIN,
        "The entity should stand at its destination, but stood at " + subject.getPos());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityArrivesWithTheFallItWasTakingForgotten(TestContext context) {
    final CowEntity subject = FiringRangeSupport.liveTargetOnPedestalAt(context, DEPARTURE);
    subject.fallDistance = A_LONG_FALL;

    EnderTeleport.move(
        context.getWorld(), subject, context.getAbsolute(Vec3d.ofBottomCenter(ARRIVAL)));

    context.assertEquals(
        subject.fallDistance, 0.0f, "A teleport should not land the fall it interrupted");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void thereIsNothingToMoveWithoutASubject(TestContext context) {
    context.assertFalse(
        EnderTeleport.move(
            context.getWorld(), null, context.getAbsolute(Vec3d.ofBottomCenter(ARRIVAL))),
        "A teleport with no subject should move nothing");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void thereIsNowhereToMoveToWithoutADestination(TestContext context) {
    final CowEntity subject = FiringRangeSupport.liveTargetOnPedestalAt(context, DEPARTURE);

    context.assertFalse(
        EnderTeleport.move(context.getWorld(), subject, null),
        "A teleport with no destination should move nothing");
    context.complete();
  }
}
