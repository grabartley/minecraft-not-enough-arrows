package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotEnoughArrowsConfigTest {

  @Test
  void defaultsEveryFamily() {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();

    assertEquals(ExplosiveArrowConfig.defaults(), defaults.explosive());
    assertEquals(GrappleArrowConfig.defaults(), defaults.grapple());
    assertEquals(UtilityArrowConfig.defaults(), defaults.utility());
    assertEquals(PhysicsArrowConfig.defaults(), defaults.physics());
    assertEquals(EnderArrowConfig.defaults(), defaults.ender());
    assertEquals(CombatArrowConfig.defaults(), defaults.combat());
    assertEquals(ControlArrowConfig.defaults(), defaults.control());
    assertEquals(FletchingStationConfig.defaults(), defaults.fletching());
  }

  @Test
  void substitutesDefaultsForNullFamilies() {
    assertEquals(
        NotEnoughArrowsConfig.defaults(),
        new NotEnoughArrowsConfig(null, null, null, null, null, null, null, null));
  }

  @Test
  void replacesOnlyTheExplosiveFamily() {
    final ExplosiveArrowConfig replacement =
        new ExplosiveArrowConfig(
            new ExplosiveTierConfig(5, 1.0f), null, null, true, true, 1, 100, 0.5f, null);
    final NotEnoughArrowsConfig updated =
        NotEnoughArrowsConfig.defaults().withExplosive(replacement);

    assertEquals(replacement, updated.explosive());
    assertEquals(GrappleArrowConfig.defaults(), updated.grapple());
    assertEquals(UtilityArrowConfig.defaults(), updated.utility());
    assertEquals(PhysicsArrowConfig.defaults(), updated.physics());
  }

  @Test
  void replacesOnlyTheGrappleFamily() {
    final GrappleArrowConfig replacement =
        new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true);
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withGrapple(replacement);

    assertEquals(replacement, updated.grapple());
    assertEquals(ExplosiveArrowConfig.defaults(), updated.explosive());
  }

  @Test
  void replacesOnlyTheUtilityFamily() {
    final UtilityArrowConfig replacement = new UtilityArrowConfig(10, 10, 1, 1.0f, 0.5f);
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withUtility(replacement);

    assertEquals(replacement, updated.utility());
    assertEquals(PhysicsArrowConfig.defaults(), updated.physics());
  }

  @Test
  void replacesOnlyThePhysicsFamily() {
    final PhysicsArrowConfig replacement =
        new PhysicsArrowConfig(2, List.of("minecraft:bedrock"), 1, false);
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withPhysics(replacement);

    assertEquals(replacement, updated.physics());
    assertEquals(UtilityArrowConfig.defaults(), updated.utility());
  }

  @Test
  void replacesOnlyTheEnderFamily() {
    final EnderArrowConfig replacement = new EnderArrowConfig(96, 8, true);
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withEnder(replacement);

    assertEquals(replacement, updated.ender());
    assertEquals(PhysicsArrowConfig.defaults(), updated.physics());
    assertEquals(FletchingStationConfig.defaults(), updated.fletching());
  }

  @Test
  void replacesOnlyTheFletchingFamily() {
    final FletchingStationConfig replacement = new FletchingStationConfig(false);
    final NotEnoughArrowsConfig updated =
        NotEnoughArrowsConfig.defaults().withFletching(replacement);

    assertEquals(replacement, updated.fletching());
    assertEquals(PhysicsArrowConfig.defaults(), updated.physics());
    assertEquals(EnderArrowConfig.defaults(), updated.ender());
    assertEquals(UtilityArrowConfig.defaults(), updated.utility());
  }

  @Test
  void replacesOnlyTheCombatFamily() {
    final CombatArrowConfig replacement =
        CombatArrowConfig.defaults().withShock(new ShockArrowConfig(0.0f, 1.0f));
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withCombat(replacement);

    assertEquals(replacement, updated.combat());
    assertEquals(EnderArrowConfig.defaults(), updated.ender());
    assertEquals(FletchingStationConfig.defaults(), updated.fletching());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        NotEnoughArrowsConfig.defaults(), NotEnoughArrowsConfig.fromJson(new JsonObject()));
  }

  @Test
  void ignoresUnknownTopLevelKeys() {
    final NotEnoughArrowsConfig parsed =
        NotEnoughArrowsConfig.fromJson(
            JsonParser.parseString("{\"somethingRemoved\":{\"a\":1}}").getAsJsonObject());

    assertEquals(NotEnoughArrowsConfig.defaults(), parsed);
  }

  @Test
  void defaultsFamiliesThatAreAbsentFromAPartialFile() {
    final NotEnoughArrowsConfig parsed =
        NotEnoughArrowsConfig.fromJson(
            JsonParser.parseString("{\"grapple\":{\"maxRangeBlocks\":64}}").getAsJsonObject());

    assertEquals(64, parsed.grapple().maxRangeBlocks());
    assertEquals(ExplosiveArrowConfig.defaults(), parsed.explosive());
    assertEquals(UtilityArrowConfig.defaults(), parsed.utility());
    assertEquals(PhysicsArrowConfig.defaults(), parsed.physics());
    assertEquals(EnderArrowConfig.defaults(), parsed.ender());
    assertEquals(CombatArrowConfig.defaults(), parsed.combat());
    assertEquals(FletchingStationConfig.defaults(), parsed.fletching());
  }

  @Test
  void nestsEachFamilyUnderItsOwnKey() {
    final JsonObject json = NotEnoughArrowsConfig.defaults().toJson();

    assertTrue(json.get("explosive").isJsonObject());
    assertTrue(json.get("grapple").isJsonObject());
    assertTrue(json.get("utility").isJsonObject());
    assertTrue(json.get("physics").isJsonObject());
    assertTrue(json.get("ender").isJsonObject());
    assertTrue(json.get("combat").isJsonObject());
    assertTrue(json.get("fletching").isJsonObject());
  }

  @Test
  void replacesOnlyTheControlFamily() {
    final ControlArrowConfig replacement =
        ControlArrowConfig.defaults().withDisarm(new DisarmArrowConfig(false));
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withControl(replacement);

    assertEquals(replacement, updated.control());
    assertEquals(CombatArrowConfig.defaults(), updated.combat());
    assertEquals(FletchingStationConfig.defaults(), updated.fletching());
  }

  @Test
  void roundTripsDefaultsThroughJson() {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();

    assertEquals(defaults, NotEnoughArrowsConfig.fromJson(defaults.toJson()));
  }

  @Test
  void roundTripsFullyCustomisedValuesThroughJson() {
    final NotEnoughArrowsConfig original =
        new NotEnoughArrowsConfig(
            new ExplosiveArrowConfig(
                new ExplosiveTierConfig(0, 1.0f),
                new ExplosiveTierConfig(15, 12.0f),
                new ExplosiveTierConfig(199, 20.0f),
                true,
                false,
                8,
                5999,
                1.75f,
                new IncendiaryArrowConfig(7, 42, false)),
            new GrappleArrowConfig(127, 3.9f, 0.2f, false, false, 127, true),
            new UtilityArrowConfig(5999, 1199, 1, 15.5f, 7.5f),
            new PhysicsArrowConfig(8, List.of("minecraft:bedrock"), 16, false),
            new EnderArrowConfig(96, 8, true),
            new CombatArrowConfig(
                new ShockArrowConfig(31.5f, 19.5f),
                new LifestealArrowConfig(0.95f, 19.5f),
                new StatusArrowConfig(11999, 1, 6000),
                new HomingArrowConfig(0.95f, 63.5f, 179.0f),
                new VolleyArrowConfig(12, 0.95f, 44.0f, 39),
                new RailgunArrowConfig(7.5f, 1.95f)),
            new ControlArrowConfig(
                new FrostArrowConfig(1199),
                new LevitationArrowConfig(1),
                new TargetingArrowConfig(31.5f, 5999, 0.5f, 1, 5999),
                new SmokeArrowConfig(15.5f, 1),
                new DisarmArrowConfig(false)),
            new FletchingStationConfig(false));

    assertEquals(original, NotEnoughArrowsConfig.fromJson(original.toJson()));
  }
}
