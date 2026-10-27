package com.grahambartley.volley.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SessionTrackerTest {
  private record Pull(UUID pulled, UUID puller, int remainingTicks) {}

  private static final UUID ALEX = UUID.randomUUID();
  private static final UUID SAM = UUID.randomUUID();
  private static final UUID COW = UUID.randomUUID();

  private SessionTracker<Pull> tracker;

  @BeforeEach
  void setUp() {
    tracker = new SessionTracker<>(Pull::pulled, pull -> pull.remainingTicks() == 0);
  }

  @Test
  void keepsOneSessionPerKeyWithTheLatestWinning() {
    tracker.add(new Pull(COW, ALEX, 10));
    final Pull latest = new Pull(COW, SAM, 5);
    tracker.add(latest);

    assertEquals(List.of(latest), tracker.all());
    assertEquals(latest, tracker.get(COW));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 10})
  void aSessionWithTimeLeftIsTracked(final int remainingTicks) {
    tracker.add(new Pull(COW, ALEX, remainingTicks));

    assertEquals(1, tracker.size());
  }

  @Test
  void anExpiredSessionIsNotTracked() {
    tracker.add(new Pull(COW, ALEX, 0));

    assertTrue(tracker.isEmpty());
  }

  @Test
  void removingReturnsWhatWasThere() {
    final Pull pull = new Pull(COW, ALEX, 10);
    tracker.add(pull);

    assertEquals(pull, tracker.remove(COW));
    assertNull(tracker.remove(COW));
    assertEquals(0, tracker.size());
  }

  @Test
  void removeIfTakesOnlyTheMatchingSessions() {
    final Pull alexs = new Pull(COW, ALEX, 10);
    final Pull sams = new Pull(ALEX, SAM, 10);
    tracker.add(alexs);
    tracker.add(sams);

    assertEquals(List.of(alexs), tracker.removeIf(pull -> pull.puller().equals(ALEX)));
    assertEquals(List.of(sams), tracker.all());
  }

  @Test
  void missingKeysAndSessionsAreIgnored() {
    tracker.add(null);

    assertNull(tracker.get(null));
    assertNull(tracker.remove(null));
    assertTrue(tracker.isEmpty());
  }
}
