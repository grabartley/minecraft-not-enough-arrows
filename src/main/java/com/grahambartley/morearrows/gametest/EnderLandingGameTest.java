package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ender.EnderLanding;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class EnderLandingGameTest implements FabricGameTest {
  private static final String BATCH = "ender-landing";
  private static final BlockPos SUBJECT_STAND = new BlockPos(1, 3, 1);
  private static final BlockPos ANCHOR = new BlockPos(3, 3, 3);
  private static final BlockPos MIDAIR_ANCHOR = new BlockPos(3, 5, 3);
  private static final double ON_THE_ANCHOR = 0.5;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityLandsOnTheAnchorWhenItFitsThere(TestContext context) {
    final Vec3d anchor = context.getAbsolute(Vec3d.ofBottomCenter(ANCHOR));
    context.assertFalse(
        context.getBlockState(ANCHOR.down()).isAir(),
        "This test is about a supported anchor, so it needs ground under it");

    final Optional<Vec3d> landing =
        EnderLanding.forEntity(context.getWorld(), subject(context), anchor);

    context.assertTrue(landing.isPresent(), "An open anchor should be somewhere to land");
    context.assertTrue(
        landing.get().equals(anchor),
        "An open anchor should be the landing itself, but was " + landing.get());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityBlockedAtTheAnchorLandsBesideIt(TestContext context) {
    wallOff(context, ANCHOR);
    final Vec3d anchor = context.getAbsolute(Vec3d.ofBottomCenter(ANCHOR));

    final Optional<Vec3d> landing =
        EnderLanding.forEntity(context.getWorld(), subject(context), anchor);

    context.assertTrue(landing.isPresent(), "A blocked anchor should still find a neighbour");
    context.assertTrue(
        landing.get().distanceTo(anchor) > ON_THE_ANCHOR,
        "A blocked anchor should push the landing aside, but it stayed at " + landing.get());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void thereIsNowhereToLandWhenEveryNeighbourIsSolid(TestContext context) {
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        wallOff(context, ANCHOR.add(x, 0, z));
      }
    }

    final Optional<Vec3d> landing =
        EnderLanding.forEntity(
            context.getWorld(),
            subject(context),
            context.getAbsolute(Vec3d.ofBottomCenter(ANCHOR)));

    context.assertFalse(
        landing.isPresent(), "A solid anchor and solid neighbours offer no landing");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anAnchorWithNoGroundUnderItStillTakesTheEntity(TestContext context) {
    final Vec3d anchor = context.getAbsolute(Vec3d.ofBottomCenter(MIDAIR_ANCHOR));
    context.assertTrue(
        context.getBlockState(MIDAIR_ANCHOR.down()).isAir(),
        "This test is about an unsupported anchor, so it needs nothing under it");

    final Optional<Vec3d> landing =
        EnderLanding.forEntity(context.getWorld(), subject(context), anchor);

    context.assertTrue(
        landing.isPresent(),
        "An anchor a jumping shooter occupies should still be somewhere to land");
    context.assertTrue(
        landing.get().equals(anchor),
        "With nothing supported nearby the anchor itself is the landing, but was " + landing.get());
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void thereIsNowhereToLandWithoutAnAnchor(TestContext context) {
    context.assertFalse(
        EnderLanding.forEntity(context.getWorld(), subject(context), null).isPresent(),
        "A landing needs somewhere to be measured from");
    context.complete();
  }

  private static CowEntity subject(final TestContext context) {
    return FiringRangeSupport.liveTargetOnPedestalAt(context, SUBJECT_STAND);
  }

  private static void wallOff(final TestContext context, final BlockPos relativePos) {
    context.setBlockState(relativePos, Blocks.STONE);
    context.setBlockState(relativePos.up(), Blocks.STONE);
  }
}
