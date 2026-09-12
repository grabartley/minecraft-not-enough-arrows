package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.option.FloatOption;
import com.grahambartley.notenougharrows.config.option.IntOption;

public record OptionScale(double min, double max, double step) {

  public OptionScale {
    if (max < min) {
      throw new IllegalArgumentException("Scale max " + max + " is below min " + min);
    }
    if (step <= 0.0d) {
      throw new IllegalArgumentException("Scale step must be positive but was " + step);
    }
  }

  public static OptionScale of(final IntOption<?> option) {
    return new OptionScale(option.min(), option.max(), 1.0d);
  }

  public static OptionScale of(final FloatOption<?> option) {
    return new OptionScale(option.min(), option.max(), option.step());
  }

  public double toProgress(final double value) {
    if (max == min) {
      return 0.0d;
    }
    return clampProgress((value - min) / (max - min));
  }

  public double toValue(final double progress) {
    final double span = max - min;
    final double raw = min + clampProgress(progress) * span;
    final double snapped = min + Math.round((raw - min) / step) * step;
    return Math.max(min, Math.min(max, snapped));
  }

  private static double clampProgress(final double progress) {
    return Math.max(0.0d, Math.min(1.0d, progress));
  }
}
