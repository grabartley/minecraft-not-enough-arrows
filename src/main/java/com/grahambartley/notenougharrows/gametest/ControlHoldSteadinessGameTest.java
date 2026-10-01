package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.Escort;
import com.grahambartley.notenougharrows.control.MobSteering;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class ControlHoldSteadinessGameTest implements FabricGameTest {
  private static final String BATCH = "control-hold-steadiness";
  private static final BlockPos PLAYER_STAND = new BlockPos(2, 2, 3);
  private static final BlockPos HOSTILE_STAND = new BlockPos(5, 2, 3);
  private static final BlockPos FAR_HOSTILE_STAND = new BlockPos(16, 2, 3);
  private static final int HOLD_TICKS = 200;
  private static final int UNHARMABLE = 4;
  private static final int LANDED = 10;
  private static final int FIRST_SAMPLE = 20;
  private static final int LAST_SAMPLE = 80;
  private static final int SAMPLE_EVERY = 5;
  private static final double TURNING_BACK = 0.25;
  private static final int ESCORT_DEADLINE = 160;
  private static final int LIES_DOWN = 25;
  private static final BlockPos SWIM_STAND = new BlockPos(10, 4, 8);
  private static final float FACING_AWAY = 30.0f;

  private static PlayerEntity unharmablePlayer(final TestContext context) {
    final PlayerEntity player = MockPlayerSupport.mortalPlayerAt(context, PLAYER_STAND);
    player.addStatusEffect(
        new StatusEffectInstance(StatusEffects.RESISTANCE, HOLD_TICKS * 2, UNHARMABLE));
    return player;
  }

  private static ZombieEntity huntingZombie(final TestContext context, final PlayerEntity prey) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, HOSTILE_STAND);
    zombie.setTarget(prey);
    return zombie;
  }

  private static TargetingArrowConfig repellingFor(final int ticks) {
    return new TargetingArrowConfig(
        8.0f, ticks, 8.0f, ticks, TargetingArrowConfig.DEFAULT_REPEL_DISTANCE);
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 100)
  public void aRepelledHostileRunsWithoutTurningBackToFight(TestContext context) {
    final PlayerEntity player = unharmablePlayer(context);
    final ZombieEntity zombie = huntingZombie(context, player);
    final double[] furthest = {Double.NEGATIVE_INFINITY};

    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.repel(
                context.getWorld(), player.getPos(), repellingFor(HOLD_TICKS)));
    for (int tick = FIRST_SAMPLE; tick <= LAST_SAMPLE; tick += SAMPLE_EVERY) {
      context.runAtTick(
          tick,
          () -> {
            final double fled = zombie.getX() - player.getX();
            context.assertTrue(
                zombie.getTarget() == null,
                "A fleeing hostile should not keep a target to be pulled back toward");
            context.assertTrue(
                fled >= furthest[0] - TURNING_BACK,
                "A fleeing hostile should not turn back toward the fight, but it came back from "
                    + furthest[0]
                    + " to "
                    + fled);
            furthest[0] = Math.max(furthest[0], fled);
          });
    }
    context.runAtTick(LAST_SAMPLE + 1, context::complete);
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void aCorneredRepelledHostileKeepsItsTargetToFightBack(TestContext context) {
    final PlayerEntity player = unharmablePlayer(context);
    for (final Direction side : Direction.Type.HORIZONTAL) {
      context.setBlockState(HOSTILE_STAND.offset(side), Blocks.BEDROCK);
      context.setBlockState(HOSTILE_STAND.offset(side).up(), Blocks.BEDROCK);
    }
    context.setBlockState(HOSTILE_STAND.up(2), Blocks.BEDROCK);
    final ZombieEntity zombie = huntingZombie(context, player);

    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.repel(
                context.getWorld(), player.getPos(), repellingFor(HOLD_TICKS)));
    context.runAtTick(
        LANDED + 20,
        () -> {
          context.assertTrue(
              zombie.getTarget() == player,
              "A repelled hostile with nowhere to run should keep its target so it can fight back");
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 200)
  public void anAlliedHostileWithNothingToFightFollowsWhoItDefends(TestContext context) {
    context.setBlockState(PLAYER_STAND.down(), Blocks.STONE);
    final CowEntity defended = context.spawnMob(EntityType.COW, PLAYER_STAND);
    defended.setAiDisabled(true);
    final ZombieEntity ally = context.spawnMob(EntityType.ZOMBIE, FAR_HOSTILE_STAND);
    ControlHoldService.enlist(context.getWorld(), ally, defended, AllegianceArrowConfig.defaults());

    context.runAtTick(
        ESCORT_DEADLINE,
        () -> {
          final double gap = Math.sqrt(ally.squaredDistanceTo(defended));
          context.assertTrue(
              gap <= Escort.CLOSE_IN_BEYOND,
              "An allied hostile with nothing to fight should follow who it defends, like a pet,"
                  + " but it is "
                  + gap
                  + " blocks away");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH + "-distance", tickLimit = 220)
  public void aRepelledMobStopsOnceItIsTheFleeDistanceAway(TestContext context) {
    final PlayerEntity player = MockPlayerSupport.mortalPlayerAt(context, MobArena.PLAYER_STAND);
    player.addStatusEffect(
        new StatusEffectInstance(StatusEffects.RESISTANCE, HOLD_TICKS * 2, UNHARMABLE));
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, MobArena.NEAR_STAND);
    final Vec3d[] impact = new Vec3d[1];
    context.runAtTick(
        LANDED,
        () -> {
          impact[0] = player.getPos();
          ControlHoldService.repel(context.getWorld(), impact[0], TargetingArrowConfig.defaults());
        });
    context.runAtTick(
        200,
        () -> {
          final double away = Math.hypot(zombie.getX() - impact[0].x, zombie.getZ() - impact[0].z);
          final double fleeDistance = TargetingArrowConfig.DEFAULT_REPEL_DISTANCE;
          context.assertTrue(
              away >= fleeDistance - 1.0 && away <= fleeDistance + 4.0,
              "A repelled mob should stop about the flee distance away rather than keep running,"
                  + " but it is "
                  + away
                  + " blocks from the impact");
          context.assertTrue(
              zombie.getTarget() == null,
              "A mob that stopped running should still leave you alone");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH + "-rouse", tickLimit = 60)
  public void anAlliedPandaThatLiesDownOnTheWayGetsBackUp(TestContext context) {
    final CowEntity defended = MobArena.cow(context, MobArena.PLAYER_STAND);
    final PandaEntity ally = context.spawnMob(EntityType.PANDA, MobArena.FAR_STAND);
    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.enlist(
                context.getWorld(), ally, defended, AllegianceArrowConfig.defaults()));
    context.runAtTick(LIES_DOWN, () -> ally.setLyingOnBack(true));
    context.runAtTick(
        LIES_DOWN + 2,
        () -> {
          context.assertFalse(
              ally.isLyingOnBack(),
              "An allied panda with ground to cover should get up rather than lie on its back");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH + "-rouse", tickLimit = 60)
  public void aRepelledPandaThatLiesDownOnTheWayGetsBackUp(TestContext context) {
    final PandaEntity fleeing = context.spawnMob(EntityType.PANDA, MobArena.NEAR_STAND);
    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.repel(
                context.getWorld(),
                fleeing.getPos().add(-2.0, 0.0, 0.0),
                TargetingArrowConfig.defaults()));
    context.runAtTick(LIES_DOWN, () -> fleeing.setLyingOnBack(true));
    context.runAtTick(
        LIES_DOWN + 2,
        () -> {
          context.assertFalse(
              fleeing.isLyingOnBack(),
              "A repelled panda should keep running rather than lie on its back");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH + "-swimmer", tickLimit = 40)
  public void aRepelledAxolotlTurnsAwayAtOnceInsteadOfSwimmingOnThroughTheImpact(
      TestContext context) {
    for (int x = 1; x <= 20; x++) {
      for (int y = 3; y <= 6; y++) {
        for (int z = 4; z <= 12; z++) {
          context.setBlockState(new BlockPos(x, y, z), Blocks.WATER);
        }
      }
    }
    final AxolotlEntity axolotl = context.spawnMob(EntityType.AXOLOTL, SWIM_STAND);
    final Vec3d[] impact = new Vec3d[1];
    context.runAtTick(
        LANDED,
        () -> {
          impact[0] = axolotl.getPos().add(-3.0, 0.0, 0.0);
          axolotl.setYaw(MobSteering.yawToward(axolotl.getPos(), impact[0]));
          ControlHoldService.repel(context.getWorld(), impact[0], TargetingArrowConfig.defaults());
        });
    context.runAtTick(
        LANDED + 1,
        () -> {
          final float away = MobSteering.yawToward(impact[0], axolotl.getPos());
          final float off = Math.abs(MathHelper.wrapDegrees(axolotl.getYaw() - away));
          context.assertTrue(
              off <= FACING_AWAY,
              "A repelled axolotl should turn its back on the impact at once, but it is "
                  + off
                  + " degrees off");
          context.complete();
        });
  }
}
