package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class SocialArrowConfigTest {

  @Test
  void defaultsEveryArrow() {
    final SocialArrowConfig defaults = SocialArrowConfig.defaults();

    assertEquals(CourierArrowConfig.defaults(), defaults.courier());
    assertEquals(SnowGolemArrowConfig.defaults(), defaults.snowGolem());
    assertEquals(MagnetArrowConfig.defaults(), defaults.magnet());
  }

  @Test
  void substitutesDefaultsForMissingArrows() {
    assertEquals(SocialArrowConfig.defaults(), new SocialArrowConfig(null, null, null));
  }

  @Test
  void replacesOnlyTheCourierArrow() {
    final CourierArrowConfig replacement = CourierArrowConfig.defaults().withMaxPayload(63);
    final SocialArrowConfig updated = SocialArrowConfig.defaults().withCourier(replacement);

    assertEquals(replacement, updated.courier());
    assertEquals(SnowGolemArrowConfig.defaults(), updated.snowGolem());
    assertEquals(MagnetArrowConfig.defaults(), updated.magnet());
  }

  @Test
  void replacesOnlyTheSnowGolemArrow() {
    final SnowGolemArrowConfig replacement =
        SnowGolemArrowConfig.defaults().withLifetimeTicks(11999);
    final SocialArrowConfig updated = SocialArrowConfig.defaults().withSnowGolem(replacement);

    assertEquals(replacement, updated.snowGolem());
    assertEquals(CourierArrowConfig.defaults(), updated.courier());
    assertEquals(MagnetArrowConfig.defaults(), updated.magnet());
  }

  @Test
  void replacesOnlyTheMagnetArrow() {
    final MagnetArrowConfig replacement = MagnetArrowConfig.defaults().withRadius(15);
    final SocialArrowConfig updated = SocialArrowConfig.defaults().withMagnet(replacement);

    assertEquals(replacement, updated.magnet());
    assertEquals(CourierArrowConfig.defaults(), updated.courier());
    assertEquals(SnowGolemArrowConfig.defaults(), updated.snowGolem());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(SocialArrowConfig.defaults(), SocialArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOneArrowAndDefaultsTheRest() {
    final SocialArrowConfig parsed =
        SocialArrowConfig.fromJson(
            JsonParser.parseString("{\"courier\":{\"maxPayload\":63}}").getAsJsonObject());

    assertEquals(63, parsed.courier().maxPayload());
    assertEquals(SnowGolemArrowConfig.defaults(), parsed.snowGolem());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = SocialArrowConfig.defaults().toJson();

    assertTrue(json.get("courier").isJsonObject());
    assertTrue(json.get("snowGolem").isJsonObject());
    assertTrue(json.get("magnet").isJsonObject());
  }

  @Test
  void roundTripsThroughJson() {
    final SocialArrowConfig original =
        new SocialArrowConfig(
            CourierArrowConfig.defaults().withMaxPayload(63),
            SnowGolemArrowConfig.defaults().withLifetimeTicks(11999),
            MagnetArrowConfig.defaults().withRadius(15));

    assertEquals(original, SocialArrowConfig.fromJson(original.toJson()));
  }
}
