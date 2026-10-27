package com.grahambartley.volley.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SteeringGraceTest {
  private final SteeringGrace grace = new SteeringGrace();
  private static final long STEERED_AT = 100L;
  private final UUID slime = UUID.randomUUID();

  @Test
  void aSlimeNobodySteersIsNotSteered() {
    assertFalse(grace.isSteered(slime, STEERED_AT));
  }

  @Test
  void aSteeredSlimeStaysSteeredThroughTheGrace() {
    grace.steer(slime, STEERED_AT);
    assertTrue(grace.isSteered(slime, STEERED_AT));
    assertTrue(grace.isSteered(slime, STEERED_AT + SteeringGrace.STEERING_GRACE_TICKS));
  }

  @Test
  void aSlimeNoLongerSteeredGoesBackToItsOwnWay() {
    grace.steer(slime, STEERED_AT);
    assertFalse(grace.isSteered(slime, STEERED_AT + SteeringGrace.STEERING_GRACE_TICKS + 1));
    assertFalse(grace.isSteered(slime, STEERED_AT));
  }

  @Test
  void steeringOneSlimeLeavesTheOthersAlone() {
    grace.steer(slime, STEERED_AT);
    assertFalse(grace.isSteered(UUID.randomUUID(), STEERED_AT));
  }

  @Test
  void aReleasedSlimeIsNoLongerSteered() {
    grace.steer(slime, STEERED_AT);
    grace.release(slime);
    assertFalse(grace.isSteered(slime, STEERED_AT));
  }

  @Test
  void forgettingEverythingReleasesEverySlime() {
    grace.steer(slime, STEERED_AT);
    grace.forgetAll();
    assertFalse(grace.isSteered(slime, STEERED_AT));
  }
}
