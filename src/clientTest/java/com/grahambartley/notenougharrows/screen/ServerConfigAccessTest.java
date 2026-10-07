package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServerConfigAccessTest {

  @ParameterizedTest(name = "inWorld={0} operator={1} gives {2}")
  @CsvSource({
    "false, false, NOT_IN_WORLD",
    "false, true,  NOT_IN_WORLD",
    "true,  true,  EDITABLE",
    "true,  false, NOT_OPERATOR",
  })
  void resolvesAccessFromTheSessionTheScreenOpenedIn(
      final boolean inWorld, final boolean operator, final ServerConfigAccess expected) {
    assertEquals(expected, ServerConfigAccess.of(inWorld, operator));
  }

  @Test
  void keepsServerSettingsReadOnlyForASingleplayerHostWithCheatsOff() {
    final boolean hostOperatorWithCheatsOff = false;
    assertEquals(
        ServerConfigAccess.NOT_OPERATOR, ServerConfigAccess.of(true, hostOperatorWithCheatsOff));
  }

  @Test
  void onlyLetsTheEditableStateChangeServerSettings() {
    assertTrue(ServerConfigAccess.EDITABLE.editable());
    assertFalse(ServerConfigAccess.NOT_OPERATOR.editable());
    assertFalse(ServerConfigAccess.NOT_IN_WORLD.editable());
  }

  @Test
  void explainsEveryReadOnlyState() {
    assertEquals(Optional.empty(), ServerConfigAccess.EDITABLE.messageKey());
    assertEquals(
        Optional.of("config.not-enough-arrows.access.not_operator"),
        ServerConfigAccess.NOT_OPERATOR.messageKey());
    assertEquals(
        Optional.of("config.not-enough-arrows.access.not_in_world"),
        ServerConfigAccess.NOT_IN_WORLD.messageKey());
  }

  @Test
  void treatsAMissingClientAsBeingOutsideAWorld() {
    assertEquals(ServerConfigAccess.NOT_IN_WORLD, ServerConfigAccess.of(null));
  }
}
