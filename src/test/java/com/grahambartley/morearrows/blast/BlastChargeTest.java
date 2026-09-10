package com.grahambartley.morearrows.blast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.explosive.ExplosiveTier;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BlastChargeTest {

  private static final UUID CARRIER = UUID.randomUUID();
  private static final UUID SHOOTER = UUID.randomUUID();

  @ParameterizedTest(name = "{0}")
  @EnumSource(ExplosiveTier.class)
  void keepsTheTierThatDecidesWhatTheBlastDoes(final ExplosiveTier tier) {
    assertEquals(tier, new BlastCharge(CARRIER, tier, SHOOTER).tier());
  }

  @Test
  void keepsTheCarrierAndShooterItWasGiven() {
    final BlastCharge charge = new BlastCharge(CARRIER, ExplosiveTier.TNT, SHOOTER);

    assertEquals(CARRIER, charge.carrierId());
    assertEquals(Optional.of(SHOOTER), charge.shooter());
  }

  @Test
  void reportsNoShooterForAChargeNobodyFired() {
    final BlastCharge charge = new BlastCharge(CARRIER, ExplosiveTier.GUNPOWDER, null);

    assertEquals(Optional.empty(), charge.shooter());
    assertEquals(CARRIER, charge.carrierId());
  }

  @Test
  void refusesAChargeWithNothingToCarryItOrNoTierToSizeIt() {
    assertThrows(
        NullPointerException.class, () -> new BlastCharge(null, ExplosiveTier.TNT, SHOOTER));
    assertThrows(NullPointerException.class, () -> new BlastCharge(CARRIER, null, SHOOTER));
  }

  @Test
  void treatsTwoChargesOnTheSameCarrierWithTheSameTierAsEqual() {
    assertTrue(
        new BlastCharge(CARRIER, ExplosiveTier.TNT, SHOOTER)
            .equals(new BlastCharge(CARRIER, ExplosiveTier.TNT, SHOOTER)));
  }
}
