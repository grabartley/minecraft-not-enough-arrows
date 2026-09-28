package com.grahambartley.notenougharrows.agriculture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.Uuids;

public record BeeSwarm(long expiryTick, Optional<UUID> shooterId, Optional<UUID> targetId) {

  public static final Codec<BeeSwarm> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.LONG.fieldOf("expires").forGetter(BeeSwarm::expiryTick),
                      Uuids.CODEC.optionalFieldOf("shooter").forGetter(BeeSwarm::shooterId),
                      Uuids.CODEC.optionalFieldOf("target").forGetter(BeeSwarm::targetId))
                  .apply(instance, BeeSwarm::new));

  public BeeSwarm {
    Objects.requireNonNull(shooterId, "shooterId");
    Objects.requireNonNull(targetId, "targetId");
    if (shooterId.isPresent() && shooterId.equals(targetId)) {
      targetId = Optional.empty();
    }
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean spares(final UUID entityId) {
    return entityId != null && shooterId.map(entityId::equals).orElse(false);
  }
}
