package com.grahambartley.notenougharrows.arrow;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class ArrowTagAudit {

  private ArrowTagAudit() {}

  public static List<Identifier> unfireable(
      final Collection<Identifier> registered, final Collection<Identifier> tagged) {
    Objects.requireNonNull(registered, "registered");
    Objects.requireNonNull(tagged, "tagged");

    final Set<Identifier> nockable = Set.copyOf(tagged);
    return registered.stream().filter(id -> !nockable.contains(id)).toList();
  }
}
