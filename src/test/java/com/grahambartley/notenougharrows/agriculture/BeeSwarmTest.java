package com.grahambartley.notenougharrows.agriculture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BeeSwarmTest {
  private static final UUID SHOOTER = UUID.nameUUIDFromBytes("shooter".getBytes());
  private static final UUID TARGET = UUID.nameUUIDFromBytes("target".getBytes());

  @Test
  void livesUntilItsExpiryTick() {
    final BeeSwarm swarm = new BeeSwarm(100L, Optional.of(SHOOTER), Optional.of(TARGET));

    assertFalse(swarm.hasExpired(99L));
    assertTrue(swarm.hasExpired(100L));
    assertTrue(swarm.hasExpired(101L));
  }

  @Test
  void sparesItsShooterAndNobodyElse() {
    final BeeSwarm swarm = new BeeSwarm(100L, Optional.of(SHOOTER), Optional.of(TARGET));

    assertTrue(swarm.spares(SHOOTER));
    assertFalse(swarm.spares(TARGET));
    assertFalse(swarm.spares(null));
  }

  @Test
  void aSwarmWithNoShooterSparesNobody() {
    final BeeSwarm swarm = new BeeSwarm(100L, Optional.empty(), Optional.of(TARGET));

    assertFalse(swarm.spares(SHOOTER));
    assertFalse(swarm.spares(TARGET));
  }

  @Test
  void neverTakesItsShooterAsItsTarget() {
    assertEquals(
        Optional.empty(),
        new BeeSwarm(100L, Optional.of(SHOOTER), Optional.of(SHOOTER)).targetId());
  }

  @Test
  void refusesAMissingShooterOrTargetOptional() {
    assertThrows(NullPointerException.class, () -> new BeeSwarm(1L, null, Optional.empty()));
    assertThrows(NullPointerException.class, () -> new BeeSwarm(1L, Optional.empty(), null));
  }

  @Test
  void survivesASaveAndLoad() {
    final BeeSwarm swarm = new BeeSwarm(1234L, Optional.of(SHOOTER), Optional.of(TARGET));

    final JsonElement saved = BeeSwarm.CODEC.encodeStart(JsonOps.INSTANCE, swarm).getOrThrow();

    assertEquals(swarm, BeeSwarm.CODEC.parse(JsonOps.INSTANCE, saved).getOrThrow());
  }

  @Test
  void survivesASaveAndLoadWithNoShooterOrTarget() {
    final BeeSwarm swarm = new BeeSwarm(5L, Optional.empty(), Optional.empty());

    final JsonElement saved = BeeSwarm.CODEC.encodeStart(JsonOps.INSTANCE, swarm).getOrThrow();

    assertEquals(swarm, BeeSwarm.CODEC.parse(JsonOps.INSTANCE, saved).getOrThrow());
  }
}
