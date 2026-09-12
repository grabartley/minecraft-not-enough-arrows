package com.grahambartley.notenougharrows.fuse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FuseCadenceTest {
  private static final int DEFAULT_DELAY_TICKS = 60;

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -60})
  void aFuseThatNeverBurnsIsNeverScheduledToBeep(final int delayTicks) {
    assertEquals(List.of(), FuseCadence.beepTicks(delayTicks));
  }

  @Test
  void theFirstBeepLandsTheMomentTheFuseIsLit() {
    assertEquals(
        DEFAULT_DELAY_TICKS, FuseCadence.beepTicks(DEFAULT_DELAY_TICKS).getFirst().intValue());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 20, 40, 60, 100, 200})
  void everyScheduledBeepFallsInsideTheFuseItBelongsTo(final int delayTicks) {
    for (final int beep : FuseCadence.beepTicks(delayTicks)) {
      assertTrue(
          beep > 0 && beep <= delayTicks, "Beep at " + beep + " of a " + delayTicks + " fuse");
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {40, 50, 60, 100, 200})
  void theGapBetweenBeepsOnlyEverShortens(final int delayTicks) {
    final List<Integer> beeps = FuseCadence.beepTicks(delayTicks);

    int previousGap = Integer.MAX_VALUE;
    for (int beep = 1; beep < beeps.size(); beep++) {
      final int gap = beeps.get(beep - 1) - beeps.get(beep);
      assertTrue(
          gap <= previousGap, "Gap " + gap + " should not exceed previous gap " + previousGap);
      previousGap = gap;
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {40, 50, 60, 100, 200})
  void theLastGapBeforeDetonationIsShorterThanTheFirst(final int delayTicks) {
    final List<Integer> beeps = FuseCadence.beepTicks(delayTicks);

    final int firstGap = beeps.get(0) - beeps.get(1);
    final int lastGap = beeps.get(beeps.size() - 2) - beeps.get(beeps.size() - 1);
    assertTrue(lastGap < firstGap, "Last gap " + lastGap + " should beat first gap " + firstGap);
  }

  @Test
  void aDefaultLengthFuseBeepsOnTheScheduleItAdvertises() {
    assertEquals(List.of(60, 40, 26, 16, 9, 4, 1), FuseCadence.beepTicks(DEFAULT_DELAY_TICKS));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 20, 40, 50, 60, 100, 200})
  void askingWhetherATickBeepsAgreesWithTheSchedule(final int delayTicks) {
    final List<Integer> beeps = FuseCadence.beepTicks(delayTicks);

    for (int remaining = 0; remaining <= delayTicks; remaining++) {
      assertEquals(
          beeps.contains(remaining),
          FuseCadence.beepsAt(remaining, delayTicks),
          "Tick " + remaining + " of a " + delayTicks + " fuse");
    }
  }

  @ParameterizedTest
  @CsvSource({"61, 60", "-1, 60", "0, 60", "10, 0", "10, -5"})
  void aTickOutsideTheFuseNeverBeeps(final int remainingTicks, final int delayTicks) {
    assertFalse(FuseCadence.beepsAt(remainingTicks, delayTicks));
  }

  @Test
  void theIntervalOpensAtItsSlowestAndClosesAtItsFastest() {
    assertEquals(
        FuseCadence.SLOWEST_INTERVAL_TICKS,
        FuseCadence.intervalAt(DEFAULT_DELAY_TICKS, DEFAULT_DELAY_TICKS));
    assertEquals(
        FuseCadence.FASTEST_INTERVAL_TICKS, FuseCadence.intervalAt(0, DEFAULT_DELAY_TICKS));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 20, 60, 200})
  void theIntervalNeverLeavesItsBounds(final int delayTicks) {
    for (int remaining = -5; remaining <= delayTicks + 5; remaining++) {
      final int interval = FuseCadence.intervalAt(remaining, delayTicks);
      assertTrue(
          interval >= FuseCadence.FASTEST_INTERVAL_TICKS
              && interval <= FuseCadence.SLOWEST_INTERVAL_TICKS,
          "Interval " + interval + " at " + remaining + " of " + delayTicks);
    }
  }
}
