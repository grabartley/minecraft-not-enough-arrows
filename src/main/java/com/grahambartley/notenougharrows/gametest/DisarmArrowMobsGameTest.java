package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.DisarmDrop;
import com.grahambartley.notenougharrows.control.DisarmFetchService;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class DisarmArrowMobsGameTest implements FabricGameTest {
  private static final double A_REAL_THROW = 5.0;

  @CustomTestProvider
  public Collection<TestFunction> everyArmedMobFetchesItsWeaponBack() {
    return MobArena.perMob(
        "disarm",
        "fetches",
        Mob::holdsWeapon,
        MobArena.LONG_LIMIT,
        DisarmArrowMobsGameTest::fetches);
  }

  private static void fetches(final TestContext context, final Mob mob) {
    final ServerPlayerEntity player = MobArena.player(context);
    final MobEntity target = MobArena.thinking(context, mob, MobArena.NEAR_STAND);
    final Item weapon = MobArena.weaponFor(mob);
    target.equipStack(EquipmentSlot.MAINHAND, new ItemStack(weapon));
    context.runAtTick(
        MobArena.SETTLED,
        () ->
            MobArena.check(
                context,
                DisarmDrop.disarm(context.getWorld(), target, player.getPos(), true, A_REAL_THROW),
                "A " + mob.name() + " should be disarmed"));
    context.runAtTick(
        MobArena.LONG_LIMIT - 10,
        () -> {
          MobArena.check(
              context,
              target.getMainHandStack().isOf(weapon),
              "A disarmed "
                  + mob.name()
                  + " should fetch its "
                  + weapon
                  + ", but holds "
                  + target.getMainHandStack()
                  + ", still fetching "
                  + DisarmFetchService.isFetching(context.getWorld(), target)
                  + ", loose items "
                  + looseItems(context)
                  + ", standing at "
                  + context.getRelative(target.getPos()));
          context.complete();
        });
  }

  private static List<String> looseItems(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(ItemEntity.class, context.getTestBox().expand(8.0), item -> true)
        .stream()
        .map(item -> item.getStack() + " at " + context.getRelative(item.getPos()))
        .toList();
  }
}
