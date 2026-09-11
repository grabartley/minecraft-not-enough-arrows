package com.grahambartley.morearrows.fletching;

public final class FletchingStationGate {
  private FletchingStationGate() {}

  public static boolean opensStation(
      final boolean stationEnabled,
      final boolean fletchingTable,
      final boolean cancellingInteraction,
      final boolean handsEmpty) {
    return stationEnabled && fletchingTable && !sneakPlacing(cancellingInteraction, handsEmpty);
  }

  public static boolean sneakPlacing(
      final boolean cancellingInteraction, final boolean handsEmpty) {
    return cancellingInteraction && !handsEmpty;
  }
}
