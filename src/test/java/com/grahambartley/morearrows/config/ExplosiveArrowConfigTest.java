package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExplosiveArrowConfigTest {

  @Test
  void defaultsToNoTerrainDamageSoSharedServersAreSafeOutOfTheBox() {
    assertFalse(ExplosiveArrowConfig.defaults().damageTerrain());
  }

  @Test
  void defaultsToDamagingEntities() {
    assertTrue(ExplosiveArrowConfig.defaults().damageEntities());
  }

  @Test
  void defaultsToEscalatingPowerAcrossTiers() {
    final ExplosiveArrowConfig defaults = ExplosiveArrowConfig.defaults();

    assertTrue(defaults.gunpowder().power() < defaults.tnt().power());
    assertTrue(defaults.tnt().power() < defaults.fireCharge().power());
  }

  @Test
  void defaultsToATelegraphedCountdownOnEveryTier() {
    final ExplosiveArrowConfig defaults = ExplosiveArrowConfig.defaults();

    assertFalse(defaults.gunpowder().detonatesOnContact());
    assertFalse(defaults.tnt().detonatesOnContact());
    assertFalse(defaults.fireCharge().detonatesOnContact());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "2, 2", "8, 8", "99, 8"})
  void clampsFirePatchRadius(final int given, final int expected) {
    assertEquals(expected, withFirePatchRadius(given).firePatchRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsFirePatchDuration(final int given, final int expected) {
    final ExplosiveArrowConfig defaults = ExplosiveArrowConfig.defaults();
    final ExplosiveArrowConfig config =
        new ExplosiveArrowConfig(
            defaults.gunpowder(),
            defaults.tnt(),
            defaults.fireCharge(),
            defaults.damageTerrain(),
            defaults.damageEntities(),
            defaults.firePatchRadius(),
            given,
            defaults.beepVolume());

    assertEquals(expected, config.firePatchDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "1.0, 1.0", "2.0, 2.0", "5.0, 2.0"})
  void clampsBeepVolume(final float given, final float expected) {
    final ExplosiveArrowConfig defaults = ExplosiveArrowConfig.defaults();
    final ExplosiveArrowConfig config =
        new ExplosiveArrowConfig(
            defaults.gunpowder(),
            defaults.tnt(),
            defaults.fireCharge(),
            defaults.damageTerrain(),
            defaults.damageEntities(),
            defaults.firePatchRadius(),
            defaults.firePatchDurationTicks(),
            given);

    assertEquals(expected, config.beepVolume());
  }

  @Test
  void substitutesDefaultsForNullTiers() {
    final ExplosiveArrowConfig config =
        new ExplosiveArrowConfig(null, null, null, false, true, 2, 200, 1.0f);

    assertEquals(ExplosiveArrowConfig.DEFAULT_GUNPOWDER, config.gunpowder());
    assertEquals(ExplosiveArrowConfig.DEFAULT_TNT, config.tnt());
    assertEquals(ExplosiveArrowConfig.DEFAULT_FIRE_CHARGE, config.fireCharge());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ExplosiveArrowConfig.defaults(), ExplosiveArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ExplosiveArrowConfig config =
        ExplosiveArrowConfig.fromJson(
            JsonParser.parseString("{\"damageTerrain\":true}").getAsJsonObject());

    assertTrue(config.damageTerrain());
    assertEquals(ExplosiveArrowConfig.DEFAULT_BEEP_VOLUME, config.beepVolume());
    assertEquals(ExplosiveArrowConfig.DEFAULT_GUNPOWDER, config.gunpowder());
  }

  @Test
  void readsNestedTierValues() {
    final ExplosiveArrowConfig config =
        ExplosiveArrowConfig.fromJson(
            JsonParser.parseString("{\"tnt\":{\"delayTicks\":0,\"power\":9.0}}").getAsJsonObject());

    assertTrue(config.tnt().detonatesOnContact());
    assertEquals(9.0f, config.tnt().power());
    assertEquals(ExplosiveArrowConfig.DEFAULT_GUNPOWDER, config.gunpowder());
  }

  @Test
  void roundTripsThroughJson() {
    final ExplosiveArrowConfig original =
        new ExplosiveArrowConfig(
            new ExplosiveTierConfig(10, 1.5f),
            new ExplosiveTierConfig(20, 2.5f),
            new ExplosiveTierConfig(0, 12.0f),
            true,
            false,
            5,
            1234,
            0.25f);

    assertEquals(original, ExplosiveArrowConfig.fromJson(original.toJson()));
  }

  private static ExplosiveArrowConfig withFirePatchRadius(final int radius) {
    final ExplosiveArrowConfig defaults = ExplosiveArrowConfig.defaults();
    return new ExplosiveArrowConfig(
        defaults.gunpowder(),
        defaults.tnt(),
        defaults.fireCharge(),
        defaults.damageTerrain(),
        defaults.damageEntities(),
        radius,
        defaults.firePatchDurationTicks(),
        defaults.beepVolume());
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final ExplosiveArrowConfig original = ExplosiveArrowConfig.defaults();

    final ExplosiveArrowConfig updated = original.withFirePatchRadius(5);

    assertEquals(5, updated.firePatchRadius());
    assertEquals(original.gunpowder(), updated.gunpowder());
    assertEquals(original.beepVolume(), updated.beepVolume());
    assertEquals(original.damageEntities(), updated.damageEntities());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        ExplosiveArrowConfig.FIRE_PATCH_RADIUS_MAX,
        ExplosiveArrowConfig.defaults().withFirePatchRadius(999).firePatchRadius());
  }

  @Test
  void everyExplosiveFieldCanBeChangedOnItsOwn() {
    final ExplosiveTierConfig tier = new ExplosiveTierConfig(10, 2.0f);

    final ExplosiveArrowConfig updated =
        ExplosiveArrowConfig.defaults()
            .withGunpowder(tier)
            .withTnt(tier)
            .withFireCharge(tier)
            .withDamageTerrain(true)
            .withDamageEntities(false)
            .withFirePatchRadius(5)
            .withFirePatchDurationTicks(400)
            .withBeepVolume(0.5f);

    assertEquals(new ExplosiveArrowConfig(tier, tier, tier, true, false, 5, 400, 0.5f), updated);
  }
}
