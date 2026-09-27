package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.SmokeArrowConfig;
import com.grahambartley.notenougharrows.control.SmokeCloudService;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class SmokeArrowMobsGameTest implements FabricGameTest {

  @CustomTestProvider
  public Collection<TestFunction> blindsEveryMobInTheCloud() {
    return MobArena.perMob(
        "smoke", "blinds", mob -> true, MobArena.SHORT_LIMIT, SmokeArrowMobsGameTest::blinds);
  }

  private static void blinds(final TestContext context, final Mob mob) {
    final MobEntity target = MobArena.still(context, mob, MobArena.NEAR_STAND);
    context.runAtTick(
        MobArena.SETTLED,
        () ->
            SmokeCloudService.open(
                context.getWorld(),
                target.getBoundingBox().getCenter(),
                SmokeArrowConfig.defaults()));
    context.runAtTick(
        MobArena.SETTLED + 30,
        () -> {
          if (MobRoster.refusesEveryStatusEffect(mob)) {
            MobArena.check(
                context,
                !target.hasStatusEffect(StatusEffects.BLINDNESS),
                "Vanilla makes a " + mob.name() + " immune to every status effect, and so is this");
          } else {
            MobArena.check(
                context,
                target.hasStatusEffect(StatusEffects.BLINDNESS),
                "A smoke cloud should blind a " + mob.name() + " standing in it");
          }
          context.complete();
        });
  }
}
