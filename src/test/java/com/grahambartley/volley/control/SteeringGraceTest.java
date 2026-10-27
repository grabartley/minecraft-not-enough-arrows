package com.grahambartley.volley.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SteeringGraceTest {
  private final SteeringGrace grace = new SteeringGrace();
  private static final long STEERED_AT = 100L;
  private final UUID slime = UUID.randomUUID();

  @Test
  void aSlimeNobodySteersIsNotSteered() {
    assertFalse(grace.isSteered(slime, STEERED_AT));
  }

  @ParameterizedTest(name = "{0} ticks later steered = {1}")
  @CsvSource({"0, true", "1, true", "2, true", "3, false", "20, false"})
  void aSteeredMobStaysSteeredOnlyThroughTheGrace(final long later, final boolean steered) {
    grace.steer(slime, STEERED_AT);

    assertEquals(steered, grace.isSteered(slime, STEERED_AT + later));
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
