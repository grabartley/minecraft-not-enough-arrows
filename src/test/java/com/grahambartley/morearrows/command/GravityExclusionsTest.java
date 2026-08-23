package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class GravityExclusionsTest {

  @Test
  void addingABlockPutsItOnTheList() {
    final GravityExclusions.Result result = GravityExclusions.add(List.of(), "minecraft:sand");

    assertEquals(GravityExclusions.Outcome.ADDED, result.outcome());
    assertEquals(List.of("minecraft:sand"), result.updated());
  }

  @ParameterizedTest(name = "\"{0}\" is stored as \"{1}\"")
  @CsvSource({
    "'  minecraft:sand  ', minecraft:sand",
    "'MINECRAFT:SAND',     minecraft:sand",
    "'sand',               minecraft:sand",
  })
  void anIdentifierIsNormalisedBeforeItIsStored(String raw, String expected) {
    assertEquals(List.of(expected), GravityExclusions.add(List.of(), raw).updated());
  }

  @ParameterizedTest(name = "rejects \"{0}\"")
  @ValueSource(strings = {"not an id", "minecraft::sand", "minecraft:sand!"})
  void anIdentifierThatCannotBeParsedIsRejected(String raw) {
    assertEquals(
        GravityExclusions.Outcome.INVALID_ID, GravityExclusions.add(List.of(), raw).outcome());
  }

  @Test
  void aNullIdentifierIsRejectedRatherThanThrowing() {
    assertNull(GravityExclusions.normalize(null));
  }

  @Test
  void addingABlockThatIsAlreadyExcludedIsRejected() {
    final List<String> current = List.of("minecraft:sand");

    final GravityExclusions.Result result = GravityExclusions.add(current, "minecraft:sand");

    assertEquals(GravityExclusions.Outcome.ALREADY_PRESENT, result.outcome());
    assertSame(current, result.updated());
  }

  @Test
  void addingBeyondTheConfiguredCapIsRejected() {
    final List<String> full = new ArrayList<>();
    for (int i = 0; i < PhysicsArrowConfig.GRAVITY_BLOCK_EXCLUSIONS_MAX; i++) {
      full.add("more-arrows:block_" + i);
    }

    assertEquals(
        GravityExclusions.Outcome.LIST_FULL,
        GravityExclusions.add(List.copyOf(full), "minecraft:sand").outcome());
  }

  @Test
  void removingABlockTakesItOffTheList() {
    final GravityExclusions.Result result =
        GravityExclusions.remove(List.of("minecraft:sand", "minecraft:gravel"), "minecraft:sand");

    assertEquals(GravityExclusions.Outcome.REMOVED, result.outcome());
    assertEquals(List.of("minecraft:gravel"), result.updated());
  }

  @Test
  void removingABlockThatWasNeverExcludedIsRejected() {
    assertEquals(
        GravityExclusions.Outcome.NOT_PRESENT,
        GravityExclusions.remove(List.of("minecraft:sand"), "minecraft:gravel").outcome());
  }

  @Test
  void clearingEmptiesTheList() {
    final GravityExclusions.Result result = GravityExclusions.clear(List.of("minecraft:sand"));

    assertEquals(GravityExclusions.Outcome.CLEARED, result.outcome());
    assertTrue(result.updated().isEmpty());
  }

  @ParameterizedTest(name = "{0} succeeded -> {1}")
  @CsvSource({
    "ADDED,           true",
    "REMOVED,         true",
    "CLEARED,         true",
    "INVALID_ID,      false",
    "ALREADY_PRESENT, false",
    "NOT_PRESENT,     false",
    "LIST_FULL,       false",
  })
  void onlyEditsThatChangedTheListCountAsSuccess(
      GravityExclusions.Outcome outcome, boolean expected) {
    assertEquals(expected, outcome.succeeded());
  }
}
