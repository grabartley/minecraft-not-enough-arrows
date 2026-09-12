package com.grahambartley.notenougharrows.blast;

import com.grahambartley.notenougharrows.explosive.ExplosiveTier;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public record BlastCharge(UUID carrierId, ExplosiveTier tier, @Nullable UUID shooterId) {

  public BlastCharge {
    Objects.requireNonNull(carrierId, "A blast charge needs the entity that carries it");
    Objects.requireNonNull(tier, "A blast charge needs the tier that decides its power");
  }

  public Optional<UUID> shooter() {
    return Optional.ofNullable(shooterId);
  }
}
