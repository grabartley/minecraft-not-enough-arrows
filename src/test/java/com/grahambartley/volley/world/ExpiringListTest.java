package com.grahambartley.volley.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExpiringListTest {
  private final ExpiringList<Long> expiringAt =
      new ExpiringList<>((expiry, tick) -> tick >= expiry);

  @Test
  void startsEmptyAndIgnoresNothing() {
    expiringAt.add(null);

    assertTrue(expiringAt.isEmpty());
  }

  @ParameterizedTest
  @CsvSource({"0, 3", "10, 2", "20, 1", "30, 0"})
  void dropsOnlyWhatHasExpiredByTheGivenTick(final long tick, final int left) {
    expiringAt.add(10L);
    expiringAt.add(20L);
    expiringAt.add(30L);

    expiringAt.removeExpired(tick);

    assertEquals(left, expiringAt.size());
  }

  @Test
  void listsWhatIsLiveInTheOrderItWasAdded() {
    expiringAt.add(30L);
    expiringAt.add(10L);

    assertEquals(List.of(30L, 10L), expiringAt.live());
  }
}
