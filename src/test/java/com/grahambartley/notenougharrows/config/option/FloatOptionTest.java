package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FloatOptionTest {
  private static final float TOLERANCE = 1.0e-6f;

  private static FloatOption<NotEnoughArrowsConfig> pullSpeed() {
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
        expected,
        pullSpeed().read(pullSpeed().write(NotEnoughArrowsConfig.defaults(), written)),
        TOLERANCE);
  }

  @Test
  void readsTheValueOffTheSubject() {
    assertEquals(
        GrappleArrowConfig.DEFAULT_PULL_SPEED,
        pullSpeed().read(NotEnoughArrowsConfig.defaults()),
        TOLERANCE);
  }

  @Test
  void displaysTheValueAsText() {
    assertEquals("1.50", pullSpeed().displayValue(NotEnoughArrowsConfig.defaults()));
  }

  @Test
  void rejectsAMinAboveItsMax() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new FloatOption<NotEnoughArrowsConfig>(
                "broken", 4.0f, 1.0f, 0.1f, config -> 0.0f, (config, value) -> config));
  }

  @ParameterizedTest
  @CsvSource({"0.0", "-0.5"})
  void rejectsANonPositiveStep(final float step) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new FloatOption<NotEnoughArrowsConfig>(
                "broken", 0.0f, 1.0f, step, config -> 0.0f, (config, value) -> config));
  }
}
