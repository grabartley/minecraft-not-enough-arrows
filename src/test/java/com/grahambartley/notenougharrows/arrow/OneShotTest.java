package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OneShotTest {

  @Test
  void theFirstClaimGoesThrough() {
    assertTrue(new OneShot().claim());
  }

  @Test
  void everyLaterClaimIsRefused() {
    final OneShot shot = new OneShot();
    shot.claim();

    assertFalse(shot.claim());
    assertFalse(shot.claim());
  }

  @Test
  void eachShotIsItsOwn() {
    final OneShot first = new OneShot();
    first.claim();

    assertTrue(new OneShot().claim());
  }
}
