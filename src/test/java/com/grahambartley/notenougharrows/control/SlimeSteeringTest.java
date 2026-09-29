package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SlimeSteeringTest {
  private static final long STEERED_AT = 100L;
  private final UUID slime = UUID.randomUUID();

  @AfterEach
  void forget() {
    SlimeSteering.forgetAll();
  }

  @Test
  void aSlimeNobodySteersIsNotSteered() {
    assertFalse(SlimeSteering.isSteered(slime, STEERED_AT));
  }

  @Test
  void aSteeredSlimeStaysSteeredThroughTheGrace() {
    SlimeSteering.steer(slime, STEERED_AT);
    assertTrue(SlimeSteering.isSteered(slime, STEERED_AT));
    assertTrue(SlimeSteering.isSteered(slime, STEERED_AT + SlimeSteering.STEERING_GRACE_TICKS));
  }

  @Test
  void aSlimeNoLongerSteeredGoesBackToItsOwnWay() {
    SlimeSteering.steer(slime, STEERED_AT);
    assertFalse(
        SlimeSteering.isSteered(slime, STEERED_AT + SlimeSteering.STEERING_GRACE_TICKS + 1));
    assertFalse(SlimeSteering.isSteered(slime, STEERED_AT));
  }

  @Test
  void steeringOneSlimeLeavesTheOthersAlone() {
    SlimeSteering.steer(slime, STEERED_AT);
    assertFalse(SlimeSteering.isSteered(UUID.randomUUID(), STEERED_AT));
  }

  @Test
  void aReleasedSlimeIsNoLongerSteered() {
    SlimeSteering.steer(slime, STEERED_AT);
    SlimeSteering.release(slime);
    assertFalse(SlimeSteering.isSteered(slime, STEERED_AT));
  }

  @Test
  void forgettingEverythingReleasesEverySlime() {
    SlimeSteering.steer(slime, STEERED_AT);
    SlimeSteering.forgetAll();
    assertFalse(SlimeSteering.isSteered(slime, STEERED_AT));
  }
}
