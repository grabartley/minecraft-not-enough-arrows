package com.grahambartley.morearrows.recipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class FletchingSlotMatcher {

  private FletchingSlotMatcher() {}

  public static boolean matchesExactly(
      final List<? extends Collection<Integer>> candidateSlotsPerIngredient,
      final Collection<Integer> occupiedSlots) {
    Objects.requireNonNull(candidateSlotsPerIngredient, "candidateSlotsPerIngredient");
    Objects.requireNonNull(occupiedSlots, "occupiedSlots");

    final Set<Integer> occupied = new HashSet<>(occupiedSlots);
    if (candidateSlotsPerIngredient.size() != occupied.size()) {
      return false;
    }

    final List<List<Integer>> candidates =
        candidateSlotsPerIngredient.stream()
            .map(slots -> slots.stream().filter(occupied::contains).distinct().toList())
            .toList();
    return assign(candidates, 0, new HashSet<>());
  }

  private static boolean assign(
      final List<List<Integer>> candidates, final int ingredientIndex, final Set<Integer> taken) {
    if (ingredientIndex == candidates.size()) {
      return true;
    }
    for (final Integer slot : candidates.get(ingredientIndex)) {
      if (taken.add(slot)) {
        if (assign(candidates, ingredientIndex + 1, taken)) {
          return true;
        }
        taken.remove(slot);
      }
    }
    return false;
  }
}
