package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class WatcherTest {
  private static final UUID OWNER = UUID.randomUUID();
  private static final BlockPos POS = new BlockPos(3, 70, 9);

  @Test
  void expiresAtTheEndOfItsLifetime() {
    final Watcher watcher = Watcher.placed(OWNER, POS, 1000L, 200, 40);

    assertFalse(watcher.hasExpired(1199L));
    assertTrue(watcher.hasExpired(1200L));
  }

  @Test
  void mayReportAsSoonAsItIsSet() {
    assertTrue(Watcher.placed(OWNER, POS, 1000L, 200, 40).canReportAt(1000L));
  }

  @Test
  void reportsAtMostOncePerInterval() {
    final Watcher reported = Watcher.placed(OWNER, POS, 1000L, 200, 40).reportedAt(1010L);

    assertFalse(reported.canReportAt(1010L));
    assertFalse(reported.canReportAt(1049L));
    assertTrue(reported.canReportAt(1050L));
  }

  @Test
  void aWatcherWithNoShooterNeverReports() {
    final Watcher ownerless = Watcher.placed(null, POS, 1000L, 200, 40);

    assertFalse(ownerless.canReportAt(1000L));
    assertFalse(ownerless.canReportAt(5000L));
  }

  @Test
  void ignoresItsOwnerAndNobodyElse() {
    final Watcher watcher = Watcher.placed(OWNER, POS, 1000L, 200, 40);

    assertTrue(watcher.ignores(OWNER));
    assertFalse(watcher.ignores(UUID.randomUUID()));
    assertFalse(Watcher.placed(null, POS, 1000L, 200, 40).ignores(OWNER));
  }

  @Test
  void reportingKeepsItsIdentityAndExpiry() {
    final Watcher watcher = Watcher.placed(OWNER, POS, 1000L, 200, 40);
    final Watcher reported = watcher.reportedAt(1010L);

    assertEquals(watcher.id(), reported.id());
    assertEquals(watcher.expiryTick(), reported.expiryTick());
    assertEquals(POS, reported.pos());
  }

  @Test
  void aZeroIntervalStillThrottlesToOneTick() {
    final Watcher reported = Watcher.placed(OWNER, POS, 0L, 200, 0).reportedAt(5L);

    assertFalse(reported.canReportAt(5L));
    assertTrue(reported.canReportAt(6L));
  }
}
