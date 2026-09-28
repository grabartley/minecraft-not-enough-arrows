package com.grahambartley.notenougharrows.tow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.grapple.GrappleProgress;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TowSessionTest {
  private static final UUID TARGET = UUID.randomUUID();
  private static final UUID SHOOTER = UUID.randomUUID();

  @Test
  void aTowStartsWithItsWholeBudget() {
    final TowSession tow = TowSession.beginning(TARGET, SHOOTER, 100, 12.0);

    assertEquals(100, tow.remainingTicks());
    assertEquals(0, tow.pulledTicks());
  }

  @Test
  void pullingOneTickSpendsOneTickOfTheBudget() {
    final TowSession pulled = TowSession.beginning(TARGET, SHOOTER, 100, 12.0).pulled(11.5);

    assertEquals(99, pulled.remainingTicks());
    assertEquals(1, pulled.pulledTicks());
  }

  @Test
  void aTowRunsOutWhenItsBudgetIsSpent() {
    final TowSession lastTick = TowSession.beginning(TARGET, SHOOTER, 1, 12.0);

    assertFalse(lastTick.hasExpired());
    assertTrue(lastTick.pulled(11.0).hasExpired());
  }

  @Test
  void aTargetThatStopsComingIsObstructed() {
    TowSession tow = TowSession.beginning(TARGET, SHOOTER, 500, 12.0);
    for (int tick = 0; tick < GrappleProgress.IDLE_TICKS_LIMIT; tick++) {
      tow = tow.pulled(12.0);
    }

    assertTrue(tow.hasStopped());
  }

  @Test
  void aTowInvolvesBothItsEnds() {
    final TowSession tow = TowSession.beginning(TARGET, SHOOTER, 100, 12.0);

    assertTrue(tow.involves(TARGET));
    assertTrue(tow.involves(SHOOTER));
    assertFalse(tow.involves(UUID.randomUUID()));
  }

  @Test
  void aTowNeedsBothEnds() {
    assertThrows(NullPointerException.class, () -> TowSession.beginning(null, SHOOTER, 1, 1.0));
    assertThrows(NullPointerException.class, () -> TowSession.beginning(TARGET, null, 1, 1.0));
  }
}
