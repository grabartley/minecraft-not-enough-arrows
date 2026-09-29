package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.entity.TripwireArrowEntity;
import com.grahambartley.notenougharrows.reveal.Watcher;
import com.grahambartley.notenougharrows.reveal.WatcherService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class TripwireArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "tripwire-arrow";

  @BeforeBatch(batchId = BATCH)
  public void forgetWatchersBeforeBatch(ServerWorld world) {
    WatcherService.forget();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aTripwireArrowLeavesAWatcherForItsShooterAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.TRIPWIRE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final List<Watcher> here = watchersAt(context, FiringRangeSupport.IMPACT_FACE);
          context.assertEquals(1, here.size(), "Watchers in front of the struck face");
          context.assertEquals(shooter.getUuid(), here.getFirst().owner(), "Watcher owner");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TripwireArrowEntity.class) == null,
              "A tripwire arrow that set its watcher is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedTripwireArrowLeavesAWatcherThatReportsToNobody(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, DiscoveryArrows.TRIPWIRE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final List<Watcher> here = watchersAt(context, FiringRangeSupport.IMPACT_FACE);
          context.assertEquals(1, here.size(), "Watchers in front of the struck face");
          context.assertTrue(here.getFirst().owner() == null, "A dispensed watcher has no owner");
          context.complete();
        });
  }

  private static List<Watcher> watchersAt(final TestContext context, final BlockPos relative) {
    final BlockPos pos = context.getAbsolutePos(relative);
    return WatcherService.in(context.getWorld()).stream()
        .filter(watcher -> watcher.pos().equals(pos))
        .toList();
  }
}
