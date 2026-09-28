package com.grahambartley.notenougharrows.render;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RidingPlayersTest {

  @BeforeEach
  void setUp() {
    RidingPlayers.clear();
  }

  @Test
  void aPlayerToldToBeRidingIsRiding() {
    RidingPlayers.accept(7, true);

    assertTrue(RidingPlayers.isRiding(7));
    assertFalse(RidingPlayers.isRiding(8));
  }

  @Test
  void aPlayerToldTheRideEndedIsNoLongerRiding() {
    RidingPlayers.accept(7, true);

    RidingPlayers.accept(7, false);

    assertFalse(RidingPlayers.isRiding(7));
  }

  @Test
  void anUnloadedPlayerIsForgotten() {
    RidingPlayers.accept(7, true);

    RidingPlayers.forget(7);

    assertFalse(RidingPlayers.isRiding(7));
  }

  @Test
  void leavingTheServerForgetsEveryRider() {
    RidingPlayers.accept(7, true);
    RidingPlayers.accept(9, true);

    RidingPlayers.clear();

    assertFalse(RidingPlayers.isRiding(7));
    assertFalse(RidingPlayers.isRiding(9));
  }
}
