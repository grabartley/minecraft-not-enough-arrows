package com.grahambartley.notenougharrows.zipline;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record SpanResult(@Nullable Span span, @Nullable SpanRefusal refusal) {

  public static SpanResult strung(final Span span) {
    return new SpanResult(span, null);
  }

  public static SpanResult refused(final SpanRefusal refusal) {
    return new SpanResult(null, refusal);
  }

  public boolean wasStrung() {
    return span != null;
  }

  public Optional<Span> strungSpan() {
    return Optional.ofNullable(span);
  }
}
