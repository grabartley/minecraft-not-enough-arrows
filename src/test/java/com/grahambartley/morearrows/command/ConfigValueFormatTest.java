package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConfigValueFormatTest {

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({"0, 0", "60, 60", "-3, -3"})
  void integersAreRenderedPlainly(int value, String expected) {
    assertEquals(expected, ConfigValueFormat.of(value));
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({"true, true", "false, false"})
  void booleansAreRenderedPlainly(boolean value, String expected) {
    assertEquals(expected, ConfigValueFormat.of(value));
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({"0.8, 0.80", "1, 1.00", "4.567, 4.57"})
  void floatsAreRenderedToTwoDecimalPlaces(float value, String expected) {
    assertEquals(expected, ConfigValueFormat.of(value));
  }

  @Test
  void anEmptyListReadsAsNoneRatherThanBlank() {
    assertEquals(ConfigValueFormat.EMPTY_LIST, ConfigValueFormat.of(List.of()));
  }

  @Test
  void aNullListReadsAsNoneRatherThanThrowing() {
    assertEquals(ConfigValueFormat.EMPTY_LIST, ConfigValueFormat.of(null));
  }

  @Test
  void listEntriesAreJoinedForReading() {
    assertEquals(
        "minecraft:sand, minecraft:gravel",
        ConfigValueFormat.of(List.of("minecraft:sand", "minecraft:gravel")));
  }
}
