package com.grahambartley.morearrows.countdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CountdownChangesTest {
  private static final UUID CARRIER = UUID.fromString("00000000-0000-0000-0000-00000000000a");
  private static final UUID OTHER = UUID.fromString("00000000-0000-0000-0000-00000000000b");
  private static final int CARRIER_ENTITY_ID = 42;

  private CountdownChanges changes;

  @BeforeEach
  void setUp() {
    changes = new CountdownChanges();
  }

  @Test
  void theFirstSightOfAFuseIsAChange() {
    assertTrue(changes.record(CARRIER, CARRIER_ENTITY_ID, 60));
  }

  @Test
  void aFuseAlreadyAnnouncedIsNotAChangeWhileItBurnsDown() {
    changes.record(CARRIER, CARRIER_ENTITY_ID, 60);

    assertFalse(changes.record(CARRIER, CARRIER_ENTITY_ID, 60));
    assertFalse(changes.record(CARRIER, CARRIER_ENTITY_ID, 60));
  }

  @Test
  void aFuseRelitAtADifferentDelayIsAChange() {
    changes.record(CARRIER, CARRIER_ENTITY_ID, 60);

    assertTrue(changes.record(CARRIER, CARRIER_ENTITY_ID, 40));
  }

  @Test
  void theSameCarrierSeenUnderANewEntityIdIsAChange() {
    changes.record(CARRIER, CARRIER_ENTITY_ID, 60);

    assertTrue(changes.record(CARRIER, CARRIER_ENTITY_ID + 1, 60));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -60})
  void aFuseWithNoDelayIsNeverAnnounced(final int delayTicks) {
    assertFalse(changes.record(CARRIER, CARRIER_ENTITY_ID, delayTicks));
    assertEquals(Set.of(), changes.announced());
  }

  @Test
  void aMissingCarrierIsNeverAnnounced() {
    assertFalse(changes.record(null, CARRIER_ENTITY_ID, 60));
    assertEquals(Set.of(), changes.announced());
  }

  @Test
  void everyAnnouncedCarrierIsListed() {
    changes.record(CARRIER, CARRIER_ENTITY_ID, 60);
    changes.record(OTHER, CARRIER_ENTITY_ID + 1, 40);

    assertEquals(Set.of(CARRIER, OTHER), changes.announced());
  }

  @Test
  void forgettingACarrierReturnsWhatWasAnnouncedForIt() {
    changes.record(CARRIER, CARRIER_ENTITY_ID, 60);

    assertEquals(new CountdownChanges.Announced(CARRIER_ENTITY_ID, 60), changes.forget(CARRIER));
    assertFalse(changes.knows(CARRIER));
  }

  @Test
  void forgettingACarrierThatWasNeverAnnouncedReturnsNothing() {
    assertNull(changes.forget(CARRIER));
    assertNull(changes.forget(null));
  }

  @Test
  void aForgottenFuseIsAnnouncedAgainWhenItComesBack() {
    changes.record(CARRIER, CARRIER_ENTITY_ID, 60);
    changes.forget(CARRIER);

    assertTrue(changes.record(CARRIER, CARRIER_ENTITY_ID, 60));
  }
}
