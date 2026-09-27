package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SocialArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final SocialArrowConfig defaults = SocialArrowConfig.defaults();

    assertEquals(SocialArrowConfig.DEFAULT_COURIER_MAX_PAYLOAD, defaults.courierMaxPayload());
    assertEquals(SocialArrowConfig.DEFAULT_COURIER_UNDELIVERABLE, defaults.courierUndeliverable());
    assertEquals(
        SocialArrowConfig.DEFAULT_SNOW_GOLEM_LIFETIME_TICKS, defaults.snowGolemLifetimeTicks());
    assertEquals(SocialArrowConfig.DEFAULT_MAGNET_RADIUS, defaults.magnetRadius());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final SocialArrowConfig defaults = SocialArrowConfig.defaults();

    assertEquals(
        defaults,
        new SocialArrowConfig(
            defaults.courierMaxPayload(),
            defaults.courierUndeliverable(),
            defaults.snowGolemLifetimeTicks(),
            defaults.magnetRadius()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "64, 64", "65, 64"})
  void clampsCourierMaxPayload(final int given, final int expected) {
    assertEquals(
        expected, SocialArrowConfig.defaults().withCourierMaxPayload(given).courierMaxPayload());
  }

  @Test
  void normalisesCourierUndeliverableEntries() {
    final SocialArrowConfig config =
        SocialArrowConfig.defaults()
            .withCourierUndeliverable(
                List.of("  Minecraft:Stone ", "minecraft:stone", "", "minecraft:dirt"));

    assertEquals(List.of("minecraft:stone", "minecraft:dirt"), config.courierUndeliverable());
  }

  @Test
  void capsCourierUndeliverableSoTheSyncPayloadStaysBounded() {
    final List<String> tooMany =
        IntStream.rangeClosed(0, SocialArrowConfig.COURIER_UNDELIVERABLE_MAX)
            .mapToObj(i -> "minecraft:block_" + i)
            .toList();

    assertEquals(
        SocialArrowConfig.COURIER_UNDELIVERABLE_MAX,
        SocialArrowConfig.defaults()
            .withCourierUndeliverable(tooMany)
            .courierUndeliverable()
            .size());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "12000, 12000", "12001, 12000"})
  void clampsSnowGolemLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected,
        SocialArrowConfig.defaults().withSnowGolemLifetimeTicks(given).snowGolemLifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "16, 16", "17, 16"})
  void clampsMagnetRadius(final int given, final int expected) {
    assertEquals(expected, SocialArrowConfig.defaults().withMagnetRadius(given).magnetRadius());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(SocialArrowConfig.defaults(), SocialArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(SocialArrowConfig.defaults(), SocialArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final SocialArrowConfig parsed =
        SocialArrowConfig.fromJson(
            JsonParser.parseString("{\"courierMaxPayload\":63}").getAsJsonObject());

    assertEquals(63, parsed.courierMaxPayload());
    assertEquals(SocialArrowConfig.DEFAULT_COURIER_UNDELIVERABLE, parsed.courierUndeliverable());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final SocialArrowConfig parsed =
        SocialArrowConfig.fromJson(
            JsonParser.parseString("{\"courierMaxPayload\":164}").getAsJsonObject());

    assertEquals(SocialArrowConfig.COURIER_MAX_PAYLOAD_MAX, parsed.courierMaxPayload());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final SocialArrowConfig parsed =
        SocialArrowConfig.fromJson(
            JsonParser.parseString("{\"courierMaxPayload\":\"lots\"}").getAsJsonObject());

    assertEquals(SocialArrowConfig.DEFAULT_COURIER_MAX_PAYLOAD, parsed.courierMaxPayload());
  }

  @Test
  void roundTripsThroughJson() {
    final SocialArrowConfig original =
        new SocialArrowConfig(63, List.of("minecraft:stone"), 11999, 15);

    assertEquals(original, SocialArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final SocialArrowConfig original = SocialArrowConfig.defaults();

    final SocialArrowConfig updated = original.withCourierMaxPayload(63);

    assertEquals(63, updated.courierMaxPayload());
    assertEquals(original.courierUndeliverable(), updated.courierUndeliverable());
    assertEquals(original.snowGolemLifetimeTicks(), updated.snowGolemLifetimeTicks());
    assertEquals(original.magnetRadius(), updated.magnetRadius());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final SocialArrowConfig updated =
        SocialArrowConfig.defaults()
            .withCourierMaxPayload(63)
            .withCourierUndeliverable(List.of("minecraft:stone"))
            .withSnowGolemLifetimeTicks(11999)
            .withMagnetRadius(15);

    assertEquals(new SocialArrowConfig(63, List.of("minecraft:stone"), 11999, 15), updated);
  }
}
