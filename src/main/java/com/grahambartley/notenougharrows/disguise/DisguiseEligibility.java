package com.grahambartley.notenougharrows.disguise;

import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;

public final class DisguiseEligibility {
  public static final Set<EntityType<?>> BOSSES =
      Set.of(EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.WARDEN);

  private DisguiseEligibility() {}

  public static boolean canDisguise(final Entity entity) {
    return entity instanceof MobEntity mob
        && mob.isAlive()
        && entity instanceof Monster
        && !isExempt(entity);
  }

  public static boolean isExempt(final Entity entity) {
    return entity instanceof PlayerEntity
        || entity instanceof MerchantEntity
        || (entity instanceof Tameable tameable && tameable.getOwnerUuid() != null)
        || BOSSES.contains(entity.getType());
  }
}
