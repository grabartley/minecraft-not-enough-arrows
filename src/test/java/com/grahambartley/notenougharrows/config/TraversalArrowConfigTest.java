package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class TraversalArrowConfigTest {

  @Test
  void defaultsEveryArrow() {
    final TraversalArrowConfig defaults = TraversalArrowConfig.defaults();

    assertEquals(ZiplineArrowConfig.defaults(), defaults.zipline());
    assertEquals(TowArrowConfig.defaults(), defaults.tow());
    assertEquals(UpdraftArrowConfig.defaults(), defaults.updraft());
    assertEquals(VineArrowConfig.defaults(), defaults.vine());
    assertEquals(TrampolineArrowConfig.defaults(), defaults.trampoline());
    assertEquals(ScaffoldArrowConfig.defaults(), defaults.scaffold());
    assertEquals(BridgeArrowConfig.defaults(), defaults.bridge());
  }

  @Test
  void substitutesDefaultsForMissingArrows() {
    assertEquals(
        TraversalArrowConfig.defaults(),
        new TraversalArrowConfig(null, null, null, null, null, null, null));
  }

  @Test
  void replacesOnlyTheZiplineArrow() {
    final ZiplineArrowConfig replacement = ZiplineArrowConfig.defaults().withMaxSpanBlocks(127);
    final TraversalArrowConfig updated = TraversalArrowConfig.defaults().withZipline(replacement);

    assertEquals(replacement, updated.zipline());
    assertEquals(TowArrowConfig.defaults(), updated.tow());
    assertEquals(UpdraftArrowConfig.defaults(), updated.updraft());
    assertEquals(VineArrowConfig.defaults(), updated.vine());
    assertEquals(TrampolineArrowConfig.defaults(), updated.trampoline());
    assertEquals(ScaffoldArrowConfig.defaults(), updated.scaffold());
    assertEquals(BridgeArrowConfig.defaults(), updated.bridge());
  }

  @Test
  void replacesOnlyTheTowArrow() {
    final TowArrowConfig replacement = TowArrowConfig.defaults().withRangeBlocks(127);
    final TraversalArrowConfig updated = TraversalArrowConfig.defaults().withTow(replacement);

    assertEquals(replacement, updated.tow());
    assertEquals(ZiplineArrowConfig.defaults(), updated.zipline());
    assertEquals(UpdraftArrowConfig.defaults(), updated.updraft());
    assertEquals(VineArrowConfig.defaults(), updated.vine());
    assertEquals(TrampolineArrowConfig.defaults(), updated.trampoline());
    assertEquals(ScaffoldArrowConfig.defaults(), updated.scaffold());
    assertEquals(BridgeArrowConfig.defaults(), updated.bridge());
  }

  @Test
  void replacesOnlyTheUpdraftArrow() {
    final UpdraftArrowConfig replacement = UpdraftArrowConfig.defaults().withHeightBlocks(63);
    final TraversalArrowConfig updated = TraversalArrowConfig.defaults().withUpdraft(replacement);

    assertEquals(replacement, updated.updraft());
    assertEquals(ZiplineArrowConfig.defaults(), updated.zipline());
    assertEquals(TowArrowConfig.defaults(), updated.tow());
    assertEquals(VineArrowConfig.defaults(), updated.vine());
    assertEquals(TrampolineArrowConfig.defaults(), updated.trampoline());
    assertEquals(ScaffoldArrowConfig.defaults(), updated.scaffold());
    assertEquals(BridgeArrowConfig.defaults(), updated.bridge());
  }

  @Test
  void replacesOnlyTheVineArrow() {
    final VineArrowConfig replacement = VineArrowConfig.defaults().withLengthBlocks(63);
    final TraversalArrowConfig updated = TraversalArrowConfig.defaults().withVine(replacement);

    assertEquals(replacement, updated.vine());
    assertEquals(ZiplineArrowConfig.defaults(), updated.zipline());
    assertEquals(TowArrowConfig.defaults(), updated.tow());
    assertEquals(UpdraftArrowConfig.defaults(), updated.updraft());
    assertEquals(TrampolineArrowConfig.defaults(), updated.trampoline());
    assertEquals(ScaffoldArrowConfig.defaults(), updated.scaffold());
    assertEquals(BridgeArrowConfig.defaults(), updated.bridge());
  }

  @Test
  void replacesOnlyTheTrampolineArrow() {
    final TrampolineArrowConfig replacement = TrampolineArrowConfig.defaults().withStrength(3.5f);
    final TraversalArrowConfig updated =
        TraversalArrowConfig.defaults().withTrampoline(replacement);

    assertEquals(replacement, updated.trampoline());
    assertEquals(ZiplineArrowConfig.defaults(), updated.zipline());
    assertEquals(TowArrowConfig.defaults(), updated.tow());
    assertEquals(UpdraftArrowConfig.defaults(), updated.updraft());
    assertEquals(VineArrowConfig.defaults(), updated.vine());
    assertEquals(ScaffoldArrowConfig.defaults(), updated.scaffold());
    assertEquals(BridgeArrowConfig.defaults(), updated.bridge());
  }

  @Test
  void replacesOnlyTheScaffoldArrow() {
    final ScaffoldArrowConfig replacement = ScaffoldArrowConfig.defaults().withHeightBlocks(63);
    final TraversalArrowConfig updated = TraversalArrowConfig.defaults().withScaffold(replacement);

    assertEquals(replacement, updated.scaffold());
    assertEquals(ZiplineArrowConfig.defaults(), updated.zipline());
    assertEquals(TowArrowConfig.defaults(), updated.tow());
    assertEquals(UpdraftArrowConfig.defaults(), updated.updraft());
    assertEquals(VineArrowConfig.defaults(), updated.vine());
    assertEquals(TrampolineArrowConfig.defaults(), updated.trampoline());
    assertEquals(BridgeArrowConfig.defaults(), updated.bridge());
  }

  @Test
  void replacesOnlyTheBridgeArrow() {
    final BridgeArrowConfig replacement = BridgeArrowConfig.defaults().withLengthBlocks(63);
    final TraversalArrowConfig updated = TraversalArrowConfig.defaults().withBridge(replacement);

    assertEquals(replacement, updated.bridge());
    assertEquals(ZiplineArrowConfig.defaults(), updated.zipline());
    assertEquals(TowArrowConfig.defaults(), updated.tow());
    assertEquals(UpdraftArrowConfig.defaults(), updated.updraft());
    assertEquals(VineArrowConfig.defaults(), updated.vine());
    assertEquals(TrampolineArrowConfig.defaults(), updated.trampoline());
    assertEquals(ScaffoldArrowConfig.defaults(), updated.scaffold());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TraversalArrowConfig.defaults(), TraversalArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOneArrowAndDefaultsTheRest() {
    final TraversalArrowConfig parsed =
        TraversalArrowConfig.fromJson(
            JsonParser.parseString("{\"zipline\":{\"maxSpanBlocks\":127}}").getAsJsonObject());

    assertEquals(127, parsed.zipline().maxSpanBlocks());
    assertEquals(TowArrowConfig.defaults(), parsed.tow());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = TraversalArrowConfig.defaults().toJson();

    assertTrue(json.get("zipline").isJsonObject());
    assertTrue(json.get("tow").isJsonObject());
    assertTrue(json.get("updraft").isJsonObject());
    assertTrue(json.get("vine").isJsonObject());
    assertTrue(json.get("trampoline").isJsonObject());
    assertTrue(json.get("scaffold").isJsonObject());
    assertTrue(json.get("bridge").isJsonObject());
  }

  @Test
  void roundTripsThroughJson() {
    final TraversalArrowConfig original =
        new TraversalArrowConfig(
            ZiplineArrowConfig.defaults().withMaxSpanBlocks(127),
            TowArrowConfig.defaults().withRangeBlocks(127),
            UpdraftArrowConfig.defaults().withHeightBlocks(63),
            VineArrowConfig.defaults().withLengthBlocks(63),
            TrampolineArrowConfig.defaults().withStrength(3.5f),
            ScaffoldArrowConfig.defaults().withHeightBlocks(63),
            BridgeArrowConfig.defaults().withLengthBlocks(63));

    assertEquals(original, TraversalArrowConfig.fromJson(original.toJson()));
  }
}
