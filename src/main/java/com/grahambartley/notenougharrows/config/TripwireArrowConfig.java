package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TripwireArrowConfig(int lifetimeTicks, int reportIntervalTicks) {

  public static final int LIFETIME_TICKS_MIN = 20;
  public static final int LIFETIME_TICKS_MAX = 24000;
  public static final int REPORT_INTERVAL_TICKS_MIN = 20;
  public static final int REPORT_INTERVAL_TICKS_MAX = 1200;

  public static final int DEFAULT_LIFETIME_TICKS = 6000;
  public static final int DEFAULT_REPORT_INTERVAL_TICKS = 40;

  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";
  static final String KEY_REPORT_INTERVAL_TICKS = "reportIntervalTicks";

  public TripwireArrowConfig {
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
    reportIntervalTicks =
        ConfigValues.clampInt(
            reportIntervalTicks, REPORT_INTERVAL_TICKS_MIN, REPORT_INTERVAL_TICKS_MAX);
  }

  public static TripwireArrowConfig defaults() {
    return new TripwireArrowConfig(DEFAULT_LIFETIME_TICKS, DEFAULT_REPORT_INTERVAL_TICKS);
  }

  public static TripwireArrowConfig fromJson(final JsonObject root) {
    final TripwireArrowConfig defaults = defaults();
    return new TripwireArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_REPORT_INTERVAL_TICKS,
            defaults.reportIntervalTicks(),
            REPORT_INTERVAL_TICKS_MIN,
            REPORT_INTERVAL_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    root.addProperty(KEY_REPORT_INTERVAL_TICKS, reportIntervalTicks);
    return root;
  }

  public TripwireArrowConfig withLifetimeTicks(final int value) {
    return new TripwireArrowConfig(value, reportIntervalTicks);
  }

  public TripwireArrowConfig withReportIntervalTicks(final int value) {
    return new TripwireArrowConfig(lifetimeTicks, value);
  }
}
