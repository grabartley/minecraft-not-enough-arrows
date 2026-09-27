package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.combat.StatusArrowImpact;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class LevitationArrowMobsGameTest implements FabricGameTest {

  @CustomTestProvider
  public Collection<TestFunction> liftsEveryMob() {
    return MobArena.perMob(
        "levitation",
        "lifts",
        mob -> true,
        MobArena.SHORT_LIMIT,
        LevitationArrowMobsGameTest::lifts);
  }

  private static void lifts(final TestContext context, final Mob mob) {
    final MobEntity target = MobArena.still(context, mob, MobArena.NEAR_STAND);
    context.runAtTick(
        MobArena.SETTLED,
        () ->
            StatusArrowImpact.apply(target, StatusEffects.LEVITATION, MobArena.SHORT_LIMIT, null));
    context.runAtTick(
        MobArena.SETTLED + 5,
        () -> {
          if (MobRoster.refusesEveryStatusEffect(mob)) {
            MobArena.check(
                context,
                !target.hasStatusEffect(StatusEffects.LEVITATION),
                "Vanilla makes a " + mob.name() + " immune to every status effect, and so is this");
          } else {
            MobArena.check(
                context,
                target.hasStatusEffect(StatusEffects.LEVITATION),
                "A levitation arrow should lift a " + mob.name());
          }
          context.complete();
        });
  }
}
