package com.grahambartley.morearrows.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FletchingSlotMatcherTest {

  static List<Arguments> assignments() {
    return List.of(
        Arguments.of("no ingredients and no occupied slots", List.of(), List.of(), true),
        Arguments.of(
            "one ingredient taking the only occupied slot", List.of(List.of(0)), List.of(0), true),
        Arguments.of(
            "two ingredients each accepting a different slot",
            List.of(List.of(0), List.of(1)),
            List.of(0, 1),
            true),
        Arguments.of(
            "two ingredients that both accept both slots",
            List.of(List.of(0, 1), List.of(0, 1)),
            List.of(0, 1),
            true),
        Arguments.of(
            "ingredients declared in the opposite order to the slots they fill",
            List.of(List.of(1), List.of(0)),
            List.of(0, 1),
            true),
        Arguments.of(
            "a greedy first choice that only works after backtracking",
            List.of(List.of(0, 1), List.of(0)),
            List.of(0, 1),
            true),
        Arguments.of(
            "three ingredients where only one assignment works",
            List.of(List.of(0, 1, 2), List.of(1, 2), List.of(2)),
            List.of(0, 1, 2),
            true),
        Arguments.of(
            "two ingredients competing for the same single slot",
            List.of(List.of(0), List.of(0)),
            List.of(0, 1),
            false),
        Arguments.of(
            "an ingredient no occupied slot satisfies",
            List.of(List.of(0), List.of()),
            List.of(0, 1),
            false),
        Arguments.of(
            "more occupied slots than declared ingredients",
            List.of(List.of(0, 1)),
            List.of(0, 1),
            false),
        Arguments.of(
            "more declared ingredients than occupied slots",
            List.of(List.of(0), List.of(0)),
            List.of(0),
            false),
        Arguments.of(
            "candidates naming a slot that is not occupied",
            List.of(List.of(3)),
            List.of(0),
            false));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("assignments")
  void decidesWhetherEveryIngredientCanClaimItsOwnOccupiedSlot(
      final String description,
      final List<List<Integer>> candidateSlotsPerIngredient,
      final List<Integer> occupiedSlots,
      final boolean expected) {
    assertTrue(
        expected == FletchingSlotMatcher.matchesExactly(candidateSlotsPerIngredient, occupiedSlots),
        description);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("assignments")
  void namesTheSlotEachIngredientClaimsWheneverAnAssignmentExists(
      final String description,
      final List<List<Integer>> candidateSlotsPerIngredient,
      final List<Integer> occupiedSlots,
      final boolean expected) {
    final Optional<List<Integer>> assignment =
        FletchingSlotMatcher.assign(candidateSlotsPerIngredient, occupiedSlots);

    assertEquals(expected, assignment.isPresent(), description);
    assignment.ifPresent(
        slots -> {
          assertEquals(candidateSlotsPerIngredient.size(), slots.size(), description);
          assertEquals(slots.size(), new HashSet<>(slots).size(), description);
          for (int index = 0; index < slots.size(); index++) {
            assertTrue(
                candidateSlotsPerIngredient.get(index).contains(slots.get(index)), description);
            assertTrue(occupiedSlots.contains(slots.get(index)), description);
          }
        });
  }

  @Test
  void claimsTheOnlyWorkableSlotForEachIngredientRatherThanTheFirstOneOffered() {
    assertEquals(
        Optional.of(List.of(0, 1, 2)),
        FletchingSlotMatcher.assign(
            List.of(List.of(0, 1, 2), List.of(1, 2), List.of(2)), List.of(0, 1, 2)));
  }

  @Test
  void reportsNoAssignmentWhenTwoIngredientsCompeteForOneSlot() {
    assertEquals(
        Optional.empty(),
        FletchingSlotMatcher.assign(List.of(List.of(0), List.of(0)), List.of(0, 1)));
  }

  @Test
  void ignoresDuplicateCandidateSlotsRatherThanCountingThemTwice() {
    assertFalse(
        FletchingSlotMatcher.matchesExactly(List.of(List.of(0, 0), List.of(0, 0)), List.of(0, 1)));
  }

  @Test
  void acceptsAnySlotCollectionRatherThanRequiringLists() {
    assertTrue(FletchingSlotMatcher.matchesExactly(List.of(Set.of(1), Set.of(0)), Set.of(0, 1)));
  }

  @Test
  void rejectsNullArguments() {
    assertThrows(
        NullPointerException.class, () -> FletchingSlotMatcher.matchesExactly(null, List.of()));
    assertThrows(
        NullPointerException.class, () -> FletchingSlotMatcher.matchesExactly(List.of(), null));
    assertThrows(NullPointerException.class, () -> FletchingSlotMatcher.assign(null, List.of()));
    assertThrows(NullPointerException.class, () -> FletchingSlotMatcher.assign(List.of(), null));
  }
}
