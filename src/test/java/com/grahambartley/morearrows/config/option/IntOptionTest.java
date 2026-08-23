package com.grahambartley.morearrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class IntOptionTest {

  private static IntOption<MoreArrowsConfig> ropeLength() {
    return new IntOption<>(
        "grapple.ropeLengthBlocks",
        1,
        128,
        config -> config.grapple().ropeLengthBlocks(),
        (config, value) -> config.withGrapple(config.grapple().withRopeLengthBlocks(value)));
  }

  @ParameterizedTest(name = "write {0} stores {1}")
  @CsvSource({"1,1", "64,64", "128,128", "0,1", "-40,1", "129,128", "9000,128"})
  void clampsWritesIntoItsOwnRange(final int written, final int expected) {
    assertEquals(
        expected, ropeLength().read(ropeLength().write(MoreArrowsConfig.defaults(), written)));
  }

  @Test
  void readsTheValueOffTheSubject() {
    assertEquals(16, ropeLength().read(MoreArrowsConfig.defaults()));
  }

  @Test
  void displaysTheValueAsText() {
    assertEquals("16", ropeLength().displayValue(MoreArrowsConfig.defaults()));
  }

  @Test
  void rejectsAMinAboveItsMax() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new IntOption<MoreArrowsConfig>(
                "broken", 10, 2, config -> 0, (config, value) -> config));
  }
}
