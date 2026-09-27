package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.FrostArrowConfig;
import com.grahambartley.notenougharrows.control.FrostGripService;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import java.util.Set;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class FrostArrowMobsGameTest implements FabricGameTest {
  private static final Set<EntityType<?>> VANILLA_FREEZE_IMMUNE =
      Set.of(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);

  @CustomTestProvider
  public Collection<TestFunction> freezesEveryMobThatFeelsTheCold() {
    return MobArena.perMob(
        "frost", "freezes", mob -> true, MobArena.SHORT_LIMIT, FrostArrowMobsGameTest::freezes);
  }

  private static void freezes(final TestContext context, final Mob mob) {
    final MobEntity target = MobArena.still(context, mob, MobArena.NEAR_STAND);
    context.runAtTick(
        MobArena.SETTLED,
        () -> FrostGripService.grip(context.getWorld(), target, FrostArrowConfig.defaults()));
    context.runAtTick(
        MobArena.SETTLED + 5,
        () -> {
          if (VANILLA_FREEZE_IMMUNE.contains(mob.type())) {
            MobArena.check(
                context,
                target.getFrozenTicks() == 0,
                "A " + mob.name() + " is immune to the cold");
          } else {
            MobArena.check(
                context, target.isFrozen(), "A frost arrow should freeze a " + mob.name());
          }
          context.complete();
        });
  }
}
