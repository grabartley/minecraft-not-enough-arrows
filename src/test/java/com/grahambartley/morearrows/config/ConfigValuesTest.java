package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConfigValuesTest {

  @ParameterizedTest
  @CsvSource({"5, 0, 10, 5", "-1, 0, 10, 0", "11, 0, 10, 10", "0, 0, 10, 0", "10, 0, 10, 10"})
  void clampsIntegersIntoRange(final int value, final int min, final int max, final int expected) {
    assertEquals(expected, ConfigValues.clampInt(value, min, max));
  }

  @ParameterizedTest
  @CsvSource({"0.5, 0.0, 1.0, 0.5", "-0.5, 0.0, 1.0, 0.0", "1.5, 0.0, 1.0, 1.0"})
  void clampsFloatsIntoRange(
      final float value, final float min, final float max, final float expected) {
    assertEquals(expected, ConfigValues.clampFloat(value, min, max));
  }

  @Test
  void readsAnIntegerWhenPresent() {
    assertEquals(7, ConfigValues.readInt(object("{\"a\":7}"), "a", 3, 0, 10));
  }

  @Test
  void fallsBackWhenAnIntegerKeyIsAbsent() {
    assertEquals(3, ConfigValues.readInt(object("{}"), "a", 3, 0, 10));
  }

  @Test
  void clampsAnIntegerThatIsOutOfRange() {
    assertEquals(10, ConfigValues.readInt(object("{\"a\":99}"), "a", 3, 0, 10));
  }

  @ParameterizedTest
  @CsvSource({"'{\"a\":\"abc\"}'", "'{\"a\":{}}'", "'{\"a\":[]}'", "'{\"a\":true}'"})
  void fallsBackWhenAnIntegerValueIsUnreadable(final String json) {
    assertEquals(3, ConfigValues.readInt(object(json), "a", 3, 0, 10));
  }

  @Test
  void readsAFloatAndClampsIt() {
    assertEquals(1.0f, ConfigValues.readFloat(object("{\"a\":9.5}"), "a", 0.5f, 0.0f, 1.0f));
  }

  @Test
  void readsABooleanWhenPresent() {
    assertTrue(ConfigValues.readBoolean(object("{\"a\":true}"), "a", false));
  }

  @ParameterizedTest
  @CsvSource({"'{}'", "'{\"a\":\"true\"}'", "'{\"a\":1}'", "'{\"a\":null}'"})
  void fallsBackWhenABooleanIsAbsentOrNotABoolean(final String json) {
    assertTrue(ConfigValues.readBoolean(object(json), "a", true));
  }

  @Test
  void readsANestedObject() {
    assertEquals(1, ConfigValues.readObject(object("{\"a\":{\"b\":1}}"), "a").get("b").getAsInt());
  }

  @ParameterizedTest
  @CsvSource({"'{}'", "'{\"a\":5}'", "'{\"a\":[]}'"})
  void yieldsAnEmptyObjectWhenANestedObjectIsAbsentOrWrongType(final String json) {
    assertTrue(ConfigValues.readObject(object(json), "a").isEmpty());
  }

  @Test
  void normalizesIdentifiersByTrimmingLoweringAndDeduplicating() {
    assertEquals(
        List.of("minecraft:stone", "minecraft:dirt"),
        ConfigValues.normalizeIdentifiers(
            Arrays.asList("  Minecraft:Stone ", "minecraft:dirt", "MINECRAFT:STONE", "", "  ")));
  }

  @Test
  void treatsANullIdentifierListAsEmpty() {
    assertEquals(List.of(), ConfigValues.normalizeIdentifiers(null));
  }

  @Test
  void dropsNullEntriesFromAnIdentifierList() {
    assertEquals(
        List.of("minecraft:stone"),
        ConfigValues.normalizeIdentifiers(Arrays.asList("minecraft:stone", null)));
  }

  @Test
  void exposesNormalizedIdentifiersAsAnImmutableList() {
    final List<String> normalized = ConfigValues.normalizeIdentifiers(List.of("minecraft:stone"));

    assertThrows(UnsupportedOperationException.class, () -> normalized.add("minecraft:dirt"));
  }

  @Test
  void readsAnIdentifierListAndSkipsNonStringEntries() {
    assertEquals(
        List.of("minecraft:stone"),
        ConfigValues.readIdentifierList(
            object("{\"a\":[\"Minecraft:Stone\", 5, {}]}"), "a", List.of()));
  }

  @ParameterizedTest
  @CsvSource({"'{}'", "'{\"a\":\"minecraft:stone\"}'", "'{\"a\":5}'"})
  void fallsBackWhenAnIdentifierListIsAbsentOrNotAnArray(final String json) {
    assertEquals(
        List.of("minecraft:dirt"),
        ConfigValues.readIdentifierList(object(json), "a", List.of("minecraft:dirt")));
  }

  @Test
  void roundTripsAnIdentifierListThroughJson() {
    final List<String> values = List.of("minecraft:stone", "minecraft:dirt");
    final JsonObject root = new JsonObject();
    root.add("a", ConfigValues.toJsonArray(values));

    assertEquals(values, ConfigValues.readIdentifierList(root, "a", List.of()));
  }

  private static JsonObject object(final String json) {
    return JsonParser.parseString(json).getAsJsonObject();
  }
}
