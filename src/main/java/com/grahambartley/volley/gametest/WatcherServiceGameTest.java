package com.grahambartley.volley.gametest;

import com.grahambartley.volley.reveal.Watcher;
import com.grahambartley.volley.reveal.WatcherAlarm;
import com.grahambartley.volley.reveal.WatcherService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class WatcherServiceGameTest implements FabricGameTest {
  private static final String BATCH = "watcher-service";
  private static final BlockPos WIRE = new BlockPos(3, 2, 3);
  private static final int LIFETIME_TICKS = 400;
  private static final int INTERVAL_TICKS = 40;

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-somethingCrossingAWatcherRaisesAnAlarmForItsOwner",
      tickLimit = 10)
  public void somethingCrossingAWatcherRaisesAnAlarmForItsOwner(TestContext context) {
    WatcherService.forget();
    final ServerPlayerEntity owner = MockPlayerSupport.player(context);
    place(context, owner);
    final CowEntity cow = ControlTestSupport.stillCowAt(context, WIRE);

    final List<WatcherAlarm> alarms = sweep(context, 0);

    context.assertEquals(1, alarms.size(), "Alarms raised");
    context.assertEquals(owner.getUuid(), alarms.getFirst().owner(), "Alarm owner");
    context.assertEquals(cow, alarms.getFirst().crosser(), "What crossed");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-aBusyWatcherReportsAtMostOncePerInterval",
      tickLimit = 10)
  public void aBusyWatcherReportsAtMostOncePerInterval(TestContext context) {
    WatcherService.forget();
    final ServerPlayerEntity owner = MockPlayerSupport.player(context);
    place(context, owner);
    ControlTestSupport.stillCowAt(context, WIRE);
    ControlTestSupport.stillCowAt(context, WIRE);

    int raised = 0;
    for (int tick = 0; tick < INTERVAL_TICKS * 3; tick++) {
      raised += sweep(context, tick).size();
    }

    context.assertEquals(3, raised, "Alarms raised across three intervals by two cows");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-nothingCrossingRaisesNoAlarm",
      tickLimit = 10)
  public void nothingCrossingRaisesNoAlarm(TestContext context) {
    WatcherService.forget();
    place(context, MockPlayerSupport.player(context));
    ControlTestSupport.stillCowAt(context, WIRE.east(2));

    context.assertTrue(sweep(context, 0).isEmpty(), "Nothing is inside the watcher");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-theOwnerWalkingThroughTheirOwnWatcherRaisesNoAlarm",
      tickLimit = 10)
  public void theOwnerWalkingThroughTheirOwnWatcherRaisesNoAlarm(TestContext context) {
    WatcherService.forget();
    final ServerPlayerEntity owner = MockPlayerSupport.playerAt(context, WIRE);
    place(context, owner);

    context.assertTrue(sweep(context, 0).isEmpty(), "The owner does not trip their own wire");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-anotherPlayerCrossingRaisesAnAlarm",
      tickLimit = 10)
  public void anotherPlayerCrossingRaisesAnAlarm(TestContext context) {
    WatcherService.forget();
    final ServerPlayerEntity owner = MockPlayerSupport.player(context);
    place(context, owner);
    final ServerPlayerEntity intruder = MockPlayerSupport.playerAt(context, WIRE);

    final List<WatcherAlarm> alarms = sweep(context, 0);

    context.assertEquals(1, alarms.size(), "Alarms raised");
    context.assertEquals(intruder, alarms.getFirst().crosser(), "What crossed");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-aWatcherWithNoShooterReportsToNobodyAndStillExpires",
      tickLimit = 10)
  public void aWatcherWithNoShooterReportsToNobodyAndStillExpires(TestContext context) {
    WatcherService.forget();
    final Watcher watcher = place(context, null);
    ControlTestSupport.stillCowAt(context, WIRE);

    context.assertTrue(sweep(context, 0).isEmpty(), "Nobody to report to");
    context.assertTrue(isLive(context, watcher), "It is still there until it expires");
    sweep(context, LIFETIME_TICKS);
    context.assertFalse(isLive(context, watcher), "It expired quietly");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-anExpiredWatcherIsGoneAndRaisesNothing",
      tickLimit = 10)
  public void anExpiredWatcherIsGoneAndRaisesNothing(TestContext context) {
    WatcherService.forget();
    final Watcher watcher = place(context, MockPlayerSupport.player(context));
    ControlTestSupport.stillCowAt(context, WIRE);

    context.assertEquals(
        1, sweep(context, LIFETIME_TICKS - 1).size(), "A watcher still reports a tick before");
    context.assertTrue(sweep(context, LIFETIME_TICKS).isEmpty(), "An expired watcher is silent");
    context.assertFalse(isLive(context, watcher), "An expired watcher is dropped");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-aWatcherWhoseGroundUnloadsIsDropped",
      tickLimit = 10)
  public void aWatcherWhoseGroundUnloadsIsDropped(TestContext context) {
    WatcherService.forget();
    final Watcher watcher = place(context, MockPlayerSupport.player(context));
    ControlTestSupport.stillCowAt(context, WIRE);
    final BlockPos wire = context.getAbsolutePos(WIRE);

    final List<WatcherAlarm> alarms =
        WatcherService.sweep(context.getWorld(), 0L, pos -> !pos.equals(wire));

    context.assertTrue(alarms.isEmpty(), "A watcher in unloaded ground raises nothing");
    context.assertFalse(isLive(context, watcher), "It is gone and does not come back");
    context.complete();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH + "-forgettingDropsEveryWatcherAsARestartWould",
      tickLimit = 10)
  public void forgettingDropsEveryWatcherAsARestartWould(TestContext context) {
    WatcherService.forget();
    final Watcher watcher = place(context, MockPlayerSupport.player(context));

    WatcherService.forget();

    context.assertFalse(isLive(context, watcher), "No watcher survives a restart");
    context.complete();
  }

  private static Watcher place(final TestContext context, final ServerPlayerEntity owner) {
    return WatcherService.place(
        context.getWorld(), context.getAbsolutePos(WIRE), owner, LIFETIME_TICKS, INTERVAL_TICKS);
  }

  private static List<WatcherAlarm> sweep(final TestContext context, final long ticksLater) {
    final BlockPos wire = context.getAbsolutePos(WIRE);
    final long placedAt = context.getWorld().getTime();
    return WatcherService.sweep(context.getWorld(), placedAt + ticksLater, pos -> true).stream()
        .filter(alarm -> alarm.watcherPos().equals(wire))
        .toList();
  }

  private static boolean isLive(final TestContext context, final Watcher watcher) {
    return WatcherService.in(context.getWorld()).stream()
        .anyMatch(live -> live.id().equals(watcher.id()));
  }
}
