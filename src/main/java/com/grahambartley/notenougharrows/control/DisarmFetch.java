package com.grahambartley.notenougharrows.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.Uuids;

public record DisarmFetch(UUID mobId, UUID itemId, float mainHandDropChance, long expiryTick) {
  public static final int OWNER_SETTLE_TICKS = 20;

  public static final Codec<DisarmFetch> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Uuids.CODEC.fieldOf("mob").forGetter(DisarmFetch::mobId),
                      Uuids.CODEC.fieldOf("item").forGetter(DisarmFetch::itemId),
                      Codec.FLOAT.fieldOf("dropChance").forGetter(DisarmFetch::mainHandDropChance),
                      Codec.LONG.fieldOf("expires").forGetter(DisarmFetch::expiryTick))
                  .apply(instance, DisarmFetch::new));

  public DisarmFetch {
    Objects.requireNonNull(mobId, "mobId");
    Objects.requireNonNull(itemId, "itemId");
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public static boolean canGrab(final boolean withinPickupRange, final int itemAge) {
    return withinPickupRange && itemAge >= OWNER_SETTLE_TICKS;
  }
}
