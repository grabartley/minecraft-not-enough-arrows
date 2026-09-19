package com.grahambartley.notenougharrows.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class LifestealHealTest {
  private static final float FULL_HEALTH = 20.0f;

  @ParameterizedTest
  @CsvSource({"4.0, 0.5, 10.0, 2.0", "10.0, 0.25, 10.0, 2.5", "6.0, 1.0, 10.0, 6.0"})
  void returnsTheConfiguredShareOfTheDamageDealt(
      final double dealt, final float share, final float cap, final float expected) {
    assertEquals(expected, LifestealHeal.amount(dealt, share, cap, 1.0f, FULL_HEALTH));
  }

  @Test
  void neverReturnsMoreThanTheCapForOneHit() {
    assertEquals(3.0f, LifestealHeal.amount(100.0, 1.0f, 3.0f, 1.0f, FULL_HEALTH));
  }

  @Test
  void neverHealsPastTheShootersOwnMaximum() {
    assertEquals(2.0f, LifestealHeal.amount(100.0, 1.0f, 50.0f, 18.0f, FULL_HEALTH));
  }

  @Test
  void healsNothingWhenTheShooterIsAlreadyFull() {
    assertEquals(0.0f, LifestealHeal.amount(100.0, 1.0f, 50.0f, FULL_HEALTH, FULL_HEALTH));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -1.0, -100.0})
  void aHitThatDealtNoDamageHealsNothing(final double dealt) {
    assertEquals(0.0f, LifestealHeal.amount(dealt, 1.0f, 10.0f, 1.0f, FULL_HEALTH));
  }

  @ParameterizedTest
  @CsvSource({"0.0, 10.0", "-1.0, 10.0"})
  void aShareOfNothingHealsNothing(final float share, final float cap) {
    assertEquals(0.0f, LifestealHeal.amount(20.0, share, cap, 1.0f, FULL_HEALTH));
  }

  @Test
  void aCapOfNothingHealsNothing() {
    assertEquals(0.0f, LifestealHeal.amount(20.0, 1.0f, 0.0f, 1.0f, FULL_HEALTH));
  }

  @Test
  void aShooterOnOneHeartCanStillBeToppedRightUp() {
    assertEquals(FULL_HEALTH - 2.0f, LifestealHeal.amount(100.0, 1.0f, 100.0f, 2.0f, FULL_HEALTH));
  }
}
