package com.grahambartley.volley.gametest;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class ChaosTestSupport {
  static final double STURDY_PLAYER_HEALTH = 400.0;

  private static final int PROTECTION_TICKS = 2000;

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
}
