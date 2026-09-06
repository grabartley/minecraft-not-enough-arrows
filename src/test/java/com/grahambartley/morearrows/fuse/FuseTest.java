package com.grahambartley.morearrows.fuse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FuseTest {
  private static final UUID HOST = UUID.fromString("00000000-0000-0000-0000-0000000000aa");
  private static final int DELAY_TICKS = 60;

  @Test
  void aFuseWithoutAHostIsRefused() {
    assertThrows(NullPointerException.class, () -> Fuse.lit(null, DELAY_TICKS));
  }

  @Test
  void aFreshlyLitFuseHasItsWholeDelayLeftToBurn() {
    final Fuse fuse = Fuse.lit(HOST, DELAY_TICKS);

    assertEquals(HOST, fuse.hostId());
    assertEquals(DELAY_TICKS, fuse.delayTicks());
    assertEquals(DELAY_TICKS, fuse.remainingTicks());
    assertEquals(0, fuse.lostTicks());
    assertFalse(fuse.hasExpired());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -60})
  void aNegativeDelayIsTreatedAsNoDelayAtAll(final int delayTicks) {
    assertTrue(Fuse.lit(HOST, delayTicks).isInstant());
  }

  @Test
  void aFuseWithNoDelayIsAlreadyExpiredWhenItIsLit() {
    final Fuse fuse = Fuse.lit(HOST, 0);

    assertTrue(fuse.isInstant());
    assertTrue(fuse.hasExpired());
  }

  @Test
  void aFuseWithADelayIsNotInstant() {
    assertFalse(Fuse.lit(HOST, DELAY_TICKS).isInstant());
  }

  @ParameterizedTest
  @CsvSource({"61, 60", "1000, 60", "-5, 0"})
  void aFuseCanNeverHaveMoreLeftToBurnThanItsDelay(final int remainingTicks, final int delayTicks) {
    final Fuse fuse = new Fuse(HOST, delayTicks, remainingTicks, 0);

    assertTrue(fuse.remainingTicks() >= 0);
    assertTrue(fuse.remainingTicks() <= fuse.delayTicks());
  }

  @Test
  void burningAFuseTakesOneTickOffWhatIsLeft() {
    assertEquals(DELAY_TICKS - 1, Fuse.lit(HOST, DELAY_TICKS).burned().remainingTicks());
  }

  @Test
  void burningAFuseAllTheWayDownExpiresIt() {
    Fuse fuse = Fuse.lit(HOST, 3);
    for (int tick = 0; tick < 3; tick++) {
      assertFalse(fuse.hasExpired());
      fuse = fuse.burned();
    }

    assertTrue(fuse.hasExpired());
  }

  @Test
  void anExpiredFuseCannotBurnPastZero() {
    assertEquals(0, new Fuse(HOST, DELAY_TICKS, 0, 0).burned().remainingTicks());
  }

  @Test
  void aFuseThatBurnsAgainHasFoundItsHostAgain() {
    assertEquals(0, Fuse.lit(HOST, DELAY_TICKS).lost().lost().burned().lostTicks());
  }

  @Test
  void aFuseWithoutItsHostHoldsWhatIsLeftToBurn() {
    final Fuse held = Fuse.lit(HOST, DELAY_TICKS).lost();

    assertEquals(DELAY_TICKS, held.remainingTicks());
    assertEquals(1, held.lostTicks());
  }

  @Test
  void aFuseIsOnlyAbandonedOnceItsHostHasBeenGoneForTheWholeGracePeriod() {
    assertFalse(new Fuse(HOST, DELAY_TICKS, 10, Fuse.LOST_HOST_GRACE_TICKS - 1).isAbandoned());
    assertTrue(new Fuse(HOST, DELAY_TICKS, 10, Fuse.LOST_HOST_GRACE_TICKS).isAbandoned());
  }

  @Test
  void aFuseBeepsOnTheCadenceItsDelayScheduled() {
    for (final int beep : FuseCadence.beepTicks(DELAY_TICKS)) {
      assertTrue(new Fuse(HOST, DELAY_TICKS, beep, 0).beepsNow(), "Fuse should beep at " + beep);
    }
  }

  @Test
  void aFuseStaysQuietBetweenTheBeepsItsDelayScheduled() {
    for (int remaining = 0; remaining <= DELAY_TICKS; remaining++) {
      if (!FuseCadence.beepTicks(DELAY_TICKS).contains(remaining)) {
        assertFalse(
            new Fuse(HOST, DELAY_TICKS, remaining, 0).beepsNow(),
            "Fuse should stay quiet at " + remaining);
      }
    }
  }
}
