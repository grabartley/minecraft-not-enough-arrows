package com.grahambartley.notenougharrows.arrow;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class ArrowTextureAudit {

  private ArrowTextureAudit() {}

  public static List<Identifier> missing(final Map<Identifier, Optional<String>> digests) {
    Objects.requireNonNull(digests, "digests");
    return digests.entrySet().stream()
        .filter(entry -> entry.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .toList();
  }

  public static List<Identifier> shared(final Map<Identifier, Optional<String>> digests) {
    Objects.requireNonNull(digests, "digests");

    final Map<String, Identifier> firstByDigest = new LinkedHashMap<>();
    final Set<Identifier> sharing = new LinkedHashSet<>();
    digests.forEach(
        (arrow, digest) ->
            digest.ifPresent(
                value -> {
                  final Identifier first = firstByDigest.putIfAbsent(value, arrow);
                  if (first != null) {
                    sharing.add(first);
                    sharing.add(arrow);
                  }
                }));
    return List.copyOf(sharing);
  }
}
