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
    assertEquals(TraversalArrowConfig.defaults(), defaults.traversal());
    assertEquals(TerrainArrowConfig.defaults(), defaults.terrain());
    assertEquals(AgricultureArrowConfig.defaults(), defaults.agriculture());
    assertEquals(DiscoveryArrowConfig.defaults(), defaults.discovery());
    assertEquals(ChaosArrowConfig.defaults(), defaults.chaos());
    assertEquals(SocialArrowConfig.defaults(), defaults.social());
    assertEquals(FletchingStationConfig.defaults(), defaults.fletching());
    assertEquals(SoundConfig.defaults(), defaults.sound());
  }

  @Test
  void substitutesDefaultsForNullFamilies() {
    assertEquals(
        NotEnoughArrowsConfig.defaults(),
        new NotEnoughArrowsConfig(
            null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null));
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
    assertTrue(json.get("control").isJsonObject());
    assertTrue(json.get("traversal").isJsonObject());
    assertTrue(json.get("terrain").isJsonObject());
    assertTrue(json.get("agriculture").isJsonObject());
    assertTrue(json.get("discovery").isJsonObject());
    assertTrue(json.get("chaos").isJsonObject());
    assertTrue(json.get("social").isJsonObject());
    assertTrue(json.get("fletching").isJsonObject());
    assertTrue(json.get("sound").isJsonObject());
  }

  @Test
  void replacesOnlyTheControlFamily() {
    final ControlArrowConfig replacement =
        ControlArrowConfig.defaults().withDisarm(new DisarmArrowConfig(false, 15.5f));
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
                new TargetingArrowConfig(31.5f, 5999, 0.5f, 1, 63.5f),
                new AllegianceArrowConfig(5999, 31.5f),
                new SmokeArrowConfig(15.5f, 1),
                new DisarmArrowConfig(false, 15.5f)),
            TraversalArrowConfig.defaults()
                .withZipline(ZiplineArrowConfig.defaults().withMaxSpanBlocks(100)),
            TerrainArrowConfig.defaults().withDrill(DrillArrowConfig.defaults().withToolTier(0)),
            AgricultureArrowConfig.defaults().withBee(BeeArrowConfig.defaults().withCount(8)),
            DiscoveryArrowConfig.defaults()
                .withProspector(
                    ProspectorArrowConfig.defaults().withBlocks(List.of("minecraft:stone"))),
            ChaosArrowConfig.defaults()
                .withPolymorph(PolymorphArrowConfig.defaults().withEnabled(false)),
            SocialArrowConfig.defaults()
                .withCourier(
                    CourierArrowConfig.defaults()
                        .withUndeliverable(List.of("minecraft:shulker_box"))),
            new FletchingStationConfig(false),
            new SoundConfig(0.35f));

    assertEquals(original, NotEnoughArrowsConfig.fromJson(original.toJson()));
  }

  @Test
  void replacesOnlyTheTraversalFamily() {
    final TraversalArrowConfig replacement =
        TraversalArrowConfig.defaults()
            .withZipline(ZiplineArrowConfig.defaults().withMaxSpanBlocks(100));
    final NotEnoughArrowsConfig updated =
        NotEnoughArrowsConfig.defaults().withTraversal(replacement);

    assertEquals(replacement, updated.traversal());
    assertEquals(
        NotEnoughArrowsConfig.defaults().withTraversal(TraversalArrowConfig.defaults()),
        updated.withTraversal(TraversalArrowConfig.defaults()));
  }

  @Test
  void replacesOnlyTheTerrainFamily() {
    final TerrainArrowConfig replacement =
        TerrainArrowConfig.defaults().withDrill(DrillArrowConfig.defaults().withToolTier(0));
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withTerrain(replacement);

    assertEquals(replacement, updated.terrain());
    assertEquals(
        NotEnoughArrowsConfig.defaults().withTerrain(TerrainArrowConfig.defaults()),
        updated.withTerrain(TerrainArrowConfig.defaults()));
  }

  @Test
  void replacesOnlyTheAgricultureFamily() {
    final AgricultureArrowConfig replacement =
        AgricultureArrowConfig.defaults().withBee(BeeArrowConfig.defaults().withCount(8));
    final NotEnoughArrowsConfig updated =
        NotEnoughArrowsConfig.defaults().withAgriculture(replacement);

    assertEquals(replacement, updated.agriculture());
    assertEquals(
        NotEnoughArrowsConfig.defaults().withAgriculture(AgricultureArrowConfig.defaults()),
        updated.withAgriculture(AgricultureArrowConfig.defaults()));
  }

  @Test
  void replacesOnlyTheDiscoveryFamily() {
    final DiscoveryArrowConfig replacement =
        DiscoveryArrowConfig.defaults()
            .withProspector(
                ProspectorArrowConfig.defaults().withBlocks(List.of("minecraft:stone")));
    final NotEnoughArrowsConfig updated =
        NotEnoughArrowsConfig.defaults().withDiscovery(replacement);

    assertEquals(replacement, updated.discovery());
    assertEquals(
        NotEnoughArrowsConfig.defaults().withDiscovery(DiscoveryArrowConfig.defaults()),
        updated.withDiscovery(DiscoveryArrowConfig.defaults()));
  }

  @Test
  void replacesOnlyTheChaosFamily() {
    final ChaosArrowConfig replacement =
        ChaosArrowConfig.defaults()
            .withPolymorph(PolymorphArrowConfig.defaults().withEnabled(false));
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withChaos(replacement);

    assertEquals(replacement, updated.chaos());
    assertEquals(
        NotEnoughArrowsConfig.defaults().withChaos(ChaosArrowConfig.defaults()),
        updated.withChaos(ChaosArrowConfig.defaults()));
  }

  @Test
  void replacesOnlyTheSocialFamily() {
    final SocialArrowConfig replacement =
        SocialArrowConfig.defaults()
            .withCourier(
                CourierArrowConfig.defaults().withUndeliverable(List.of("minecraft:shulker_box")));
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withSocial(replacement);

    assertEquals(replacement, updated.social());
    assertEquals(
        NotEnoughArrowsConfig.defaults().withSocial(SocialArrowConfig.defaults()),
        updated.withSocial(SocialArrowConfig.defaults()));
  }

  @Test
  void aFileFromBeforeTheNewFamiliesGainsThemAtTheirDefaults() {
    final JsonObject legacy =
        NotEnoughArrowsConfig.defaults().withEnder(new EnderArrowConfig(96, 8, true)).toJson();
    legacy.remove("traversal");
    legacy.remove("terrain");
    legacy.remove("agriculture");
    legacy.remove("discovery");
    legacy.remove("chaos");
    legacy.remove("social");

    final NotEnoughArrowsConfig loaded = NotEnoughArrowsConfig.fromJson(legacy);
    final JsonObject rewritten = loaded.toJson();

    assertEquals(new EnderArrowConfig(96, 8, true), loaded.ender());
    assertEquals(TraversalArrowConfig.defaults(), loaded.traversal());
    assertEquals(TraversalArrowConfig.defaults().toJson(), rewritten.get("traversal"));
    assertEquals(TerrainArrowConfig.defaults(), loaded.terrain());
    assertEquals(TerrainArrowConfig.defaults().toJson(), rewritten.get("terrain"));
    assertEquals(AgricultureArrowConfig.defaults(), loaded.agriculture());
    assertEquals(AgricultureArrowConfig.defaults().toJson(), rewritten.get("agriculture"));
    assertEquals(DiscoveryArrowConfig.defaults(), loaded.discovery());
    assertEquals(DiscoveryArrowConfig.defaults().toJson(), rewritten.get("discovery"));
    assertEquals(ChaosArrowConfig.defaults(), loaded.chaos());
    assertEquals(ChaosArrowConfig.defaults().toJson(), rewritten.get("chaos"));
    assertEquals(SocialArrowConfig.defaults(), loaded.social());
    assertEquals(SocialArrowConfig.defaults().toJson(), rewritten.get("social"));
    for (final String key : legacy.keySet()) {
      assertEquals(legacy.get(key), rewritten.get(key), key);
    }
  }

  @Test
  void replacesOnlyTheSoundFamily() {
    final SoundConfig replacement = new SoundConfig(0.25f);
    final NotEnoughArrowsConfig updated = NotEnoughArrowsConfig.defaults().withSound(replacement);

    assertEquals(replacement, updated.sound());
    assertEquals(NotEnoughArrowsConfig.defaults(), updated.withSound(SoundConfig.defaults()));
  }

  @Test
  void everyOtherFamilyChangeKeepsTheSoundFamily() {
    final SoundConfig quiet = new SoundConfig(0.25f);
    final NotEnoughArrowsConfig base = NotEnoughArrowsConfig.defaults().withSound(quiet);

    assertEquals(quiet, base.withExplosive(ExplosiveArrowConfig.defaults()).sound());
    assertEquals(quiet, base.withSocial(SocialArrowConfig.defaults()).sound());
    assertEquals(quiet, base.withFletching(new FletchingStationConfig(false)).sound());
  }

  @Test
  void aFileFromBeforeTheSoundFamilyPlaysAtFullVolume() {
    final JsonObject legacy = NotEnoughArrowsConfig.defaults().toJson();
    legacy.remove("sound");

    final NotEnoughArrowsConfig loaded = NotEnoughArrowsConfig.fromJson(legacy);

    assertEquals(SoundConfig.defaults(), loaded.sound());
    assertEquals(SoundConfig.defaults().toJson(), loaded.toJson().get("sound"));
  }
}
