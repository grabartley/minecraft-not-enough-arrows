package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.config.SocialArrowConfig;
import com.grahambartley.notenougharrows.entity.CourierArrowEntity;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Box;

final class SocialTestSupport {
  static final Item PAYLOAD_ITEM = Items.DIAMOND;
  static final int PAYLOAD_COUNT = 40;
  static final int SETTLED_TICK = 30;
  static final int TICK_LIMIT = 60;

  private static final double NEARBY = 4.0;

  private SocialTestSupport() {}

  static void useSocial(final UnaryOperator<SocialArrowConfig> change) {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();
    ServerConfigHolder.set(defaults.withSocial(change.apply(defaults.social())));
  }

  static ItemStack emptyCourier() {
    return new ItemStack(SocialArrows.COURIER_ARROW.item());
  }

  static ItemStack loadedCourier() {
    return loadedCourier(new ItemStack(PAYLOAD_ITEM, PAYLOAD_COUNT));
  }

  static ItemStack loadedCourier(final ItemStack payload) {
    return CourierPayloads.loaded(emptyCourier(), payload);
  }

  static ServerPlayerEntity fireEast(final TestContext context, final ItemStack quiver) {
    final ServerPlayerEntity shooter =
        ChaosTestSupport.survivalPlayerAt(context, FiringRangeSupport.SHOOTER_STAND);
    shooter.getInventory().clear();
    MockPlayerSupport.fireEastFromBow(context, shooter, quiver);
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);
    return shooter;
  }

  static int accountedFor(
      final TestContext context, final Item item, final List<? extends PlayerEntity> players) {
    final Box around = context.getTestBox().expand(NEARBY);
    int total = 0;
    for (final ItemEntity drop :
        context.getWorld().getEntitiesByClass(ItemEntity.class, around, ItemEntity::isAlive)) {
      total += countIn(drop.getStack(), item);
    }
    for (final CourierArrowEntity arrow :
        context
            .getWorld()
            .getEntitiesByClass(CourierArrowEntity.class, around, CourierArrowEntity::isAlive)) {
      total += countIn(arrow.getItemStack(), item);
    }
    for (final PlayerEntity player : players) {
      for (int slot = 0; slot < player.getInventory().size(); slot++) {
        total += countIn(player.getInventory().getStack(slot), item);
      }
    }
    return total;
  }

  static int countIn(final ItemStack stack, final Item item) {
    if (stack.isOf(item)) {
      return stack.getCount();
    }
    return CourierPayloads.payloadOf(stack)
        .filter(payload -> payload.isOf(item))
        .map(payload -> payload.getCount() * stack.getCount())
        .orElse(0);
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
}
