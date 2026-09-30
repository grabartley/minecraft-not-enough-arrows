package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.Escort;
import com.grahambartley.notenougharrows.control.MobAggression;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
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
  private static final int SAMPLE_EVERY = 5;
  private static final int WON_OVER = 310;
  private static final int LAST_LOOK = MobArena.SLOW_LIMIT - 10;
  private static final double WIDEST_HEEL = 2.0;
  private static final double MOSTLY = 0.75;
  private static final Set<EntityType<?>> FOLLOWS_AWAITING_A_FIX =
      Set.of(EntityType.AXOLOTL, EntityType.PANDA);
  private static final Set<EntityType<?>> TOO_SLOW_TO_KEEP_UP =
      Set.of(EntityType.CAMEL, EntityType.MAGMA_CUBE, EntityType.TURTLE);
  private static final Set<EntityType<?>> JUMPS_ABOUT =
      Set.of(EntityType.ENDERMAN, EntityType.SLIME);
  private static final AllegianceArrowConfig LASTS_THE_WHOLE_TEST =
      new AllegianceArrowConfig(MobArena.SLOW_LIMIT, AllegianceArrowConfig.DEFAULT_DEFEND_RADIUS);

  @CustomTestProvider
  public Collection<TestFunction> everyFighterDefendsTheShooter() {
    return MobArena.perMob(
        "allegiance",
        "defends",
        mob -> mob.fights(),
        MobArena.LONG_LIMIT,
        AllegianceArrowMobsGameTest::defends);
  }

  @CustomTestProvider
  public Collection<TestFunction> everyMobThatMovesFollowsTheShooter() {
    return MobArena.perMob(
        "allegiance",
        "follows",
        mob ->
            mob.movesAround()
                && !MobRoster.outgrowsTheArena(mob)
                && !FOLLOWS_AWAITING_A_FIX.contains(mob.type()),
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
    final CowEntity first = MobArena.cow(context, MobArena.standFor(mob, MobArena.PLAYER_STAND));
    final MobEntity ally = MobArena.thinking(context, mob, MobArena.FAR_STAND);
    final Ward toFirst = new Ward(context, mob, ally, "first");
    final Ward toSecond = new Ward(context, mob, ally, "second");
    toFirst.enlist(MobArena.SETTLED, WON_OVER, () -> first);
    toSecond.enlist(
        WON_OVER,
        LAST_LOOK,
        () -> {
          first.discard();
          MobArena.clearPedestal(context, mob, MobArena.PLAYER_STAND);
          return MobArena.cow(context, MobArena.standFor(mob, MobArena.SECOND_WARD_STAND));
        });
    context.runAtTick(
        LAST_LOOK,
        () -> {
          toFirst.check();
          toSecond.check();
          context.complete();
        });
  }

  private static final class Ward {
    private final TestContext context;
    private final Mob mob;
    private final MobEntity ally;
    private final String name;
    private CowEntity defended;
    private final List<Double> finalStretch = new ArrayList<>();
    private double startingGap;
    private double endingGap;
    private boolean attacked;

    Ward(final TestContext context, final Mob mob, final MobEntity ally, final String name) {
      this.context = context;
      this.mob = mob;
      this.ally = ally;
      this.name = name;
    }

    void enlist(final int from, final int until, final Supplier<CowEntity> ward) {
      context.runAtTick(
          from,
          () -> {
            defended = ward.get();
            startingGap = gap();
            ControlHoldService.enlist(context.getWorld(), ally, defended, LASTS_THE_WHOLE_TEST);
          });
      for (int t = from + SAMPLE_EVERY; t < until; t += SAMPLE_EVERY) {
        final boolean inFinalStretch = t >= until - FINAL_STRETCH;
        context.runAtTick(t, () -> sample(inFinalStretch));
      }
      context.runAtTick(until, () -> endingGap = gap());
    }

    private void sample(final boolean inFinalStretch) {
      attacked |= ally.getTarget() == defended;
      if (inFinalStretch) {
        finalStretch.add(gap());
      }
    }

    void check() {
      MobArena.check(
          context,
          !attacked,
          "An allied " + mob.name() + " should never attack the " + name + " cow it defends");
      if (TOO_SLOW_TO_KEEP_UP.contains(mob.type())) {
        MobArena.check(
            context,
            startingGap - endingGap >= CLOSED_IN || endingGap <= heelFor(ally),
            "An allied "
                + mob.name()
                + " should close in on the "
                + name
                + " cow it defends, but only went from "
                + startingGap
                + " to "
                + endingGap
                + " blocks away");
        return;
      }
      final double heel = heelFor(ally);
      if (mob.moves() == MobRoster.Moves.WATER) {
        final long atHeel = finalStretch.stream().filter(gap -> gap <= heel).count();
        MobArena.check(
            context,
            atHeel >= finalStretch.size() * MOSTLY,
            "An allied "
                + mob.name()
                + " with nothing to fight should swim at the heel of the "
                + name
                + " cow it defends, but it was there for only "
                + atHeel
                + " of "
                + finalStretch.size()
                + " looks");
        return;
      }
      final boolean circles =
          mob.moves() == MobRoster.Moves.AIR || JUMPS_ABOUT.contains(mob.type());
      final double near =
          circles
              ? finalStretch.stream().mapToDouble(Double::doubleValue).min().orElse(startingGap)
              : finalStretch.stream().mapToDouble(Double::doubleValue).max().orElse(startingGap);
      MobArena.check(
          context,
          near <= heel,
          "An allied "
              + mob.name()
              + " with nothing to fight should "
              + (circles ? "keep coming back to" : "stay at the heel of")
              + " the "
              + name
              + " cow it defends, but it was "
              + near
              + " blocks away");
    }

    private static double heelFor(final MobEntity ally) {
      return AT_HEEL + Math.min(ally.getWidth(), WIDEST_HEEL);
    }

    private double gap() {
      return Math.sqrt(ally.squaredDistanceTo(defended));
    }
  }
}
