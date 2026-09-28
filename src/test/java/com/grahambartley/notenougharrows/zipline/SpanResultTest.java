package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class SpanResultTest {

  @Test
  void aStrungResultCarriesItsSpan() {
    final Span span = new Span(UUID.randomUUID(), List.of(BlockPos.ORIGIN), 1L);

    final SpanResult result = SpanResult.strung(span);

    assertTrue(result.wasStrung());
    assertEquals(span, result.strungSpan().orElseThrow());
    assertNull(result.refusal());
  }

  @Test
  void aRefusedResultCarriesItsReasonAndNoSpan() {
    final SpanResult result = SpanResult.refused(SpanRefusal.TOO_FAR);

    assertFalse(result.wasStrung());
    assertTrue(result.strungSpan().isEmpty());
    assertEquals(SpanRefusal.TOO_FAR, result.refusal());
  }
}
