package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.MobAggression;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class TauntArrowMobsGameTest implements FabricGameTest {
  private static final int FIRST_LOOK = 20;
  private static final int SECOND_LOOK = 60;

  @CustomTestProvider
  public Collection<TestFunction> everyFighterHuntingTheShooterTurnsOnTheStruckMob() {
    return MobArena.perMob(
        "taunt", "switches", Mob::fights, MobArena.LONG_LIMIT, TauntArrowMobsGameTest::switches);
  }

  private static void switches(final TestContext context, final Mob mob) {
    final ServerPlayerEntity player = MobArena.player(context);
    final MobEntity attacker = MobArena.thinking(context, mob, MobArena.NEAR_STAND);
    final CowEntity struck = MobArena.cow(context, MobArena.DECOY_STAND);
    for (int provoking = 2; provoking < MobArena.SETTLED; provoking++) {
      context.runAtTick(provoking, () -> MobAggression.aim(attacker, player));
    }
    context.runAtTick(
        MobArena.SETTLED,
        () ->
            ControlHoldService.taunt(
                context.getWorld(),
                struck.getPos(),
                struck,
                player,
                TargetingArrowConfig.defaults()));
    for (final int look : new int[] {FIRST_LOOK, SECOND_LOOK}) {
      context.runAtTick(
          MobArena.SETTLED + look,
          () ->
              MobArena.check(
                  context,
                  attacker.getTarget() == struck,
                  "A "
                      + mob.name()
                      + " hunting the shooter should turn on the mob a taunt struck, but "
                      + look
                      + " ticks in it targets "
                      + attacker.getTarget()));
    }
    context.runAtTick(MobArena.SETTLED + SECOND_LOOK + 1, context::complete);
  }
}
