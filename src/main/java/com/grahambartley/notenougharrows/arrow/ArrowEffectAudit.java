package com.grahambartley.notenougharrows.arrow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class ArrowEffectAudit {

  private ArrowEffectAudit() {}

  public static List<Identifier> soldAsTippedArrows(
      final Map<Identifier, List<ArrowEffect>> declared, final Collection<Identifier> brewable) {
    Objects.requireNonNull(declared, "declared");
    Objects.requireNonNull(brewable, "brewable");

    final Set<Identifier> potionEffects = Set.copyOf(brewable);
    return declared.entrySet().stream()
        .filter(
            entry ->
                entry.getValue().stream()
                    .anyMatch(effect -> potionEffects.contains(effect.effect())))
        .map(Map.Entry::getKey)
        .toList();
  }

  public static List<Identifier> reproducingEachOther(
      final Map<Identifier, List<ArrowEffect>> declared) {
    Objects.requireNonNull(declared, "declared");

    final Map<Set<ArrowEffect>, List<Identifier>> arrowsBySet = new LinkedHashMap<>();
    declared.forEach(
        (arrow, effects) -> {
          if (!effects.isEmpty()) {
            arrowsBySet.computeIfAbsent(Set.copyOf(effects), key -> new ArrayList<>()).add(arrow);
          }
        });
    final Set<Identifier> duplicated = new LinkedHashSet<>();
    arrowsBySet.values().stream().filter(arrows -> arrows.size() > 1).forEach(duplicated::addAll);
    return List.copyOf(duplicated);
  }
}
