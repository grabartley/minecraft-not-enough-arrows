package com.grahambartley.notenougharrows.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class FletchingSlotMatcher {
  public static final int UNASSIGNED = -1;

  private FletchingSlotMatcher() {}

  public static Optional<List<Integer>> assign(
      final List<? extends Collection<Integer>> candidateSlotsPerIngredient,
      final Collection<Integer> occupiedSlots) {
    Objects.requireNonNull(candidateSlotsPerIngredient, "candidateSlotsPerIngredient");
    Objects.requireNonNull(occupiedSlots, "occupiedSlots");

    final Set<Integer> occupied = new HashSet<>(occupiedSlots);
    if (candidateSlotsPerIngredient.size() != occupied.size()) {
      return Optional.empty();
    }

    final List<List<Integer>> candidates =
        candidateSlotsPerIngredient.stream()
            .map(slots -> slots.stream().filter(occupied::contains).distinct().toList())
            .toList();
    final List<Integer> assignment =
        new ArrayList<>(Collections.nCopies(candidates.size(), UNASSIGNED));
    return assign(candidates, 0, new HashSet<>(), assignment)
        ? Optional.of(List.copyOf(assignment))
        : Optional.empty();
  }

  private static boolean assign(
      final List<List<Integer>> candidates,
      final int ingredientIndex,
      final Set<Integer> taken,
      final List<Integer> assignment) {
    if (ingredientIndex == candidates.size()) {
      return true;
    }
    for (final Integer slot : candidates.get(ingredientIndex)) {
      if (taken.add(slot)) {
        assignment.set(ingredientIndex, slot);
        if (assign(candidates, ingredientIndex + 1, taken, assignment)) {
          return true;
        }
        assignment.set(ingredientIndex, UNASSIGNED);
        taken.remove(slot);
      }
    }
    return false;
  }
}
