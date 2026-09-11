package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import net.minecraft.server.network.ServerPlayerEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class GrappleFlightCheckTest {

  @ParameterizedTest
  @NullSource
  void aMissingPlayerIsLeftAlone(final ServerPlayerEntity player) {
    assertDoesNotThrow(() -> GrappleFlightCheck.clearFloatingCountFor(player));
  }
}
