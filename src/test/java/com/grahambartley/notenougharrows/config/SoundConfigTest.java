package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SoundConfigTest {

  @Test
  void startsAtFullVolume() {
    assertEquals(1.0f, SoundConfig.defaults().volume());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final SoundConfig defaults = SoundConfig.defaults();

    assertEquals(defaults, new SoundConfig(defaults.volume()));
  }

  @ParameterizedTest(name = "{0} is stored as {1}")
  @CsvSource({"-0.5, 0.0", "0.0, 0.0", "0.4, 0.4", "1.0, 1.0", "2.5, 1.0"})
  void clampsTheVolumeIntoItsRange(final float given, final float stored) {
    assertEquals(stored, new SoundConfig(given).volume());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(SoundConfig.defaults(), SoundConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(SoundConfig.defaults(), SoundConfig.fromJson(null));
  }

  @Test
  void readsTheVolumeFromItsKey() {
    final SoundConfig parsed =
        SoundConfig.fromJson(JsonParser.parseString("{\"volume\":0.3}").getAsJsonObject());

    assertEquals(0.3f, parsed.volume());
  }

  @Test
  void clampsAnOutOfRangeVolumeReadFromAFile() {
    final SoundConfig parsed =
        SoundConfig.fromJson(JsonParser.parseString("{\"volume\":9}").getAsJsonObject());

    assertEquals(SoundConfig.VOLUME_MAX, parsed.volume());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final SoundConfig parsed =
        SoundConfig.fromJson(JsonParser.parseString("{\"volume\":\"loud\"}").getAsJsonObject());

    assertEquals(SoundConfig.DEFAULT_VOLUME, parsed.volume());
  }

  @Test
  void roundTripsThroughJson() {
    final SoundConfig original = new SoundConfig(0.45f);

    assertEquals(original, SoundConfig.fromJson(original.toJson()));
  }

  @Test
  void changesTheVolume() {
    assertEquals(new SoundConfig(0.2f), SoundConfig.defaults().withVolume(0.2f));
  }
}
