package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class CombatArrowConfigTest {

  @Test
  void defaultsEveryArrowInTheFamily() {
    final CombatArrowConfig defaults = CombatArrowConfig.defaults();

    assertEquals(ShockArrowConfig.defaults(), defaults.shock());
    assertEquals(LifestealArrowConfig.defaults(), defaults.lifesteal());
    assertEquals(StatusArrowConfig.defaults(), defaults.status());
    assertEquals(HomingArrowConfig.defaults(), defaults.homing());
    assertEquals(VolleyArrowConfig.defaults(), defaults.volley());
    assertEquals(RailgunArrowConfig.defaults(), defaults.railgun());
  }

  @Test
  void substitutesDefaultsForNullMembers() {
    assertEquals(
        CombatArrowConfig.defaults(), new CombatArrowConfig(null, null, null, null, null, null));
  }

  @Test
  void replacesOnlyTheArrowItIsGiven() {
    final ShockArrowConfig replacement = new ShockArrowConfig(1.0f, 2.0f);
    final CombatArrowConfig updated = CombatArrowConfig.defaults().withShock(replacement);

    assertEquals(replacement, updated.shock());
    assertEquals(LifestealArrowConfig.defaults(), updated.lifesteal());
    assertEquals(RailgunArrowConfig.defaults(), updated.railgun());
  }

  @Test
  void everyArrowCanBeReplacedOnItsOwn() {
    final CombatArrowConfig updated =
        CombatArrowConfig.defaults()
            .withShock(new ShockArrowConfig(1.0f, 2.0f))
            .withLifesteal(new LifestealArrowConfig(0.1f, 1.0f))
            .withStatus(new StatusArrowConfig(1, 2, 3))
            .withHoming(new HomingArrowConfig(0.1f, 2.0f, 3.0f))
            .withVolley(new VolleyArrowConfig(3, 0.1f, 2.0f, 5))
            .withRailgun(new RailgunArrowConfig(6.0f, 1.5f));

    assertEquals(
        new CombatArrowConfig(
            new ShockArrowConfig(1.0f, 2.0f),
            new LifestealArrowConfig(0.1f, 1.0f),
            new StatusArrowConfig(1, 2, 3),
            new HomingArrowConfig(0.1f, 2.0f, 3.0f),
            new VolleyArrowConfig(3, 0.1f, 2.0f, 5),
            new RailgunArrowConfig(6.0f, 1.5f)),
        updated);
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(CombatArrowConfig.defaults(), CombatArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void defaultsArrowsThatAreAbsentFromAPartialFile() {
    final CombatArrowConfig parsed =
        CombatArrowConfig.fromJson(
            JsonParser.parseString("{\"volley\":{\"fragmentCount\":7}}").getAsJsonObject());

    assertEquals(7, parsed.volley().fragmentCount());
    assertEquals(ShockArrowConfig.defaults(), parsed.shock());
    assertEquals(RailgunArrowConfig.defaults(), parsed.railgun());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = CombatArrowConfig.defaults().toJson();

    assertTrue(json.get("shock").isJsonObject());
    assertTrue(json.get("lifesteal").isJsonObject());
    assertTrue(json.get("status").isJsonObject());
    assertTrue(json.get("homing").isJsonObject());
    assertTrue(json.get("volley").isJsonObject());
    assertTrue(json.get("railgun").isJsonObject());
  }

  @Test
  void roundTripsFullyCustomisedValuesThroughJson() {
    final CombatArrowConfig original =
        new CombatArrowConfig(
            new ShockArrowConfig(31.5f, 19.5f),
            new LifestealArrowConfig(0.95f, 19.5f),
            new StatusArrowConfig(11999, 1, 6000),
            new HomingArrowConfig(0.95f, 63.5f, 179.0f),
            new VolleyArrowConfig(12, 0.95f, 44.0f, 39),
            new RailgunArrowConfig(7.5f, 1.95f));

    assertEquals(original, CombatArrowConfig.fromJson(original.toJson()));
  }
}
