package com.grahambartley.notenougharrows.fletching;

public final class FletchingStationGate {
  private FletchingStationGate() {}

  public static boolean opensStation(
      final boolean stationEnabled,
      final boolean fletchingTable,
      final boolean spectator,
      final boolean cancellingInteraction,
      final boolean handsEmpty) {
    return stationEnabled
        && fletchingTable
        && !spectator
        && !sneakPlacing(cancellingInteraction, handsEmpty);
  }

  private static boolean sneakPlacing(
      final boolean cancellingInteraction, final boolean handsEmpty) {
    return cancellingInteraction && !handsEmpty;
  }
}
