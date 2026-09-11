package com.grahambartley.morearrows.fletching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingStationGateTest {

  @ParameterizedTest(
      name = "enabled={0} table={1} cancelling={2} handsEmpty={3} opens the station: {4}")
  @CsvSource({
    "true,  true,  false, true,  true",
    "true,  true,  false, false, true",
    "true,  true,  true,  true,  true",
    "true,  true,  true,  false, false",
    "false, true,  false, true,  false",
    "false, true,  true,  false, false",
    "true,  false, false, true,  false",
    "true,  false, true,  false, false",
    "false, false, false, true,  false"
  })
  void opensStationOnlyForAnEnabledStationOnATableThatIsNotBeingSneakPlacedAgainst(
      final boolean stationEnabled,
      final boolean fletchingTable,
      final boolean cancellingInteraction,
      final boolean handsEmpty,
      final boolean expected) {
    assertEquals(
        expected,
        FletchingStationGate.opensStation(
            stationEnabled, fletchingTable, cancellingInteraction, handsEmpty));
  }

  @ParameterizedTest(name = "cancelling={0} handsEmpty={1} is a sneak place: {2}")
  @CsvSource({
    "true,  false, true",
    "true,  true,  false",
    "false, false, false",
    "false, true,  false"
  })
  void sneakPlacingNeedsBothASneakAndSomethingInHand(
      final boolean cancellingInteraction, final boolean handsEmpty, final boolean expected) {
    assertEquals(expected, FletchingStationGate.sneakPlacing(cancellingInteraction, handsEmpty));
  }
}
