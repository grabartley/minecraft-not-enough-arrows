package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class ZiplineOutcomeTest {

  @Test
  void onlyStringingASpanSpendsTheArrow() {
    for (final ZiplineOutcome outcome : ZiplineOutcome.values()) {
      assertEquals(outcome == ZiplineOutcome.STRUNG, outcome.spendsTheArrow(), outcome.name());
    }
  }

  @Test
  void aRefusalIsToldForWhatItWas() {
    assertEquals(ZiplineOutcome.TOO_FAR, ZiplineOutcome.of(SpanRefusal.TOO_FAR));
    assertEquals(ZiplineOutcome.TOO_SHORT, ZiplineOutcome.of(SpanRefusal.TOO_SHORT));
    assertEquals(ZiplineOutcome.BLOCKED, ZiplineOutcome.of(SpanRefusal.BLOCKED));
  }

  @Test
  void refusalsAPlayerCannotActOnAreToldAsBlocked() {
    assertEquals(ZiplineOutcome.BLOCKED, ZiplineOutcome.of(SpanRefusal.NOT_AN_ANCHOR));
    assertEquals(ZiplineOutcome.BLOCKED, ZiplineOutcome.of(SpanRefusal.NOT_STRUNG));
    assertEquals(ZiplineOutcome.BLOCKED, ZiplineOutcome.of(null));
  }

  @Test
  void noRefusalSpendsTheArrow() {
    for (final SpanRefusal refusal : SpanRefusal.values()) {
      assertFalse(ZiplineOutcome.of(refusal).spendsTheArrow(), refusal.name());
    }
  }
}
