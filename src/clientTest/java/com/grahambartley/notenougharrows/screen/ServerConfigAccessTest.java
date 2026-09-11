package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServerConfigAccessTest {

  @ParameterizedTest(name = "inWorld={0} singleplayer={1} operator={2} gives {3}")
  @CsvSource({
    "false, false, false, NOT_IN_WORLD",
    "false, true,  true,  NOT_IN_WORLD",
    "true,  true,  false, EDITABLE",
    "true,  false, true,  EDITABLE",
    "true,  true,  true,  EDITABLE",
    "true,  false, false, NOT_OPERATOR",
  })
  void resolvesAccessFromTheSessionTheScreenOpenedIn(
      final boolean inWorld,
      final boolean singleplayer,
      final boolean operator,
      final ServerConfigAccess expected) {
    assertEquals(expected, ServerConfigAccess.of(inWorld, singleplayer, operator));
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
