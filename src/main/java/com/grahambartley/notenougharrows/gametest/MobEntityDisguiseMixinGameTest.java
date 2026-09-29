package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.MobAggression;
import com.grahambartley.notenougharrows.disguise.DisguiseEligibility;
import com.grahambartley.notenougharrows.disguise.DisguiseService;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;

public final class MobEntityDisguiseMixinGameTest implements FabricGameTest {
  private static final BlockPos PLAYER_STAND = new BlockPos(4, 3, 8);
  private static final BlockPos FIGHTER_STAND = new BlockPos(8, 3, 8);
  private static final int DISGUISED_AT = 2;
  private static final int PROVOKED_UNTIL = 10;
  private static final int WATCH = 200;
  private static final int DISGUISE_TICKS = 2000;

  @CustomTestProvider
  public Collection<TestFunction> everyDisguisedFighterLeavesAProvokingPlayerUnhurt() {
    return MobArena.perMob(
        "polymorph",
        "harmless",
        Mob::fights,
        MobArena.LONG_LIMIT,
        MobEntityDisguiseMixinGameTest::harmless);
  }

  private static void harmless(final TestContext context, final Mob mob) {
    final ServerPlayerEntity player = ChaosTestSupport.sturdyPlayerAt(context, PLAYER_STAND);
    final MobEntity fighter = ChaosTestSupport.fighting(context, mob, FIGHTER_STAND);
    context.runAtTick(
        DISGUISED_AT,
        () -> {
          final boolean eligible = DisguiseEligibility.canDisguise(fighter);
          MobArena.check(
              context,
              DisguiseService.disguise(context.getWorld(), fighter, DISGUISE_TICKS) == eligible,
              "A " + mob.name() + " should be disguised exactly when it is eligible");
          if (!eligible) {
            context.complete();
          }
        });
    for (int provoking = DISGUISED_AT + 1; provoking < PROVOKED_UNTIL; provoking++) {
      context.runAtTick(provoking, () -> MobAggression.aim(fighter, player));
    }
    context.runAtTick(
        PROVOKED_UNTIL + WATCH,
        () -> {
          MobArena.check(
              context,
              player.getHealth() == player.getMaxHealth(),
              "A disguised "
                  + mob.name()
                  + " should leave the player unhurt, but the player lost "
                  + (player.getMaxHealth() - player.getHealth()));
          DisguiseService.revert(context.getWorld(), fighter);
          context.complete();
        });
  }
}
