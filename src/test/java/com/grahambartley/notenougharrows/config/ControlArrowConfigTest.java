package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

class ControlArrowConfigTest {

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ControlArrowConfig.defaults(), ControlArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void replacesEveryNullSubRecordWithItsDefaults() {
    assertEquals(
        ControlArrowConfig.defaults(), new ControlArrowConfig(null, null, null, null, null));
  }

  @Test
  void roundTripsThroughJson() {
    final ControlArrowConfig original =
        new ControlArrowConfig(
            new FrostArrowConfig(11),
            new LevitationArrowConfig(22),
            new TargetingArrowConfig(1.0f, 33, 2.0f, 44, 55),
            new SmokeArrowConfig(3.0f, 66),
            new DisarmArrowConfig(false));

    assertEquals(original, ControlArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneSubRecordLeavesTheRestAlone() {
    final ControlArrowConfig updated =
        ControlArrowConfig.defaults().withSmoke(new SmokeArrowConfig(9.0f, 90));

    assertEquals(9.0f, updated.smoke().radius());
    assertEquals(FrostArrowConfig.defaults(), updated.frost());
    assertEquals(DisarmArrowConfig.defaults(), updated.disarm());
  }
}
