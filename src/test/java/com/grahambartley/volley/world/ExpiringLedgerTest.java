package com.grahambartley.volley.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExpiringLedgerTest {
  private static final UUID FIRST = new UUID(0L, 1L);
  private static final UUID SECOND = new UUID(0L, 2L);

  @Test
  void holdsAnEntryUntilItsExpiryTick() {
    final ExpiringLedger<String> ledger = new ExpiringLedger<>();
    ledger.put(FIRST, "sheep", 10L);

    assertTrue(ledger.removeExpired(9L).isEmpty());
    assertEquals(Optional.of("sheep"), ledger.get(FIRST));
    assertEquals(List.of(FIRST), ledger.removeExpired(10L));
    assertTrue(ledger.isEmpty());
  }

  @Test
  void removesOnlyTheExpiredEntries() {
    final ExpiringLedger<String> ledger = new ExpiringLedger<>();
    ledger.put(FIRST, "sheep", 5L);
    ledger.put(SECOND, "pig", 50L);

    assertEquals(List.of(FIRST), ledger.removeExpired(20L));
    assertTrue(ledger.contains(SECOND));
    assertFalse(ledger.contains(FIRST));
  }

  @Test
  void puttingAgainRefreshesTheExpiry() {
    final ExpiringLedger<String> ledger = new ExpiringLedger<>();
    ledger.put(FIRST, "sheep", 5L);
    ledger.put(FIRST, "sheep", 50L);

    assertTrue(ledger.removeExpired(20L).isEmpty());
    assertEquals(1, ledger.size());
  }

  @Test
  void removingReturnsTheValueOnce() {
    final ExpiringLedger<String> ledger = new ExpiringLedger<>();
    ledger.put(FIRST, "sheep", 5L);

    assertEquals(Optional.of("sheep"), ledger.remove(FIRST));
    assertTrue(ledger.remove(FIRST).isEmpty());
    assertFalse(ledger.contains(FIRST));
  }

  @Test
  void containsIsFalseForNull() {
    assertFalse(new ExpiringLedger<String>().contains(null));
  }

  @Test
  void refusesAMissingIdOrValue() {
    final ExpiringLedger<String> ledger = new ExpiringLedger<>();

    assertThrows(NullPointerException.class, () -> ledger.put(null, "sheep", 1L));
    assertThrows(NullPointerException.class, () -> ledger.put(FIRST, null, 1L));
  }

  @ParameterizedTest(name = "{0} entries")
  @ValueSource(ints = {0, 1, 3})
  void listsTheIdsItHoldsInTheOrderTheyWereAdded(final int count) {
    final ExpiringLedger<String> ledger = new ExpiringLedger<>();
    final List<UUID> added = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      final UUID id = UUID.randomUUID();
      ledger.put(id, "entry", 10L - i);
      added.add(id);
    }

    assertEquals(added, ledger.ids());
  }
}
