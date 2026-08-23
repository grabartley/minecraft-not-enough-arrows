package com.grahambartley.morearrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.option.FloatOption;
import com.grahambartley.morearrows.config.option.IntOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OptionScaleTest {
  private static final double TOLERANCE = 1.0e-9d;

  @ParameterizedTest(name = "value {0} sits at progress {1}")
  @CsvSource({"0,0.0", "100,0.5", "200,1.0", "-50,0.0", "500,1.0"})
  void mapsAValueOntoSliderProgress(final double value, final double expected) {
    assertEquals(expected, new OptionScale(0, 200, 1).toProgress(value), TOLERANCE);
  }

  @ParameterizedTest(name = "progress {0} is value {1}")
  @CsvSource({"0.0,0", "0.5,100", "1.0,200", "-1.0,0", "2.0,200"})
  void mapsSliderProgressBackOntoAValue(final double progress, final double expected) {
    assertEquals(expected, new OptionScale(0, 200, 1).toValue(progress), TOLERANCE);
  }

  @ParameterizedTest(name = "progress {0} snaps to {1}")
  @CsvSource({"0.0,0.1", "0.5,2.05", "1.0,4.0", "0.01,0.15"})
  void snapsValuesOntoItsStep(final double progress, final double expected) {
    assertEquals(expected, new OptionScale(0.1d, 4.0d, 0.05d).toValue(progress), 1.0e-6d);
  }

  @Test
  void treatsAnEmptyRangeAsFullyLeft() {
    assertEquals(0.0d, new OptionScale(5, 5, 1).toProgress(5), TOLERANCE);
    assertEquals(5.0d, new OptionScale(5, 5, 1).toValue(1.0d), TOLERANCE);
  }

  @Test
  void takesItsBoundsFromAnIntOption() {
    final IntOption<MoreArrowsConfig> option =
        new IntOption<>(
            "grapple.ropeLengthBlocks", 1, 128, config -> 16, (config, value) -> config);

    assertEquals(new OptionScale(1, 128, 1), OptionScale.of(option));
  }

  @Test
  void takesItsBoundsAndStepFromAFloatOption() {
    final FloatOption<MoreArrowsConfig> option =
        new FloatOption<>(
            "grapple.pullSpeed", 0.1f, 4.0f, 0.05f, config -> 0.8f, (config, value) -> config);

    final OptionScale scale = OptionScale.of(option);

    assertEquals(0.1f, scale.min(), 1.0e-6d);
    assertEquals(4.0f, scale.max(), 1.0e-6d);
    assertEquals(0.05f, scale.step(), 1.0e-6d);
  }

  @Test
  void rejectsAMaxBelowItsMin() {
    assertThrows(IllegalArgumentException.class, () -> new OptionScale(4, 1, 1));
  }

  @ParameterizedTest
  @CsvSource({"0.0", "-1.0"})
  void rejectsANonPositiveStep(final double step) {
    assertThrows(IllegalArgumentException.class, () -> new OptionScale(0, 1, step));
  }
}
