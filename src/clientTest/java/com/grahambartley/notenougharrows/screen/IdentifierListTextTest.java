package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class IdentifierListTextTest {

  @Test
  void joinsEntriesForDisplayInATextField() {
    assertEquals(
        "minecraft:stone, minecraft:dirt",
        IdentifierListText.join(List.of("minecraft:stone", "minecraft:dirt")));
  }

  @Test
  void joinsAnEmptyListToAnEmptyField() {
    assertEquals("", IdentifierListText.join(List.of()));
    assertEquals("", IdentifierListText.join(null));
  }

  @Test
  void splitsTypedTextBackIntoEntries() {
    assertEquals(
        List.of("minecraft:stone", "minecraft:dirt"),
        IdentifierListText.split("minecraft:stone, minecraft:dirt"));
  }

  @Test
  void toleratesRaggedSpacingAndTrailingSeparators() {
    assertEquals(
        List.of("minecraft:stone", "minecraft:dirt"),
        IdentifierListText.split("  minecraft:stone ,,  minecraft:dirt ,  "));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   ", ",", " , , "})
  void readsAnEmptyFieldAsNoEntries(final String text) {
    assertEquals(List.of(), IdentifierListText.split(text));
  }
}
