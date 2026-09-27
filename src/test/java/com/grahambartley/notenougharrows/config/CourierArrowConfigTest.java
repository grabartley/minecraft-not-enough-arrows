package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CourierArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final CourierArrowConfig defaults = CourierArrowConfig.defaults();

    assertEquals(CourierArrowConfig.DEFAULT_MAX_PAYLOAD, defaults.maxPayload());
    assertEquals(CourierArrowConfig.DEFAULT_UNDELIVERABLE, defaults.undeliverable());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final CourierArrowConfig defaults = CourierArrowConfig.defaults();

    assertEquals(defaults, new CourierArrowConfig(defaults.maxPayload(), defaults.undeliverable()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "64, 64", "65, 64"})
  void clampsMaxPayload(final int given, final int expected) {
    assertEquals(expected, CourierArrowConfig.defaults().withMaxPayload(given).maxPayload());
  }

  @Test
  void normalisesUndeliverableEntries() {
    final CourierArrowConfig config =
        CourierArrowConfig.defaults()
            .withUndeliverable(
                List.of("  Minecraft:Stone ", "minecraft:stone", "", "minecraft:dirt"));

    assertEquals(List.of("minecraft:stone", "minecraft:dirt"), config.undeliverable());
  }

  @Test
  void capsUndeliverableSoTheSyncPayloadStaysBounded() {
    final List<String> tooMany =
        IntStream.rangeClosed(0, CourierArrowConfig.UNDELIVERABLE_MAX)
            .mapToObj(i -> "minecraft:block_" + i)
            .toList();

    assertEquals(
        CourierArrowConfig.UNDELIVERABLE_MAX,
        CourierArrowConfig.defaults().withUndeliverable(tooMany).undeliverable().size());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(CourierArrowConfig.defaults(), CourierArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(CourierArrowConfig.defaults(), CourierArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final CourierArrowConfig parsed =
        CourierArrowConfig.fromJson(
            JsonParser.parseString("{\"maxPayload\":63}").getAsJsonObject());

    assertEquals(63, parsed.maxPayload());
    assertEquals(CourierArrowConfig.DEFAULT_UNDELIVERABLE, parsed.undeliverable());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final CourierArrowConfig parsed =
        CourierArrowConfig.fromJson(
            JsonParser.parseString("{\"maxPayload\":164}").getAsJsonObject());

    assertEquals(CourierArrowConfig.MAX_PAYLOAD_MAX, parsed.maxPayload());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final CourierArrowConfig parsed =
        CourierArrowConfig.fromJson(
            JsonParser.parseString("{\"maxPayload\":\"lots\"}").getAsJsonObject());

    assertEquals(CourierArrowConfig.DEFAULT_MAX_PAYLOAD, parsed.maxPayload());
  }

  @Test
  void roundTripsThroughJson() {
    final CourierArrowConfig original = new CourierArrowConfig(63, List.of("minecraft:stone"));

    assertEquals(original, CourierArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final CourierArrowConfig original = CourierArrowConfig.defaults();

    final CourierArrowConfig updated = original.withMaxPayload(63);

    assertEquals(63, updated.maxPayload());
    assertEquals(original.undeliverable(), updated.undeliverable());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final CourierArrowConfig updated =
        CourierArrowConfig.defaults()
            .withMaxPayload(63)
            .withUndeliverable(List.of("minecraft:stone"));

    assertEquals(new CourierArrowConfig(63, List.of("minecraft:stone")), updated);
  }
}
