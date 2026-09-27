package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.Escort;
import com.grahambartley.notenougharrows.control.MobAggression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class ControlMatrixGameTest implements FabricGameTest {
  private static final String BATCH = "control-matrix";
  private static final BlockPos PLAYER_STAND = new BlockPos(2, 2, 3);
  private static final BlockPos NEAR_PLAYER = new BlockPos(5, 2, 3);
  private static final BlockPos THREAT_STAND = new BlockPos(4, 2, 5);
  private static final BlockPos FAR_DECOY = new BlockPos(15, 2, 3);
  private static final BlockPos FAR_STAND = new BlockPos(18, 2, 3);
  private static final int LANDED = 10;
  private static final int TICK_LIMIT = 220;
  private static final double STURDY = 400.0;
  private static final int FLEE_WATCH = 50;

  static final List<EntityType<? extends MobEntity>> HOSTILES =
      List.of(
          EntityType.ZOMBIE,
          EntityType.HUSK,
          EntityType.DROWNED,
          EntityType.SKELETON,
          EntityType.STRAY,
          EntityType.BOGGED,
          EntityType.WITHER_SKELETON,
          EntityType.SPIDER,
          EntityType.CAVE_SPIDER,
          EntityType.VINDICATOR,
          EntityType.PILLAGER,
          EntityType.WITCH,
          EntityType.SILVERFISH,
          EntityType.ENDERMITE,
          EntityType.RAVAGER,
          EntityType.PIGLIN,
          EntityType.PIGLIN_BRUTE,
          EntityType.HOGLIN,
          EntityType.ZOGLIN,
          EntityType.ZOMBIFIED_PIGLIN);

  @CustomTestProvider
  public Collection<TestFunction> matrix() {
    final List<TestFunction> tests = new ArrayList<>();
    for (final EntityType<? extends MobEntity> type : HOSTILES) {
      add(tests, "tauntswitchestostruckmob", type, ControlMatrixGameTest::tauntSwitches);
      add(tests, "allydefendsshooter", type, ControlMatrixGameTest::allyDefends);
      add(tests, "allyfollowsshooter", type, ControlMatrixGameTest::allyFollows);
      add(tests, "repelledmobflees", type, ControlMatrixGameTest::repelledFlees);
    }
    return tests;
  }

  private static void add(
      final List<TestFunction> tests,
      final String behavior,
      final EntityType<? extends MobEntity> type,
      final BiConsumer<TestContext, EntityType<? extends MobEntity>> body) {
    tests.add(
        new TestFunction(
            BATCH,
            "notenougharrows.matrix." + behavior + "." + EntityType.getId(type).getPath(),
            CombatTestSupport.LONG_RANGE,
            TICK_LIMIT,
            0L,
            true,
            context -> body.accept(context, type)));
  }

  static MobEntity naturalMob(
      final TestContext context, final EntityType<? extends MobEntity> type, final BlockPos at) {
    final MobEntity mob = context.spawnMob(type, at);
    mob.initialize(
        context.getWorld(),
        context.getWorld().getLocalDifficulty(context.getAbsolutePos(at)),
        SpawnReason.MOB_SUMMONED,
        null);
    if (mob instanceof ZombieEntity zombie) {
      zombie.setBaby(false);
    }
    if (mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
      mob.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }
    mob.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(STURDY);
    mob.setHealth(mob.getMaxHealth());
    return mob;
  }

  static PlayerEntity unharmablePlayer(final TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    player.changeGameMode(GameMode.SURVIVAL);
    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, TICK_LIMIT * 2, 4));
    return player;
  }

  static CowEntity sturdyStillCow(final TestContext context, final BlockPos at) {
    context.setBlockState(at.down(), Blocks.BEDROCK);
    final CowEntity cow = context.spawnMob(EntityType.COW, at);
    cow.setAiDisabled(true);
    cow.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(STURDY);
    cow.setHealth(cow.getMaxHealth());
    return cow;
  }

  static void provoke(final MobEntity mob, final LivingEntity target) {
    MobAggression.aim(mob, target);
  }

  static String name(final EntityType<?> type) {
    return EntityType.getId(type).getPath();
  }

  private static void tauntSwitches(
      final TestContext context, final EntityType<? extends MobEntity> type) {
    final PlayerEntity player = unharmablePlayer(context);
    final MobEntity attacker = naturalMob(context, type, NEAR_PLAYER);
    final CowEntity decoy = sturdyStillCow(context, FAR_DECOY);
    context.runAtTick(2, () -> provoke(attacker, player));
    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.taunt(
                context.getWorld(),
                decoy.getPos(),
                decoy,
                player,
                TargetingArrowConfig.defaults()));
    for (final int tick : new int[] {LANDED + 20, LANDED + 60}) {
      context.runAtTick(
          tick,
          () ->
              context.assertTrue(
                  attacker.getTarget() == decoy,
                  "A "
                      + name(type)
                      + " attacking the shooter should switch to the mob a taunt arrow struck, but"
                      + " at tick "
                      + tick
                      + " it targets "
                      + attacker.getTarget()));
    }
    context.runAtTick(LANDED + 61, context::complete);
  }

  private static void allyDefends(
      final TestContext context, final EntityType<? extends MobEntity> type) {
    final PlayerEntity player = unharmablePlayer(context);
    final MobEntity ally = naturalMob(context, type, NEAR_PLAYER);
    final MobEntity threat = naturalMob(context, EntityType.ZOMBIE, THREAT_STAND);
    context.runAtTick(2, () -> provoke(threat, player));
    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.enlist(
                context.getWorld(), ally, player, AllegianceArrowConfig.defaults()));
    context.runAtTick(
        LANDED + 30,
        () -> {
          context.assertTrue(
              ally.getTarget() == threat,
              "An allied "
                  + name(type)
                  + " should attack whatever attacks the shooter, but it targets "
                  + ally.getTarget());
          context.complete();
        });
  }

  private static void allyFollows(
      final TestContext context, final EntityType<? extends MobEntity> type) {
    final CowEntity defended = sturdyStillCow(context, PLAYER_STAND);
    final MobEntity ally = naturalMob(context, type, FAR_STAND);
    context.runAtTick(
        LANDED,
        () ->
            ControlHoldService.enlist(
                context.getWorld(), ally, defended, AllegianceArrowConfig.defaults()));
    context.runAtTick(
        TICK_LIMIT - 10,
        () -> {
          final double gap = Math.sqrt(ally.squaredDistanceTo(defended));
          context.assertTrue(
              ally.getTarget() != defended,
              "An allied " + name(type) + " should never attack who it defends");
          context.assertTrue(
              gap <= Escort.CLOSE_IN_BEYOND + 1.0,
              "An allied "
                  + name(type)
                  + " with nothing to fight should follow who it defends, but it is "
                  + gap
                  + " blocks away");
          context.complete();
        });
  }

  private static void repelledFlees(
      final TestContext context, final EntityType<? extends MobEntity> type) {
    final PlayerEntity player = unharmablePlayer(context);
    final MobEntity mob = naturalMob(context, type, NEAR_PLAYER);
    final double[] start = new double[1];
    final double[] furthest = {Double.NEGATIVE_INFINITY};
    context.runAtTick(2, () -> provoke(mob, player));
    context.runAtTick(
        LANDED,
        () -> {
          start[0] = mob.getX() - player.getX();
          ControlHoldService.repel(
              context.getWorld(), player.getPos(), TargetingArrowConfig.defaults());
        });
    for (int t = LANDED + 10; t <= LANDED + FLEE_WATCH; t += 5) {
      final int tick = t;
      context.runAtTick(
          tick,
          () -> {
            final double fled = mob.getX() - player.getX();
            context.assertTrue(
                mob.getTarget() == null,
                "A repelled "
                    + name(type)
                    + " should drop its target while it runs, at tick "
                    + tick);
            context.assertTrue(
                fled >= furthest[0] - 0.5,
                "A repelled "
                    + name(type)
                    + " turned back toward the fight, from "
                    + furthest[0]
                    + " to "
                    + fled
                    + " at tick "
                    + tick);
            furthest[0] = Math.max(furthest[0], fled);
          });
    }
    context.runAtTick(
        LANDED + FLEE_WATCH + 1,
        () -> {
          context.assertTrue(
              furthest[0] - start[0] >= 4.0,
              "A repelled "
                  + name(type)
                  + " should get well away, but only fled "
                  + (furthest[0] - start[0]));
          context.complete();
        });
  }
}
