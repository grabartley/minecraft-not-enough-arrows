package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.entity.TracerArrowEntity;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;

public final class TracerArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "tracer-arrow";
  private static final int IN_FLIGHT_TICK = 1;
  private static final double NEAR_THE_BOW = 2.0;

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aTracerRecordsWhereItActuallyFlewFromTheBow(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.TRACER_ARROW.item());
    final Vec3d bow = context.getAbsolute(Vec3d.ofBottomCenter(FiringRangeSupport.SHOOTER_STAND));
    final TracerArrowEntity arrow = FiringRangeSupport.firedArrow(context, TracerArrowEntity.class);
    final Vec3d launch = arrow.path().points().getFirst();

    context.runAtTick(
        IN_FLIGHT_TICK,
        () -> {
          final List<Vec3d> points = arrow.path().points();
          context.assertTrue(points.size() >= 2, "Points recorded in flight: " + points.size());
          context.assertTrue(
              launch.distanceTo(bow) < NEAR_THE_BOW,
              "The path starts at the bow, not somewhere else: " + launch);
          context.assertTrue(
              points.getLast().x > points.getFirst().x, "It records the arrow flying east");
          context.complete();
        });
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aTracerIsSpentWhenItLands(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.TRACER_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TracerArrowEntity.class) == null,
              "A tracer that landed is spent");
          context.complete();
        });
  }
}
