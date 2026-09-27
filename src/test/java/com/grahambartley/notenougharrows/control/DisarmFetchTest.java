package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DisarmFetchTest {

  @ParameterizedTest
  @CsvSource({"true, 20, true", "true, 19, false", "false, 20, false", "false, 0, false"})
  void grabsOnlyASettledItemWithinVanillasPickupRange(
      final boolean withinRange, final int itemAge, final boolean expected) {
    assertEquals(expected, DisarmFetch.canGrab(withinRange, itemAge));
  }

  @org.junit.jupiter.api.Test
  void theOwnerMayGrabBeforeAnyoneElseIsAllowedTo() {
    assertTrue(DisarmFetch.OWNER_SETTLE_TICKS < DisarmDrop.PICKUP_DELAY_TICKS);
  }

  @Test
  void expiresOnItsExpiryTick() {
    final DisarmFetch fetch = new DisarmFetch(UUID.randomUUID(), UUID.randomUUID(), 0.085f, 100L);
    assertFalse(fetch.hasExpired(99L));
    assertTrue(fetch.hasExpired(100L));
  }

  @Test
  void needsBothAMobAndAnItem() {
    assertThrows(
        NullPointerException.class, () -> new DisarmFetch(null, UUID.randomUUID(), 0f, 0L));
    assertThrows(
        NullPointerException.class, () -> new DisarmFetch(UUID.randomUUID(), null, 0f, 0L));
  }
}
