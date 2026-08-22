package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import org.junit.jupiter.api.Test;

class MoreArrowsConfigTest {

  @Test
  void defaultsEveryFamily() {
    final MoreArrowsConfig defaults = MoreArrowsConfig.defaults();

    assertEquals(ExplosiveArrowConfig.defaults(), defaults.explosive());
    assertEquals(GrappleArrowConfig.defaults(), defaults.grapple());
    assertEquals(UtilityArrowConfig.defaults(), defaults.utility());
    assertEquals(PhysicsArrowConfig.defaults(), defaults.physics());
  }

  @Test
  void substitutesDefaultsForNullFamilies() {
    assertEquals(MoreArrowsConfig.defaults(), new MoreArrowsConfig(null, null, null, null));
  }

  @Test
  void replacesOnlyTheExplosiveFamily() {
    final ExplosiveArrowConfig replacement =
        new ExplosiveArrowConfig(
            new ExplosiveTierConfig(5, 1.0f), null, null, true, true, 1, 100, 0.5f);
    final MoreArrowsConfig updated = MoreArrowsConfig.defaults().withExplosive(replacement);

    assertEquals(replacement, updated.explosive());
    assertEquals(GrappleArrowConfig.defaults(), updated.grapple());
    assertEquals(UtilityArrowConfig.defaults(), updated.utility());
    assertEquals(PhysicsArrowConfig.defaults(), updated.physics());
  }

  @Test
  void replacesOnlyTheGrappleFamily() {
    final GrappleArrowConfig replacement = new GrappleArrowConfig(64, 2.0f, false, false, 8, true);
    final MoreArrowsConfig updated = MoreArrowsConfig.defaults().withGrapple(replacement);

    assertEquals(replacement, updated.grapple());
    assertEquals(ExplosiveArrowConfig.defaults(), updated.explosive());
  }

  @Test
  void replacesOnlyTheUtilityFamily() {
    final UtilityArrowConfig replacement = new UtilityArrowConfig(10, 10, 1, 1.0f, 0.5f);
    final MoreArrowsConfig updated = MoreArrowsConfig.defaults().withUtility(replacement);

    assertEquals(replacement, updated.utility());
    assertEquals(PhysicsArrowConfig.defaults(), updated.physics());
  }

  @Test
  void replacesOnlyThePhysicsFamily() {
    final PhysicsArrowConfig replacement =
        new PhysicsArrowConfig(2, List.of("minecraft:bedrock"), 1, false);
    final MoreArrowsConfig updated = MoreArrowsConfig.defaults().withPhysics(replacement);

    assertEquals(replacement, updated.physics());
    assertEquals(UtilityArrowConfig.defaults(), updated.utility());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(MoreArrowsConfig.defaults(), MoreArrowsConfig.fromJson(new JsonObject()));
  }

  @Test
  void ignoresUnknownTopLevelKeys() {
    final MoreArrowsConfig parsed =
        MoreArrowsConfig.fromJson(
            JsonParser.parseString("{\"somethingRemoved\":{\"a\":1}}").getAsJsonObject());

    assertEquals(MoreArrowsConfig.defaults(), parsed);
  }

  @Test
  void defaultsFamiliesThatAreAbsentFromAPartialFile() {
    final MoreArrowsConfig parsed =
        MoreArrowsConfig.fromJson(
            JsonParser.parseString("{\"grapple\":{\"maxRangeBlocks\":64}}").getAsJsonObject());

    assertEquals(64, parsed.grapple().maxRangeBlocks());
    assertEquals(ExplosiveArrowConfig.defaults(), parsed.explosive());
    assertEquals(UtilityArrowConfig.defaults(), parsed.utility());
    assertEquals(PhysicsArrowConfig.defaults(), parsed.physics());
  }

  @Test
  void nestsEachFamilyUnderItsOwnKey() {
    final JsonObject json = MoreArrowsConfig.defaults().toJson();

    assertTrue(json.get("explosive").isJsonObject());
    assertTrue(json.get("grapple").isJsonObject());
    assertTrue(json.get("utility").isJsonObject());
    assertTrue(json.get("physics").isJsonObject());
  }

  @Test
  void roundTripsDefaultsThroughJson() {
    final MoreArrowsConfig defaults = MoreArrowsConfig.defaults();

    assertEquals(defaults, MoreArrowsConfig.fromJson(defaults.toJson()));
  }

  @Test
  void roundTripsFullyCustomisedValuesThroughJson() {
    final MoreArrowsConfig original =
        new MoreArrowsConfig(
            new ExplosiveArrowConfig(
                new ExplosiveTierConfig(0, 1.0f),
                new ExplosiveTierConfig(15, 12.0f),
                new ExplosiveTierConfig(199, 20.0f),
                true,
                false,
                8,
                5999,
                1.75f),
            new GrappleArrowConfig(127, 3.9f, false, false, 127, true),
            new UtilityArrowConfig(5999, 1199, 1, 15.5f, 7.5f),
            new PhysicsArrowConfig(8, List.of("minecraft:bedrock"), 16, false));

    assertEquals(original, MoreArrowsConfig.fromJson(original.toJson()));
  }
}
