package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExpiringRevealsTest {

  @Test
  void keepsARevealForItsLifetimeAndNoLonger() {
    final ExpiringReveals<String> reveals = new ExpiringReveals<>(4);
    reveals.accept("ore", 3);

    reveals.tick();
    reveals.tick();
    assertEquals(List.of("ore"), reveals.live());

    reveals.tick();
    assertTrue(reveals.live().isEmpty());
  }

  @Test
  void aZeroLifetimeRevealsNothing() {
    final ExpiringReveals<String> reveals = new ExpiringReveals<>(4);
    reveals.accept("ore", 0);

    assertTrue(reveals.live().isEmpty());
  }

  @Test
  void dropsTheOldestWhenFull() {
    final ExpiringReveals<String> reveals = new ExpiringReveals<>(2);
    reveals.accept("first", 100);
    reveals.accept("second", 100);
    reveals.accept("third", 100);

    assertEquals(List.of("second", "third"), reveals.live());
  }

  @Test
  void eachRevealCountsDownOnItsOwn() {
    final ExpiringReveals<String> reveals = new ExpiringReveals<>(4);
    reveals.accept("short", 1);
    reveals.accept("long", 5);

    reveals.tick();

    assertEquals(List.of("long"), reveals.live());
  }

  @Test
  void clears() {
    final ExpiringReveals<String> reveals = new ExpiringReveals<>(4);
    reveals.accept("ore", 10);

    reveals.clear();

    assertTrue(reveals.live().isEmpty());
  }

  @Test
  void refusesANonPositiveCapacity() {
    assertThrows(IllegalArgumentException.class, () -> new ExpiringReveals<String>(0));
  }
}
