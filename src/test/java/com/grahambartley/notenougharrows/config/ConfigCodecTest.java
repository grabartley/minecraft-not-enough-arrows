package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ConfigCodecTest {
  private static final int DEFAULT_SYNC_BUDGET = 8192;

  @Test
  void roundTripsDefaults() {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();

    assertEquals(defaults, ConfigCodec.decode(ConfigCodec.encode(defaults)));
  }

  @Test
  void roundTripsFullyCustomisedValues() {
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
            new FletchingStationConfig(false));

    assertEquals(original, ConfigCodec.decode(ConfigCodec.encode(original)));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\n"})
  void decodesAbsentContentAsDefaults(final String json) {
    assertEquals(NotEnoughArrowsConfig.defaults(), ConfigCodec.decode(json));
  }

  @ParameterizedTest
  @ValueSource(strings = {"{ not json ", "[1,2,3]", "\"a string\"", "12", "null"})
  void refusesToDecodeContentThatIsNotAConfigObject(final String json) {
    assertThrows(JsonParseException.class, () -> ConfigCodec.decode(json));
  }

  @ParameterizedTest
  @ValueSource(strings = {"{ not json ", "[1,2,3]", "\"a string\"", "12", "null"})
  void fallsBackToDefaultsWhenAskedToBeLenient(final String json) {
    assertEquals(NotEnoughArrowsConfig.defaults(), ConfigCodec.decodeOrDefaults(json));
  }

  @Test
  void reportsAParseFailureRatherThanReturningNull() {
    assertThrows(JsonParseException.class, () -> ConfigCodec.decode("\"a string\""));
  }

  @Test
  void encodesDefaultsWellWithinTheWireLimit() {
    assertTrue(
        ConfigCodec.encode(NotEnoughArrowsConfig.defaults()).length()
            < ConfigCodec.MAX_ENCODED_LENGTH);
  }

  @Test
  void encodesAMaximallyPopulatedConfigWithinTheWireLimit() {
    final NotEnoughArrowsConfig maximal =
        NotEnoughArrowsConfig.defaults()
            .withPhysics(
                new PhysicsArrowConfig(
                    8,
                    longestIdentifiers(PhysicsArrowConfig.GRAVITY_BLOCK_EXCLUSIONS_MAX),
                    16,
                    false))
            .withDiscovery(
                DiscoveryArrowConfig.defaults()
                    .withProspector(
                        ProspectorArrowConfig.defaults()
                            .withBlocks(longestIdentifiers(ProspectorArrowConfig.BLOCKS_MAX))))
            .withSocial(
                SocialArrowConfig.defaults()
                    .withCourier(
                        CourierArrowConfig.defaults()
                            .withUndeliverable(
                                longestIdentifiers(CourierArrowConfig.UNDELIVERABLE_MAX))));

    assertEquals(
        PhysicsArrowConfig.GRAVITY_BLOCK_EXCLUSIONS_MAX,
        maximal.physics().gravityBlockExclusions().size());
    assertEquals(
        ProspectorArrowConfig.BLOCKS_MAX, maximal.discovery().prospector().blocks().size());
    assertEquals(
        CourierArrowConfig.UNDELIVERABLE_MAX, maximal.social().courier().undeliverable().size());
    assertTrue(ConfigCodec.encode(maximal).length() < ConfigCodec.MAX_ENCODED_LENGTH);
  }

  @Test
  void encodesDefaultsWithinTheSyncBudget() {
    assertTrue(ConfigCodec.encode(NotEnoughArrowsConfig.defaults()).length() < DEFAULT_SYNC_BUDGET);
  }

  private static List<String> longestIdentifiers(final int count) {
    return IntStream.range(0, count).mapToObj(i -> padded("minecraft:block_" + i + "_")).toList();
  }

  private static String padded(final String prefix) {
    return prefix + "x".repeat(ConfigValues.MAX_IDENTIFIER_LENGTH - prefix.length());
  }
}
