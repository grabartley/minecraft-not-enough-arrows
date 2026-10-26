package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.volley.config.GrappleArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FloatOptionTest {
  private static final float TOLERANCE = 1.0e-6f;

  private static FloatOption<VolleyConfig> pullSpeed() {
    return new FloatOption<>(
        "grapple.pullSpeed",
        0.1f,
        4.0f,
        0.05f,
        config -> config.grapple().pullSpeed(),
        (config, value) -> config.withGrapple(config.grapple().withPullSpeed(value)));
  }

  @ParameterizedTest(name = "write {0} stores {1}")
  @CsvSource({"0.1,0.1", "2.5,2.5", "4.0,4.0", "0.0,0.1", "-3.0,0.1", "4.5,4.0", "900.0,4.0"})
  void clampsWritesIntoItsOwnRange(final float written, final float expected) {
    assertEquals(
        expected, pullSpeed().read(pullSpeed().write(VolleyConfig.defaults(), written)), TOLERANCE);
  }

  @Test
  void readsTheValueOffTheSubject() {
    assertEquals(
        GrappleArrowConfig.DEFAULT_PULL_SPEED,
        pullSpeed().read(VolleyConfig.defaults()),
        TOLERANCE);
  }

  @Test
  void displaysTheValueAsText() {
    assertEquals("1.50", pullSpeed().displayValue(VolleyConfig.defaults()));
  }

  @Test
  void rejectsAMinAboveItsMax() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new FloatOption<VolleyConfig>(
                "broken", 4.0f, 1.0f, 0.1f, config -> 0.0f, (config, value) -> config));
  }

  @ParameterizedTest
  @CsvSource({"0.0", "-0.5"})
  void rejectsANonPositiveStep(final float step) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new FloatOption<VolleyConfig>(
                "broken", 0.0f, 1.0f, step, config -> 0.0f, (config, value) -> config));
  }
}
