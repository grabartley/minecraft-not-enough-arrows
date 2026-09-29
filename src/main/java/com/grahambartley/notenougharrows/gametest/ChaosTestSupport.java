package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ChaosArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.function.UnaryOperator;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

final class ChaosTestSupport {
  static final double STURDY_PLAYER_HEALTH = 400.0;

  private static final int PROTECTION_TICKS = 2000;
  private static final double NEARBY = 2.0;

  private ChaosTestSupport() {}

  static MobEntity fighting(final TestContext context, final Mob mob, final BlockPos at) {
    final MobEntity spawned =
        (MobEntity) context.spawnEntity(mob.type(), MobArena.standFor(mob, at));
    spawned.initialize(
        context.getWorld(),
        context.getWorld().getLocalDifficulty(context.getAbsolutePos(at)),
        SpawnReason.MOB_SUMMONED,
        null);
    spawned.setBaby(false);
    spawned.setPersistent();
    if (spawned instanceof AbstractPiglinEntity piglin) {
      piglin.setImmuneToZombification(true);
    }
    if (spawned instanceof HoglinEntity hoglin) {
      hoglin.setImmuneToZombification(true);
    }
    return shaded(spawned);
  }

  static <E extends MobEntity> E shaded(final E mob) {
    if (mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
      mob.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }
    return mob;
  }

  static void useChaos(final UnaryOperator<ChaosArrowConfig> change) {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();
    ServerConfigHolder.set(defaults.withChaos(change.apply(defaults.chaos())));
  }

  static ServerPlayerEntity survivalPlayerAt(final TestContext context, final BlockPos at) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, at);
    player.changeGameMode(GameMode.SURVIVAL);
    return player;
  }

  static ServerPlayerEntity sturdyPlayerAt(final TestContext context, final BlockPos at) {
    final ServerPlayerEntity player = survivalPlayerAt(context, at);
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
