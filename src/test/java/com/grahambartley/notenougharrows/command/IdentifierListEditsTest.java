package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.config.ConfigValues;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class IdentifierListEditsTest {
  private static final int MAX_ENTRIES = 4;

  @Test
  void addingABlockPutsItOnTheList() {
    final IdentifierListEdits.Result result =
        IdentifierListEdits.add(List.of(), "minecraft:sand", MAX_ENTRIES);

    assertEquals(IdentifierListEdits.Outcome.ADDED, result.outcome());
    assertEquals(List.of("minecraft:sand"), result.updated());
  }

  @ParameterizedTest(name = "\"{0}\" is stored as \"{1}\"")
  @CsvSource({
    "'  minecraft:sand  ', minecraft:sand",
    "'MINECRAFT:SAND',     minecraft:sand",
    "'sand',               minecraft:sand",
  })
  void anIdentifierIsNormalisedBeforeItIsStored(String raw, String expected) {
    assertEquals(List.of(expected), IdentifierListEdits.add(List.of(), raw, MAX_ENTRIES).updated());
  }

  @ParameterizedTest(name = "rejects \"{0}\"")
  @ValueSource(strings = {"not an id", "minecraft::sand", "minecraft:sand!"})
  void anIdentifierThatCannotBeParsedIsRejected(String raw) {
    assertEquals(
        IdentifierListEdits.Outcome.INVALID_ID,
        IdentifierListEdits.add(List.of(), raw, MAX_ENTRIES).outcome());
  }

  @Test
  void anIdentifierLongerThanTheCapIsRejected() {
    final String overCap = "minecraft:" + "a".repeat(ConfigValues.MAX_IDENTIFIER_LENGTH - 9);

    assertEquals(
        IdentifierListEdits.Outcome.TOO_LONG,
        IdentifierListEdits.add(List.of(), overCap, MAX_ENTRIES).outcome());
  }

  @Test
  void anIdentifierAtTheCapIsAccepted() {
    final String atCap = "minecraft:" + "a".repeat(ConfigValues.MAX_IDENTIFIER_LENGTH - 10);

    assertEquals(List.of(atCap), IdentifierListEdits.add(List.of(), atCap, MAX_ENTRIES).updated());
  }

  @Test
  void aNullIdentifierIsRejectedRatherThanThrowing() {
    assertNull(IdentifierListEdits.normalize(null));
  }

  @Test
  void addingABlockThatIsAlreadyExcludedIsRejected() {
    final List<String> current = List.of("minecraft:sand");

    final IdentifierListEdits.Result result =
        IdentifierListEdits.add(current, "minecraft:sand", MAX_ENTRIES);

    assertEquals(IdentifierListEdits.Outcome.ALREADY_PRESENT, result.outcome());
    assertSame(current, result.updated());
  }

  @Test
  void addingBeyondTheConfiguredCapIsRejected() {
    final List<String> full = new ArrayList<>();
    for (int i = 0; i < MAX_ENTRIES; i++) {
      full.add("not-enough-arrows:block_" + i);
    }

    assertEquals(
        IdentifierListEdits.Outcome.LIST_FULL,
        IdentifierListEdits.add(List.copyOf(full), "minecraft:sand", MAX_ENTRIES).outcome());
  }

  @Test
  void aListBelowItsCapStillAcceptsAnEntry() {
    final List<String> almostFull = List.of("minecraft:a", "minecraft:b", "minecraft:c");

    assertEquals(
        IdentifierListEdits.Outcome.ADDED,
        IdentifierListEdits.add(almostFull, "minecraft:sand", MAX_ENTRIES).outcome());
  }

  @Test
  void removingABlockTakesItOffTheList() {
    final IdentifierListEdits.Result result =
        IdentifierListEdits.remove(List.of("minecraft:sand", "minecraft:gravel"), "minecraft:sand");

    assertEquals(IdentifierListEdits.Outcome.REMOVED, result.outcome());
    assertEquals(List.of("minecraft:gravel"), result.updated());
  }

  @Test
  void removingABlockThatWasNeverExcludedIsRejected() {
    assertEquals(
        IdentifierListEdits.Outcome.NOT_PRESENT,
        IdentifierListEdits.remove(List.of("minecraft:sand"), "minecraft:gravel").outcome());
  }

  @Test
  void clearingEmptiesTheList() {
    final IdentifierListEdits.Result result = IdentifierListEdits.clear();

    assertEquals(IdentifierListEdits.Outcome.CLEARED, result.outcome());
    assertTrue(result.updated().isEmpty());
  }

  @ParameterizedTest(name = "{0} succeeded -> {1}")
  @CsvSource({
    "ADDED,           true",
    "REMOVED,         true",
    "CLEARED,         true",
    "INVALID_ID,      false",
    "TOO_LONG,        false",
    "ALREADY_PRESENT, false",
    "NOT_PRESENT,     false",
    "LIST_FULL,       false",
  })
  void onlyEditsThatChangedTheListCountAsSuccess(
      IdentifierListEdits.Outcome outcome, boolean expected) {
    assertEquals(expected, outcome.succeeded());
  }
}
