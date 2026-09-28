package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpanTrackerTest {
  private static final BlockPos CABLE_A = new BlockPos(1, 64, 0);
  private static final BlockPos CABLE_B = new BlockPos(2, 64, 0);
  private static final BlockPos ELSEWHERE = new BlockPos(9, 64, 9);

  private SpanTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new SpanTracker();
  }

  @Test
  void findsASpanByAnyLengthOfItsCable() {
    final Span span = span(100L, CABLE_A, CABLE_B);
    tracker.add(span);

    assertEquals(span, tracker.at(CABLE_A).orElseThrow());
    assertEquals(span, tracker.at(CABLE_B).orElseThrow());
    assertTrue(tracker.at(ELSEWHERE).isEmpty());
    assertTrue(tracker.at(null).isEmpty());
  }

  @Test
  void findsASpanById() {
    final Span span = span(100L, CABLE_A);
    tracker.add(span);

    assertEquals(span, tracker.find(span.id()).orElseThrow());
    assertTrue(tracker.find(UUID.randomUUID()).isEmpty());
    assertTrue(tracker.find(null).isEmpty());
  }

  @Test
  void theSameSpanIsRecordedOnce() {
    final Span span = span(100L, CABLE_A);
    tracker.add(span);
    tracker.add(span);
    tracker.add(null);

    assertEquals(1, tracker.size());
  }

  @Test
  void expiredSpansAreForgottenWithTheirCable() {
    final Span early = span(10L, CABLE_A);
    final Span late = span(20L, CABLE_B);
    tracker.add(early);
    tracker.add(late);

    assertEquals(List.of(early), tracker.removeExpired(10L));
    assertTrue(tracker.at(CABLE_A).isEmpty());
    assertEquals(late, tracker.at(CABLE_B).orElseThrow());
  }

  @Test
  void aNewerSpanOverTheSameCableKeepsItWhenTheOlderOneExpires() {
    final Span older = span(10L, CABLE_A);
    final Span newer = span(20L, CABLE_A);
    tracker.add(older);
    tracker.add(newer);

    tracker.removeExpired(10L);

    assertEquals(newer, tracker.at(CABLE_A).orElseThrow());
  }

  @Test
  void startsEmpty() {
    assertTrue(tracker.isEmpty());
  }

  private static Span span(final long expiryTick, final BlockPos... cable) {
    return new Span(UUID.randomUUID(), List.of(cable), expiryTick);
  }
}
