package com.grahambartley.morearrows.screen;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class ConfigDraft<S> {
  private final S original;
  private S current;

  public ConfigDraft(final S original) {
    this.original = Objects.requireNonNull(original, "original");
    this.current = original;
  }

  public S original() {
    return original;
  }

  public S current() {
    return current;
  }

  public void set(final S value) {
    current = Objects.requireNonNull(value, "value");
  }

  public void apply(final UnaryOperator<S> change) {
    set(change.apply(current));
  }

  public boolean isDirty() {
    return !original.equals(current);
  }

  public void revert() {
    current = original;
  }
}
