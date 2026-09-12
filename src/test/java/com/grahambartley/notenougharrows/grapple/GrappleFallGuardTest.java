package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class GrappleFallGuardTest {
  private static final UUID FIRST_PLAYER = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
  private static final UUID SECOND_PLAYER = UUID.fromString("00000000-0000-0000-0000-0000000000a2");

  @BeforeEach
  @AfterEach
  void forgetEverySparedPlayer() {
    GrappleFallGuard.forget();
  }

  @Test
  void nobodyIsSparedUntilAGrappleAsksForIt() {
    assertFalse(GrappleFallGuard.spares(FIRST_PLAYER));
  }

  @Test
  void aSparedPlayerIsSpared() {
    GrappleFallGuard.spare(FIRST_PLAYER);

    assertTrue(GrappleFallGuard.spares(FIRST_PLAYER));
  }

  @Test
  void sparingOnePlayerSparesOnlyThatPlayer() {
    GrappleFallGuard.spare(FIRST_PLAYER);

    assertFalse(GrappleFallGuard.spares(SECOND_PLAYER));
  }

  @Test
  void sparingTheSamePlayerTwiceStillTakesOneRelease() {
    GrappleFallGuard.spare(FIRST_PLAYER);
    GrappleFallGuard.spare(FIRST_PLAYER);

    GrappleFallGuard.release(FIRST_PLAYER);

    assertFalse(GrappleFallGuard.spares(FIRST_PLAYER));
  }

  @Test
  void releasingAPlayerNobodySparedChangesNothing() {
    GrappleFallGuard.spare(FIRST_PLAYER);

    GrappleFallGuard.release(SECOND_PLAYER);

    assertTrue(GrappleFallGuard.spares(FIRST_PLAYER));
  }

  @Test
  void forgettingEverythingSparesNobody() {
    GrappleFallGuard.spare(FIRST_PLAYER);
    GrappleFallGuard.spare(SECOND_PLAYER);

    GrappleFallGuard.forget();

    assertFalse(GrappleFallGuard.spares(FIRST_PLAYER));
    assertFalse(GrappleFallGuard.spares(SECOND_PLAYER));
  }

  @ParameterizedTest
  @NullSource
  void aPlayerWithNoIdentityIsNeverSpared(final UUID playerId) {
    GrappleFallGuard.spare(playerId);

    assertFalse(GrappleFallGuard.spares(playerId));
  }

  @Test
  void aFallWithNoSourceBehindItIsNotAFallAGrappleOwns() {
    assertFalse(GrappleFallGuard.isTheFallAGrappleOwns(null));
  }
}
