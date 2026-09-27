package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class DiscoveryArrowConfigTest {

  @Test
  void defaultsEveryArrow() {
    final DiscoveryArrowConfig defaults = DiscoveryArrowConfig.defaults();

    assertEquals(TorchArrowConfig.defaults(), defaults.torch());
    assertEquals(BeaconArrowConfig.defaults(), defaults.beacon());
    assertEquals(ProspectorArrowConfig.defaults(), defaults.prospector());
    assertEquals(SonarArrowConfig.defaults(), defaults.sonar());
    assertEquals(TripwireArrowConfig.defaults(), defaults.tripwire());
    assertEquals(TracerArrowConfig.defaults(), defaults.tracer());
  }

  @Test
  void substitutesDefaultsForMissingArrows() {
    assertEquals(
        DiscoveryArrowConfig.defaults(),
        new DiscoveryArrowConfig(null, null, null, null, null, null));
  }

  @Test
  void replacesOnlyTheTorchArrow() {
    final TorchArrowConfig replacement = TorchArrowConfig.defaults().withEnabled(false);
    final DiscoveryArrowConfig updated = DiscoveryArrowConfig.defaults().withTorch(replacement);

    assertEquals(replacement, updated.torch());
    assertEquals(BeaconArrowConfig.defaults(), updated.beacon());
    assertEquals(ProspectorArrowConfig.defaults(), updated.prospector());
    assertEquals(SonarArrowConfig.defaults(), updated.sonar());
    assertEquals(TripwireArrowConfig.defaults(), updated.tripwire());
    assertEquals(TracerArrowConfig.defaults(), updated.tracer());
  }

  @Test
  void replacesOnlyTheBeaconArrow() {
    final BeaconArrowConfig replacement = BeaconArrowConfig.defaults().withLifetimeTicks(11999);
    final DiscoveryArrowConfig updated = DiscoveryArrowConfig.defaults().withBeacon(replacement);

    assertEquals(replacement, updated.beacon());
    assertEquals(TorchArrowConfig.defaults(), updated.torch());
    assertEquals(ProspectorArrowConfig.defaults(), updated.prospector());
    assertEquals(SonarArrowConfig.defaults(), updated.sonar());
    assertEquals(TripwireArrowConfig.defaults(), updated.tripwire());
    assertEquals(TracerArrowConfig.defaults(), updated.tracer());
  }

  @Test
  void replacesOnlyTheProspectorArrow() {
    final ProspectorArrowConfig replacement = ProspectorArrowConfig.defaults().withRadius(15);
    final DiscoveryArrowConfig updated =
        DiscoveryArrowConfig.defaults().withProspector(replacement);

    assertEquals(replacement, updated.prospector());
    assertEquals(TorchArrowConfig.defaults(), updated.torch());
    assertEquals(BeaconArrowConfig.defaults(), updated.beacon());
    assertEquals(SonarArrowConfig.defaults(), updated.sonar());
    assertEquals(TripwireArrowConfig.defaults(), updated.tripwire());
    assertEquals(TracerArrowConfig.defaults(), updated.tracer());
  }

  @Test
  void replacesOnlyTheSonarArrow() {
    final SonarArrowConfig replacement = SonarArrowConfig.defaults().withRadius(31);
    final DiscoveryArrowConfig updated = DiscoveryArrowConfig.defaults().withSonar(replacement);

    assertEquals(replacement, updated.sonar());
    assertEquals(TorchArrowConfig.defaults(), updated.torch());
    assertEquals(BeaconArrowConfig.defaults(), updated.beacon());
    assertEquals(ProspectorArrowConfig.defaults(), updated.prospector());
    assertEquals(TripwireArrowConfig.defaults(), updated.tripwire());
    assertEquals(TracerArrowConfig.defaults(), updated.tracer());
  }

  @Test
  void replacesOnlyTheTripwireArrow() {
    final TripwireArrowConfig replacement = TripwireArrowConfig.defaults().withLifetimeTicks(23999);
    final DiscoveryArrowConfig updated = DiscoveryArrowConfig.defaults().withTripwire(replacement);

    assertEquals(replacement, updated.tripwire());
    assertEquals(TorchArrowConfig.defaults(), updated.torch());
    assertEquals(BeaconArrowConfig.defaults(), updated.beacon());
    assertEquals(ProspectorArrowConfig.defaults(), updated.prospector());
    assertEquals(SonarArrowConfig.defaults(), updated.sonar());
    assertEquals(TracerArrowConfig.defaults(), updated.tracer());
  }

  @Test
  void replacesOnlyTheTracerArrow() {
    final TracerArrowConfig replacement = TracerArrowConfig.defaults().withPathLifetimeTicks(1199);
    final DiscoveryArrowConfig updated = DiscoveryArrowConfig.defaults().withTracer(replacement);

    assertEquals(replacement, updated.tracer());
    assertEquals(TorchArrowConfig.defaults(), updated.torch());
    assertEquals(BeaconArrowConfig.defaults(), updated.beacon());
    assertEquals(ProspectorArrowConfig.defaults(), updated.prospector());
    assertEquals(SonarArrowConfig.defaults(), updated.sonar());
    assertEquals(TripwireArrowConfig.defaults(), updated.tripwire());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(DiscoveryArrowConfig.defaults(), DiscoveryArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOneArrowAndDefaultsTheRest() {
    final DiscoveryArrowConfig parsed =
        DiscoveryArrowConfig.fromJson(
            JsonParser.parseString("{\"torch\":{\"enabled\":False}}").getAsJsonObject());

    assertEquals(false, parsed.torch().enabled());
    assertEquals(BeaconArrowConfig.defaults(), parsed.beacon());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = DiscoveryArrowConfig.defaults().toJson();

    assertTrue(json.get("torch").isJsonObject());
    assertTrue(json.get("beacon").isJsonObject());
    assertTrue(json.get("prospector").isJsonObject());
    assertTrue(json.get("sonar").isJsonObject());
    assertTrue(json.get("tripwire").isJsonObject());
    assertTrue(json.get("tracer").isJsonObject());
  }

  @Test
  void roundTripsThroughJson() {
    final DiscoveryArrowConfig original =
        new DiscoveryArrowConfig(
            TorchArrowConfig.defaults().withEnabled(false),
            BeaconArrowConfig.defaults().withLifetimeTicks(11999),
            ProspectorArrowConfig.defaults().withRadius(15),
            SonarArrowConfig.defaults().withRadius(31),
            TripwireArrowConfig.defaults().withLifetimeTicks(23999),
            TracerArrowConfig.defaults().withPathLifetimeTicks(1199));

    assertEquals(original, DiscoveryArrowConfig.fromJson(original.toJson()));
  }
}
