package com.grahambartley.morearrows.fletching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingStationGateTest {

  @ParameterizedTest(
      name =
          "enabled={0} table={1} spectator={2} cancelling={3} handsEmpty={4} opens the station: {5}")
  @CsvSource({
    "true,  true,  false, false, true,  true",
    "true,  true,  false, false, false, true",
    "true,  true,  false, true,  true,  true",
    "true,  true,  false, true,  false, false",
    "true,  true,  true,  false, true,  false",
    "true,  true,  true,  true,  false, false",
    "false, true,  false, false, true,  false",
    "false, true,  false, true,  false, false",
    "true,  false, false, false, true,  false",
    "true,  false, false, true,  false, false",
    "false, false, true,  false, true,  false"
  })
  void opensOnlyForAnEnabledStationOnATableForSomeoneWhoIsNeitherSpectatingNorPlacing(
      final boolean stationEnabled,
      final boolean fletchingTable,
      final boolean spectator,
      final boolean cancellingInteraction,
      final boolean handsEmpty,
      final boolean expected) {
    assertEquals(
        expected,
        FletchingStationGate.opensStation(
            stationEnabled, fletchingTable, spectator, cancellingInteraction, handsEmpty));
  }
}
