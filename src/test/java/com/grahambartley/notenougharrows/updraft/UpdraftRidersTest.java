package com.grahambartley.notenougharrows.updraft;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdraftRidersTest {
  private static final UUID RIDER = UUID.randomUUID();
  private static final UUID OTHER = UUID.randomUUID();

  private UpdraftRiders riders;

  @BeforeEach
  void setUp() {
    riders = new UpdraftRiders();
  }

  @Test
  void anythingNewMayBeLifted() {
    assertTrue(riders.mayLift(RIDER));
    assertFalse(riders.mayLift(null));
  }

  @Test
  void aRiderStillInsideIsStillCarried() {
    riders.carry(RIDER);

    riders.settle(List.of(RIDER));

    assertTrue(riders.isCarrying(RIDER));
    assertTrue(riders.mayLift(RIDER));
  }

  @Test
  void aRiderThatLeavesIsNeverLiftedByThatColumnAgain() {
    riders.carry(RIDER);

    riders.settle(List.of());

    assertFalse(riders.isCarrying(RIDER));
    assertTrue(riders.hasLeft(RIDER));
    assertFalse(riders.mayLift(RIDER));
  }

  @Test
  void aRiderThatLeftCannotBeCarriedAgain() {
    riders.carry(RIDER);
    riders.settle(List.of());

    riders.carry(RIDER);

    assertFalse(riders.isCarrying(RIDER));
  }

  @Test
  void somethingThatWasNeverCarriedHasNotLeft() {
    riders.carry(RIDER);

    riders.settle(List.of(RIDER));

    assertFalse(riders.hasLeft(OTHER));
    assertTrue(riders.mayLift(OTHER));
  }
}
