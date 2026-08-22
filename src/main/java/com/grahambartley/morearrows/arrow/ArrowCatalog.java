package com.grahambartley.morearrows.arrow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ArrowCatalog {
  private final Map<String, ArrowDefinition<?>> byPath = new LinkedHashMap<>();

  public void add(final ArrowDefinition<?> definition) {
    Objects.requireNonNull(definition, "definition");

    final ArrowDefinition<?> existing = byPath.putIfAbsent(definition.path(), definition);
    if (existing != null) {
      throw new IllegalArgumentException(
          "Arrow path '" + definition.path() + "' is already registered");
    }
  }

  public List<ArrowDefinition<?>> definitions() {
    return List.copyOf(byPath.values());
  }

  public Optional<ArrowDefinition<?>> find(final String path) {
    return Optional.ofNullable(byPath.get(path));
  }

  public boolean isEmpty() {
    return byPath.isEmpty();
  }

  public int size() {
    return byPath.size();
  }
}
