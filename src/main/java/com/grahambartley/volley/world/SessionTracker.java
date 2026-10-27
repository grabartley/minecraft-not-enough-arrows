package com.grahambartley.volley.world;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public final class SessionTracker<S> {
  private final Map<UUID, S> sessions = new LinkedHashMap<>();
  private final Function<S, UUID> keyOf;
  private final Predicate<S> hasExpired;

  public SessionTracker(final Function<S, UUID> keyOf, final Predicate<S> hasExpired) {
    this.keyOf = keyOf;
    this.hasExpired = hasExpired;
  }

  public void add(@Nullable final S session) {
    if (session == null || hasExpired.test(session)) {
      return;
    }
    sessions.put(keyOf.apply(session), session);
  }

  @Nullable
  public S get(@Nullable final UUID key) {
    return key == null ? null : sessions.get(key);
  }

  @Nullable
  public S remove(@Nullable final UUID key) {
    return key == null ? null : sessions.remove(key);
  }

  public List<S> removeIf(final Predicate<S> matching) {
    final List<S> removed = sessions.values().stream().filter(matching).toList();
    removed.forEach(session -> sessions.remove(keyOf.apply(session)));
    return removed;
  }

  public List<S> all() {
    return List.copyOf(sessions.values());
  }

  public int size() {
    return sessions.size();
  }

  public boolean isEmpty() {
    return sessions.isEmpty();
  }
}
