package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.terrain.SphereSweep;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class SphereSweepGameTest implements FabricGameTest {
  private static final String BATCH = "sphere-sweep";
  private static final SphereSweep.PositionEffect ALWAYS = (world, pos) -> true;

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRadiusOfZeroTouchesTheCentreAlone(TestContext context) {
    context.assertEquals(
        sweep(context, 0, 10, null, ALWAYS), List.of(center(context)), "Positions changed");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSweepStopsAtItsCapNearestFirst(TestContext context) {
    final List<BlockPos> changed = sweep(context, 2, 3, null, ALWAYS);

    context.assertEquals(changed.size(), 3, "Positions changed");
    context.assertEquals(changed.get(0), center(context), "The first position changed");
    context.assertTrue(
        changed.stream().allMatch(pos -> pos.getManhattanDistance(center(context)) <= 1),
        "A capped sweep should keep to the positions nearest the centre");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPositionTheEffectSkipsDoesNotCountTowardTheCap(TestContext context) {
    final BlockPos center = center(context);
    final List<BlockPos> changed = sweep(context, 1, 2, null, (world, pos) -> !pos.equals(center));

    context.assertEquals(changed.size(), 2, "Positions changed");
    context.assertFalse(changed.contains(center), "The skipped centre should not be reported");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterChangesNothingWhereTheyMayNotBuild(TestContext context) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                sweep(context, 1, 10, shooter, ALWAYS).isEmpty(), "Changed past the border"));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSweepWithNobodyBehindItStopsAtTheWorldBorder(TestContext context) {
    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                sweep(context, 1, 10, null, ALWAYS).isEmpty(), "Changed past the border"));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSweepNeverLoadsAChunkToChangeIt(TestContext context) {
    final BlockPos farAway = center(context).add(10_000_000, 0, 10_000_000);

    context.assertTrue(
        SphereSweep.sweep(context.getWorld(), farAway, 0, 10, null, ALWAYS).isEmpty(),
        "A position in an unloaded chunk should be skipped");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aGivenListIsSweptInItsOwnOrderUpToTheCap(TestContext context) {
    final BlockPos first = center(context).east(2);
    final BlockPos second = center(context);
    final BlockPos third = center(context).west();

    final List<BlockPos> changed =
        SphereSweep.sweep(context.getWorld(), List.of(first, second, third), 2, null, ALWAYS);

    context.assertEquals(changed, List.of(first, second), "Positions changed");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPositionPastTheWorldBorderIsNotEditable(TestContext context) {
    context.assertTrue(
        SphereSweep.editableBy(context.getWorld(), null).test(center(context)),
        "Editable inside the border");
    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertFalse(
                SphereSweep.editableBy(context.getWorld(), null).test(center(context)),
                "Not editable past the border"));
    context.complete();
  }

  private static BlockPos center(final TestContext context) {
    return context.getAbsolutePos(TerrainTestSupport.CENTER);
  }

  private static List<BlockPos> sweep(
      final TestContext context,
      final int radius,
      final int cap,
      final PlayerEntity shooter,
      final SphereSweep.PositionEffect effect) {
    return SphereSweep.sweep(context.getWorld(), center(context), radius, cap, shooter, effect);
  }
}
