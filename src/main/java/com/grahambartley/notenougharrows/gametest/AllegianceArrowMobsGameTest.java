package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.Escort;
import com.grahambartley.notenougharrows.control.MobAggression;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import java.util.Set;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class AllegianceArrowMobsGameTest implements FabricGameTest {
  private static final int FIGHT_DEADLINE = 40;
  private static final double AT_HEEL = Escort.CLOSE_IN_BEYOND + 1.5;
  private static final double CLOSED_IN = 4.0;
  private static final int FINAL_STRETCH = 100;
  private static final Set<EntityType<?>> FOLLOWS_AWAITING_A_FIX =
      Set.of(EntityType.SPIDER, EntityType.GHAST, EntityType.WITHER);
  private static final AllegianceArrowConfig LASTS_THE_WHOLE_TEST =
      new AllegianceArrowConfig(MobArena.SLOW_LIMIT, AllegianceArrowConfig.DEFAULT_DEFEND_RADIUS);

  @CustomTestProvider
  public Collection<TestFunction> everyFighterDefendsTheShooter() {
    return MobArena.perMob(
        "allegiance",
        "defends",
        mob -> mob.fights() && mob.type() != EntityType.EVOKER,
        MobArena.LONG_LIMIT,
        AllegianceArrowMobsGameTest::defends);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyMobThatMovesFollowsTheShooter() {
    return MobArena.perMob(
        "allegiance",
        "follows",
        mob -> mob.movesAround() && !FOLLOWS_AWAITING_A_FIX.contains(mob.type()),
        MobArena.SLOW_LIMIT,
        AllegianceArrowMobsGameTest::follows);
  }

  private static void defends(final TestContext context, final Mob mob) {
    final ServerPlayerEntity player = MobArena.player(context);
    final MobEntity ally = MobArena.thinking(context, mob, MobArena.NEAR_STAND);
    final MobEntity threat = MobArena.threatFor(context, mob);
    context.runAtTick(2, () -> MobAggression.aim(threat, player));
    context.runAtTick(
        MobArena.SETTLED,
        () ->
            ControlHoldService.enlist(
                context.getWorld(), ally, player, AllegianceArrowConfig.defaults()));
    context.runAtTick(
        MobArena.SETTLED + FIGHT_DEADLINE,
        () -> {
          MobArena.check(
              context,
              ally.getTarget() == threat,
              "An allied "
                  + mob.name()
                  + " should attack whatever attacks the shooter, but it targets "
                  + ally.getTarget());
          context.complete();
        });
  }

  private static void follows(final TestContext context, final Mob mob) {
    final CowEntity defended = MobArena.cow(context, MobArena.standFor(mob, MobArena.PLAYER_STAND));
    final MobEntity ally = MobArena.thinking(context, mob, MobArena.FAR_STAND);
    final double[] startingGap = new double[1];
    final double[] closest = {Double.POSITIVE_INFINITY};
    for (int t = MobArena.SLOW_LIMIT - FINAL_STRETCH; t < MobArena.SLOW_LIMIT - 10; t += 5) {
      context.runAtTick(
          t, () -> closest[0] = Math.min(closest[0], Math.sqrt(ally.squaredDistanceTo(defended))));
    }
    context.runAtTick(
        MobArena.SETTLED,
        () -> {
          startingGap[0] = Math.sqrt(ally.squaredDistanceTo(defended));
          ControlHoldService.enlist(context.getWorld(), ally, defended, LASTS_THE_WHOLE_TEST);
        });
    context.runAtTick(
        MobArena.SLOW_LIMIT - 10,
        () -> {
          final double gap = Math.min(closest[0], Math.sqrt(ally.squaredDistanceTo(defended)));
          MobArena.check(
              context,
              ally.getTarget() != defended,
              "An allied " + mob.name() + " should never attack who it defends");
          MobArena.check(
              context,
              gap <= AT_HEEL + ally.getWidth() || startingGap[0] - gap >= CLOSED_IN,
              "An allied "
                  + mob.name()
                  + " with nothing to fight should follow who it defends, but it is "
                  + gap
                  + " blocks away");
          context.complete();
        });
  }
}
