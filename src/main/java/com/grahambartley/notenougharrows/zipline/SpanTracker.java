package com.grahambartley.notenougharrows.zipline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;

public final class SpanTracker {
  private final Map<UUID, Span> spans = new HashMap<>();
  private final Map<BlockPos, UUID> spansByCable = new HashMap<>();

  public void add(final Span span) {
    if (span == null || spans.containsKey(span.id())) {
      return;
    }
    spans.put(span.id(), span);
    span.cable().forEach(pos -> spansByCable.put(pos, span.id()));
  }

  public Optional<Span> at(final BlockPos pos) {
    final UUID id = pos == null ? null : spansByCable.get(pos);
    return Optional.ofNullable(id == null ? null : spans.get(id));
  }

  public Optional<Span> find(final UUID id) {
    return Optional.ofNullable(id == null ? null : spans.get(id));
  }

  public List<Span> removeExpired(final long tick) {
    final List<Span> expired =
        spans.values().stream().filter(span -> span.hasExpired(tick)).toList();
    expired.forEach(this::forget);
    return expired;
  }

  public int size() {
    return spans.size();
  }

  public boolean isEmpty() {
    return spans.isEmpty();
  }

  private void forget(final Span span) {
    spans.remove(span.id());
    span.cable().forEach(pos -> spansByCable.remove(pos, span.id()));
  }
}
