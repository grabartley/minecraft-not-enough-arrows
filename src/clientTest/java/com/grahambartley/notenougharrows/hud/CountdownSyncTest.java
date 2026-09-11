package com.grahambartley.notenougharrows.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CountdownSyncTest {
  private static final int CARRIER = 11;
  private static final int OTHER_CARRIER = 12;

  @BeforeEach
  @AfterEach
  void reset() {
    CountdownSync.clear();
  }

  @Test
  void aBurningFuseIsRemembered() {
    CountdownSync.accept(CARRIER, 60, 60);

    assertEquals(List.of(new Countdown(CARRIER, 60, 60)), CountdownSync.burning());
  }

  @Test
  void aFuseWithNothingLeftIsDroppedRatherThanKept() {
    CountdownSync.accept(CARRIER, 60, 60);
    CountdownSync.accept(CARRIER, 0, 0);

    assertTrue(CountdownSync.burning().isEmpty());
  }

  @Test
  void burningDownTakesATickOffEveryFuse() {
    CountdownSync.accept(CARRIER, 60, 60);
    CountdownSync.accept(OTHER_CARRIER, 40, 40);

    CountdownSync.burnDown();

    assertEquals(
        List.of(new Countdown(CARRIER, 60, 59), new Countdown(OTHER_CARRIER, 40, 39)),
        CountdownSync.burning());
  }

  @Test
  void aFuseThatRunsOutStopsBeingListed() {
    CountdownSync.accept(CARRIER, 60, 1);

    CountdownSync.burnDown();
    CountdownSync.burnDown();

    assertTrue(CountdownSync.burning().isEmpty());
  }

  @Test
  void aForgottenCarrierIsDropped() {
    CountdownSync.accept(CARRIER, 60, 60);

    CountdownSync.forget(CARRIER);

    assertTrue(CountdownSync.burning().isEmpty());
  }

  @Test
  void anUpdateForTheSameCarrierReplacesTheOldOne() {
    CountdownSync.accept(CARRIER, 60, 60);
    CountdownSync.accept(CARRIER, 40, 40);

    assertEquals(List.of(new Countdown(CARRIER, 40, 40)), CountdownSync.burning());
  }
}
