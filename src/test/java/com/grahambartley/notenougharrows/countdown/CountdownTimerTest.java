package com.grahambartley.notenougharrows.countdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CountdownTimerTest {
  private static final UUID HOST = UUID.nameUUIDFromBytes("host".getBytes());
  private static final UUID OWNER = UUID.nameUUIDFromBytes("owner".getBytes());
  private static final UUID STRANGER = UUID.nameUUIDFromBytes("stranger".getBytes());

  @Test
  void aTimerForEveryoneIsShownToAnyone() {
    final CountdownTimer fuse =
        new CountdownTimer(HOST, 60, 60, 0L, CountdownKind.FUSE, Optional.empty());
    assertTrue(fuse.isShownTo(OWNER));
    assertTrue(fuse.isShownTo(STRANGER));
  }

  @Test
  void aTimerForOnePlayerIsShownToThemAlone() {
    final CountdownTimer allegiance =
        new CountdownTimer(HOST, 3600, 3000, 1L, CountdownKind.ALLEGIANCE, Optional.of(OWNER));
    assertTrue(allegiance.isShownTo(OWNER));
    assertFalse(allegiance.isShownTo(STRANGER));
  }

  @Test
  void neverClaimsMoreTimeLeftThanItStartedWith() {
    assertEquals(
        60,
        new CountdownTimer(HOST, 60, 90, 0L, CountdownKind.FUSE, Optional.empty())
            .remainingTicks());
  }
}
