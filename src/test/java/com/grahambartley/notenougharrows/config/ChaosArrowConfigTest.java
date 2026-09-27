package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class ChaosArrowConfigTest {

  @Test
  void defaultsEveryArrow() {
    final ChaosArrowConfig defaults = ChaosArrowConfig.defaults();

    assertEquals(PartyArrowConfig.defaults(), defaults.party());
    assertEquals(ChickenArrowConfig.defaults(), defaults.chicken());
    assertEquals(PufferArrowConfig.defaults(), defaults.puffer());
    assertEquals(StinkArrowConfig.defaults(), defaults.stink());
    assertEquals(BoomerangArrowConfig.defaults(), defaults.boomerang());
    assertEquals(PolymorphArrowConfig.defaults(), defaults.polymorph());
  }

  @Test
  void substitutesDefaultsForMissingArrows() {
    assertEquals(
        ChaosArrowConfig.defaults(), new ChaosArrowConfig(null, null, null, null, null, null));
  }

  @Test
  void replacesOnlyThePartyArrow() {
    final PartyArrowConfig replacement = PartyArrowConfig.defaults().withEnabled(false);
    final ChaosArrowConfig updated = ChaosArrowConfig.defaults().withParty(replacement);

    assertEquals(replacement, updated.party());
    assertEquals(ChickenArrowConfig.defaults(), updated.chicken());
    assertEquals(PufferArrowConfig.defaults(), updated.puffer());
    assertEquals(StinkArrowConfig.defaults(), updated.stink());
    assertEquals(BoomerangArrowConfig.defaults(), updated.boomerang());
    assertEquals(PolymorphArrowConfig.defaults(), updated.polymorph());
  }

  @Test
  void replacesOnlyTheChickenArrow() {
    final ChickenArrowConfig replacement = ChickenArrowConfig.defaults().withEnabled(false);
    final ChaosArrowConfig updated = ChaosArrowConfig.defaults().withChicken(replacement);

    assertEquals(replacement, updated.chicken());
    assertEquals(PartyArrowConfig.defaults(), updated.party());
    assertEquals(PufferArrowConfig.defaults(), updated.puffer());
    assertEquals(StinkArrowConfig.defaults(), updated.stink());
    assertEquals(BoomerangArrowConfig.defaults(), updated.boomerang());
    assertEquals(PolymorphArrowConfig.defaults(), updated.polymorph());
  }

  @Test
  void replacesOnlyThePufferArrow() {
    final PufferArrowConfig replacement = PufferArrowConfig.defaults().withEnabled(false);
    final ChaosArrowConfig updated = ChaosArrowConfig.defaults().withPuffer(replacement);

    assertEquals(replacement, updated.puffer());
    assertEquals(PartyArrowConfig.defaults(), updated.party());
    assertEquals(ChickenArrowConfig.defaults(), updated.chicken());
    assertEquals(StinkArrowConfig.defaults(), updated.stink());
    assertEquals(BoomerangArrowConfig.defaults(), updated.boomerang());
    assertEquals(PolymorphArrowConfig.defaults(), updated.polymorph());
  }

  @Test
  void replacesOnlyTheStinkArrow() {
    final StinkArrowConfig replacement = StinkArrowConfig.defaults().withEnabled(false);
    final ChaosArrowConfig updated = ChaosArrowConfig.defaults().withStink(replacement);

    assertEquals(replacement, updated.stink());
    assertEquals(PartyArrowConfig.defaults(), updated.party());
    assertEquals(ChickenArrowConfig.defaults(), updated.chicken());
    assertEquals(PufferArrowConfig.defaults(), updated.puffer());
    assertEquals(BoomerangArrowConfig.defaults(), updated.boomerang());
    assertEquals(PolymorphArrowConfig.defaults(), updated.polymorph());
  }

  @Test
  void replacesOnlyTheBoomerangArrow() {
    final BoomerangArrowConfig replacement = BoomerangArrowConfig.defaults().withEnabled(false);
    final ChaosArrowConfig updated = ChaosArrowConfig.defaults().withBoomerang(replacement);

    assertEquals(replacement, updated.boomerang());
    assertEquals(PartyArrowConfig.defaults(), updated.party());
    assertEquals(ChickenArrowConfig.defaults(), updated.chicken());
    assertEquals(PufferArrowConfig.defaults(), updated.puffer());
    assertEquals(StinkArrowConfig.defaults(), updated.stink());
    assertEquals(PolymorphArrowConfig.defaults(), updated.polymorph());
  }

  @Test
  void replacesOnlyThePolymorphArrow() {
    final PolymorphArrowConfig replacement = PolymorphArrowConfig.defaults().withEnabled(false);
    final ChaosArrowConfig updated = ChaosArrowConfig.defaults().withPolymorph(replacement);

    assertEquals(replacement, updated.polymorph());
    assertEquals(PartyArrowConfig.defaults(), updated.party());
    assertEquals(ChickenArrowConfig.defaults(), updated.chicken());
    assertEquals(PufferArrowConfig.defaults(), updated.puffer());
    assertEquals(StinkArrowConfig.defaults(), updated.stink());
    assertEquals(BoomerangArrowConfig.defaults(), updated.boomerang());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ChaosArrowConfig.defaults(), ChaosArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOneArrowAndDefaultsTheRest() {
    final ChaosArrowConfig parsed =
        ChaosArrowConfig.fromJson(
            JsonParser.parseString("{\"party\":{\"enabled\":False}}").getAsJsonObject());

    assertEquals(false, parsed.party().enabled());
    assertEquals(ChickenArrowConfig.defaults(), parsed.chicken());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = ChaosArrowConfig.defaults().toJson();

    assertTrue(json.get("party").isJsonObject());
    assertTrue(json.get("chicken").isJsonObject());
    assertTrue(json.get("puffer").isJsonObject());
    assertTrue(json.get("stink").isJsonObject());
    assertTrue(json.get("boomerang").isJsonObject());
    assertTrue(json.get("polymorph").isJsonObject());
  }

  @Test
  void roundTripsThroughJson() {
    final ChaosArrowConfig original =
        new ChaosArrowConfig(
            PartyArrowConfig.defaults().withEnabled(false),
            ChickenArrowConfig.defaults().withEnabled(false),
            PufferArrowConfig.defaults().withEnabled(false),
            StinkArrowConfig.defaults().withEnabled(false),
            BoomerangArrowConfig.defaults().withEnabled(false),
            PolymorphArrowConfig.defaults().withEnabled(false));

    assertEquals(original, ChaosArrowConfig.fromJson(original.toJson()));
  }
}
