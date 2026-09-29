package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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

  @ParameterizedTest
  @ValueSource(strings = {"minecraft:diamond", "Minecraft:Diamond", "  MINECRAFT:DIAMOND  "})
  void refusesAnUndeliverableItemRegardlessOfCaseOrPadding(final String queried) {
    assertTrue(undeliverable("minecraft:diamond").isUndeliverable(queried));
  }

  @Test
  void refusesAVanillaItemListedWithoutItsNamespace() {
    assertTrue(undeliverable("diamond").isUndeliverable("minecraft:diamond"));
  }

  @Test
  void doesNotRefuseAModdedItemSharingAVanillaItemsPath() {
    assertFalse(undeliverable("diamond").isUndeliverable("not-enough-arrows:diamond"));
  }

  @Test
  void doesNotRefuseAnItemThatIsNotListed() {
    assertFalse(undeliverable("minecraft:diamond").isUndeliverable("minecraft:emerald"));
  }

  @Test
  void treatsANullItemAsDeliverable() {
    assertFalse(undeliverable("minecraft:diamond").isUndeliverable(null));
  }

  @ParameterizedTest
  @CsvSource({"16, 15, false", "16, 16, false", "16, 17, true"})
  void exceedsThePayloadOnlyAboveTheCap(final int cap, final int count, final boolean exceeds) {
    assertEquals(exceeds, CourierArrowConfig.defaults().withMaxPayload(cap).exceedsPayload(count));
  }

  private static CourierArrowConfig undeliverable(final String entry) {
    return CourierArrowConfig.defaults().withUndeliverable(List.of(entry));
  }
}
