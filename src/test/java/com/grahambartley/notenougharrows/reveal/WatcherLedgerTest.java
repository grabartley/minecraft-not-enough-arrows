package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class WatcherLedgerTest {
  private static final UUID OWNER = UUID.randomUUID();

  @Test
  void dropsAnExpiredWatcher() {
    final WatcherLedger ledger = new WatcherLedger();
    final Watcher shortLived = Watcher.placed(OWNER, BlockPos.ORIGIN, 0L, 20, 40);
    final Watcher longLived = Watcher.placed(OWNER, BlockPos.ORIGIN.up(), 0L, 200, 40);
    ledger.add(shortLived);
    ledger.add(longLived);

    assertEquals(1, ledger.dropWhere(20L, pos -> true));

    assertEquals(List.of(longLived), ledger.all());
  }

  @Test
  void dropsAWatcherWhoseGroundUnloaded() {
    final WatcherLedger ledger = new WatcherLedger();
    final Watcher unloaded = Watcher.placed(OWNER, new BlockPos(500, 64, 0), 0L, 200, 40);
    final Watcher loaded = Watcher.placed(OWNER, BlockPos.ORIGIN, 0L, 200, 40);
    ledger.add(unloaded);
    ledger.add(loaded);

    ledger.dropWhere(10L, pos -> pos.getX() < 100);

    assertEquals(List.of(loaded), ledger.all());
  }

  @Test
  void replacesAWatcherWithItsReportedSelf() {
    final WatcherLedger ledger = new WatcherLedger();
    final Watcher watcher = Watcher.placed(OWNER, BlockPos.ORIGIN, 0L, 200, 40);
    ledger.add(watcher);

    ledger.replace(watcher.reportedAt(5L));

    assertEquals(List.of(watcher.reportedAt(5L)), ledger.all());
  }

  @Test
  void doesNotReviveADroppedWatcher() {
    final WatcherLedger ledger = new WatcherLedger();
    final Watcher watcher = Watcher.placed(OWNER, BlockPos.ORIGIN, 0L, 20, 40);
    ledger.add(watcher);
    ledger.dropWhere(20L, pos -> true);

    ledger.replace(watcher.reportedAt(21L));

    assertTrue(ledger.isEmpty());
  }

  @Test
  void keepsOnlyTheNewestWatchersOfOneOwner() {
    final WatcherLedger ledger = new WatcherLedger();
    final Watcher first = Watcher.placed(OWNER, BlockPos.ORIGIN, 0L, 200, 40);
    ledger.add(first);
    for (int more = 1; more <= WatcherLedger.MAX_PER_OWNER; more++) {
      ledger.add(Watcher.placed(OWNER, BlockPos.ORIGIN.up(more), more, 200, 40));
    }

    assertEquals(WatcherLedger.MAX_PER_OWNER, ledger.all().size());
    assertTrue(ledger.all().stream().noneMatch(watcher -> watcher.id().equals(first.id())));
  }

  @Test
  void oneOwnersCapNeverDropsAnotherOwnersWatcher() {
    final WatcherLedger ledger = new WatcherLedger();
    final Watcher someoneElses = Watcher.placed(UUID.randomUUID(), BlockPos.ORIGIN, 0L, 200, 40);
    ledger.add(someoneElses);
    for (int more = 0; more <= WatcherLedger.MAX_PER_OWNER; more++) {
      ledger.add(Watcher.placed(OWNER, BlockPos.ORIGIN.up(more), more, 200, 40));
    }

    assertTrue(ledger.all().contains(someoneElses));
  }
}
