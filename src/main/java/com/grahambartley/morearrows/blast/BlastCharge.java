package com.grahambartley.morearrows.blast;

import com.grahambartley.morearrows.explosive.ExplosiveTier;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public record BlastCharge(UUID carrierId, ExplosiveTier tier, @Nullable UUID shooterId) {

  public BlastCharge {
    Objects.requireNonNull(carrierId, "A blast charge needs the entity that carries it");
    Objects.requireNonNull(tier, "A blast charge needs the tier that decides its power");
  }

  public static BlastCharge on(final UUID carrierId, final ExplosiveTier tier) {
    return new BlastCharge(carrierId, tier, null);
  }

  public Optional<UUID> shooter() {
    return Optional.ofNullable(shooterId);
  }
}
