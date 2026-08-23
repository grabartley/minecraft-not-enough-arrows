package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PhysicsArrowConfigTest {

  @Test
  void defaultsToCollapsingOnlyTheBlockThatWasHit() {
    final PhysicsArrowConfig defaults = PhysicsArrowConfig.defaults();

    assertEquals(0, defaults.gravityImpactRadius());
    assertTrue(defaults.affectsOnlyTheHitBlock());
  }

  @Test
  void defaultsToAnEmptyExclusionList() {
    assertEquals(List.of(), PhysicsArrowConfig.defaults().gravityBlockExclusions());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "99, 8"})
  void clampsGravityImpactRadius(final int given, final int expected) {
    assertEquals(expected, config(given, List.of()).gravityImpactRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "3, 3", "16, 16", "99, 16"})
  void clampsRicochetBounceCount(final int given, final int expected) {
    final PhysicsArrowConfig parsed = new PhysicsArrowConfig(0, List.of(), given, true);

    assertEquals(expected, parsed.ricochetBounceCount());
  }

  @Test
  void reportsARadiusAboveZeroAsAffectingNeighbours() {
    assertFalse(config(1, List.of()).affectsOnlyTheHitBlock());
  }

  @Test
  void normalizesExclusionsOnConstruction() {
    assertEquals(
        List.of("minecraft:stone"),
        config(0, List.of("  Minecraft:Stone  ", "MINECRAFT:STONE")).gravityBlockExclusions());
  }

  @Test
  void treatsANullExclusionListAsEmpty() {
    assertEquals(List.of(), config(0, null).gravityBlockExclusions());
  }

  @Test
  void exposesExclusionsAsAnImmutableList() {
    final List<String> exclusions = config(0, List.of("minecraft:stone")).gravityBlockExclusions();

    assertThrows(UnsupportedOperationException.class, () -> exclusions.add("minecraft:dirt"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"minecraft:stone", "Minecraft:Stone", "  MINECRAFT:STONE  "})
  void matchesAnExcludedBlockRegardlessOfCaseOrPadding(final String queried) {
    assertTrue(config(0, List.of("minecraft:stone")).isExcludedFromGravity(queried));
  }

  @Test
  void doesNotMatchABlockThatIsNotExcluded() {
    assertFalse(config(0, List.of("minecraft:stone")).isExcludedFromGravity("minecraft:dirt"));
  }

  @Test
  void treatsANullBlockQueryAsNotExcluded() {
    assertFalse(config(0, List.of("minecraft:stone")).isExcludedFromGravity(null));
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(PhysicsArrowConfig.defaults(), PhysicsArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsAnExclusionListFromJson() {
    final PhysicsArrowConfig parsed =
        PhysicsArrowConfig.fromJson(
            JsonParser.parseString("{\"gravityBlockExclusions\":[\"minecraft:bedrock\"]}")
                .getAsJsonObject());

    assertTrue(parsed.isExcludedFromGravity("minecraft:bedrock"));
  }

  @Test
  void roundTripsThroughJson() {
    final PhysicsArrowConfig original =
        new PhysicsArrowConfig(4, List.of("minecraft:bedrock", "minecraft:obsidian"), 7, false);

    assertEquals(original, PhysicsArrowConfig.fromJson(original.toJson()));
  }

  private static PhysicsArrowConfig config(final int radius, final List<String> exclusions) {
    return new PhysicsArrowConfig(radius, exclusions, 3, true);
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final PhysicsArrowConfig original = PhysicsArrowConfig.defaults();

    final PhysicsArrowConfig updated = original.withRicochetBounceCount(5);

    assertEquals(5, updated.ricochetBounceCount());
    assertEquals(original.gravityImpactRadius(), updated.gravityImpactRadius());
    assertEquals(original.gravityBlockExclusions(), updated.gravityBlockExclusions());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        PhysicsArrowConfig.RICOCHET_BOUNCE_COUNT_MAX,
        PhysicsArrowConfig.defaults().withRicochetBounceCount(999).ricochetBounceCount());
  }

  @Test
  void aChangedExclusionListIsStillNormalised() {
    assertEquals(
        java.util.List.of("minecraft:sand"),
        PhysicsArrowConfig.defaults()
            .withGravityBlockExclusions(java.util.List.of("  MINECRAFT:SAND  ", "minecraft:sand"))
            .gravityBlockExclusions());
  }

  @Test
  void everyPhysicsFieldCanBeChangedOnItsOwn() {
    final PhysicsArrowConfig updated =
        PhysicsArrowConfig.defaults()
            .withGravityImpactRadius(4)
            .withGravityBlockExclusions(java.util.List.of("minecraft:sand"))
            .withRicochetBounceCount(5)
            .withRicochetRetainsDamage(false);

    assertEquals(new PhysicsArrowConfig(4, java.util.List.of("minecraft:sand"), 5, false), updated);
  }
}
