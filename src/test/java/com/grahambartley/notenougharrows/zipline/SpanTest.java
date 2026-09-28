package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class SpanTest {
  private static final BlockPos FIRST = new BlockPos(1, 64, 0);
  private static final BlockPos MIDDLE = new BlockPos(2, 64, 0);
  private static final BlockPos LAST = new BlockPos(3, 64, 0);
  private static final List<BlockPos> CABLE = List.of(FIRST, MIDDLE, LAST);

  @Test
  void itsEndsAreTheCentresOfTheOutermostCable() {
    final Span span = new Span(UUID.randomUUID(), CABLE, 100L);

    assertEquals(Vec3d.ofCenter(FIRST), span.firstEnd());
    assertEquals(Vec3d.ofCenter(LAST), span.lastEnd());
  }

  @Test
  void itExpiresOnItsExpiryTick() {
    final Span span = new Span(UUID.randomUUID(), CABLE, 100L);

    assertFalse(span.hasExpired(99L));
    assertTrue(span.hasExpired(100L));
  }

  @Test
  void itIsWholeOnlyWhileEveryLengthOfCableStillStands() {
    final Span span = new Span(UUID.randomUUID(), CABLE, 100L);

    assertTrue(span.isWhole(List.of(LAST, FIRST, MIDDLE)));
    assertFalse(span.isWhole(List.of(FIRST, LAST)));
    assertFalse(span.isWhole(null));
  }

  @Test
  void aSpanWithNoCableIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new Span(UUID.randomUUID(), List.of(), 1L));
    assertThrows(IllegalArgumentException.class, () -> new Span(UUID.randomUUID(), null, 1L));
  }

  @Test
  void aSpanNeedsTheStructureItWasStrungAs() {
    assertThrows(NullPointerException.class, () -> new Span(null, CABLE, 1L));
  }
}
