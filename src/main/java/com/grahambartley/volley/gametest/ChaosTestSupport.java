package com.grahambartley.volley.gametest;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class ChaosTestSupport {
  static final double STURDY_PLAYER_HEALTH = 400.0;

  private static final int PROTECTION_TICKS = 2000;
  private static final double NEARBY = 2.0;

  private ChaosTestSupport() {}

  static ServerPlayerEntity sturdyPlayerAt(final TestContext context, final BlockPos at) {
    final ServerPlayerEntity player = MockPlayerSupport.survivalPlayerAt(context, at);
    player
        .getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
        .setBaseValue(STURDY_PLAYER_HEALTH);
    player.setHealth(player.getMaxHealth());
    player.addStatusEffect(
        new StatusEffectInstance(StatusEffects.WATER_BREATHING, PROTECTION_TICKS));
    return player;
  }

  static int droppedNearby(final TestContext context, final Item item) {
    return context
        .getWorld()
        .getEntitiesByClass(
            ItemEntity.class,
            context.getTestBox().expand(NEARBY),
            drop -> drop.getStack().isOf(item))
        .stream()
        .mapToInt(drop -> drop.getStack().getCount())
        .sum();
  }

  static int countOf(final ServerPlayerEntity player, final Item item) {
    return player.getInventory().count(item);
  }
}
