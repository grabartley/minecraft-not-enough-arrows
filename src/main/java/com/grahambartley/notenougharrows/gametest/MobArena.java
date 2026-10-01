package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import com.grahambartley.notenougharrows.gametest.MobRoster.Moves;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class MobArena {
  private static final Logger LOGGER = LoggerFactory.getLogger(MobArena.class);
  static final String TEMPLATE = "not-enough-arrows:open_arena";
  static final double ARENA_WIDTH = 16.0;
  static final BlockPos PLAYER_STAND = new BlockPos(4, 3, 8);
  static final BlockPos NEAR_STAND = new BlockPos(8, 3, 8);
  static final BlockPos THREAT_STAND = new BlockPos(6, 3, 11);
  static final BlockPos DECOY_STAND = new BlockPos(22, 3, 8);
  static final BlockPos FAR_STAND = new BlockPos(18, 3, 8);
  static final BlockPos SECOND_WARD_STAND = new BlockPos(21, 3, 8);
  static final int SETTLED = 10;
  static final int SHORT_LIMIT = 60;
  static final int LONG_LIMIT = 320;
  static final int SLOW_LIMIT = 620;

  private static final Map<EntityType<?>, EntityType<? extends MobEntity>> THREATS_LEFT_ALONE =
      Map.of(
          EntityType.AXOLOTL, EntityType.ZOMBIE,
          EntityType.IRON_GOLEM, EntityType.WOLF,
          EntityType.SNOW_GOLEM, EntityType.WOLF,
          EntityType.ZOGLIN, EntityType.ZOGLIN);
  private static final int AIRBORNE = 3;
  private static final int SUBMERGED = 2;
  private static final int WATER_TOP = 8;
  private static final double STURDY = 400.0;
  private static final int UNHARMABLE = 4;

  private MobArena() {}

  static Collection<TestFunction> perMob(
      final String arrow,
      final String behavior,
      final Predicate<Mob> applies,
      final int tickLimit,
      final BiConsumer<TestContext, Mob> body) {
    return MobRoster.EVERY_MOB.stream()
        .filter(applies)
        .map(
            mob ->
                new TestFunction(
                    batchFor(arrow, behavior, mob),
                    "notenougharrows." + arrow + "." + behavior + "." + mob.name(),
                    TEMPLATE,
                    tickLimit,
                    0L,
                    true,
                    context -> {
                      prepare(context, mob);
                      body.accept(context, mob);
                    }))
        .toList();
  }

  private static String batchFor(final String arrow, final String behavior, final Mob mob) {
    final String group = MobRoster.isBoss(mob) ? mob.name() : mob.moves().name().toLowerCase();
    return arrow + "-" + behavior + "-" + group;
  }

  private static void prepare(final TestContext context, final Mob mob) {
    context.getWorld().getGameRules().get(GameRules.DO_MOB_GRIEFING).set(false, null);
    context.checkBlock(PLAYER_STAND.down(), Blocks.BEDROCK::equals, "The arena stands on bedrock");
    context.checkBlock(PLAYER_STAND, Blocks.AIR::equals, "The arena stands are open air");
    if (mob.moves() != Moves.WATER) {
      return;
    }
    for (int x = 1; x <= 46; x++) {
      for (int y = PLAYER_STAND.getY(); y <= WATER_TOP; y++) {
        for (int z = 1; z <= 14; z++) {
          context.setBlockState(new BlockPos(x, y, z), Blocks.WATER);
        }
      }
    }
  }

  static BlockPos standFor(final Mob mob, final BlockPos base) {
    return switch (mob.moves()) {
      case AIR -> base.up(AIRBORNE);
      case WATER -> base.up(SUBMERGED);
      default -> base;
    };
  }

  static MobEntity still(final TestContext context, final Mob mob, final BlockPos at) {
    final MobEntity spawned = context.spawnMob(mob.type(), standFor(mob, at));
    spawned.setAiDisabled(true);
    return sturdy(spawned);
  }

  static MobEntity thinking(final TestContext context, final Mob mob, final BlockPos at) {
    final MobEntity spawned = context.spawnEntity(mob.type(), standFor(mob, at));
    spawned.initialize(
        context.getWorld(),
        context.getWorld().getLocalDifficulty(context.getAbsolutePos(at)),
        SpawnReason.MOB_SUMMONED,
        null);
    spawned.setBaby(false);
    if (spawned instanceof AbstractPiglinEntity piglin) {
      piglin.setImmuneToZombification(true);
    }
    if (spawned instanceof HoglinEntity hoglin) {
      hoglin.setImmuneToZombification(true);
    }
    if (spawned instanceof PandaEntity panda) {
      panda.setMainGene(PandaEntity.Gene.NORMAL);
      panda.setHiddenGene(PandaEntity.Gene.NORMAL);
      panda
          .getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
          .setBaseValue(
              DefaultAttributeRegistry.get(EntityType.PANDA)
                  .getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
    }
    return sturdy(helmeted(spawned));
  }

  static MobEntity threatFor(final TestContext context, final Mob mob) {
    return sturdy(
        helmeted(context.spawnEntity(threatLeftAloneBy(mob), standFor(mob, THREAT_STAND))));
  }

  static EntityType<? extends MobEntity> threatLeftAloneBy(final Mob mob) {
    final EntityType<? extends MobEntity> leftAlone = THREATS_LEFT_ALONE.get(mob.type());
    if (leftAlone != null) {
      return leftAlone;
    }
    return mob.moves() == Moves.WATER ? EntityType.DROWNED : EntityType.ZOMBIE;
  }

  private static MobEntity helmeted(final MobEntity mob) {
    if (mob.getType().isIn(EntityTypeTags.UNDEAD)
        && mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
      mob.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }
    return mob;
  }

  private static MobEntity sturdy(final MobEntity mob) {
    mob.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(STURDY);
    mob.setHealth(mob.getMaxHealth());
    return mob;
  }

  static ServerPlayerEntity player(final TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.survivalPlayerAt(context, PLAYER_STAND);
    player.addStatusEffect(
        new StatusEffectInstance(StatusEffects.RESISTANCE, LONG_LIMIT * 2, UNHARMABLE));
    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, LONG_LIMIT * 2));
    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, LONG_LIMIT * 2));
    return player;
  }

  static CowEntity cow(final TestContext context, final BlockPos at) {
    final CowEntity cow = ControlTestSupport.sturdyStillCow(context, at);
    cow.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, LONG_LIMIT * 2));
    cow.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, LONG_LIMIT * 2));
    return cow;
  }

  static void clearPedestal(final TestContext context, final Mob mob, final BlockPos at) {
    if (mob.moves() == Moves.AIR) {
      context.setBlockState(standFor(mob, at).down(), Blocks.AIR);
    } else if (mob.moves() == Moves.WATER) {
      context.setBlockState(standFor(mob, at).down(), Blocks.WATER);
    }
  }

  static Item weaponFor(final Mob mob) {
    final EntityType<?> type = mob.type();
    if (type == EntityType.SKELETON
        || type == EntityType.STRAY
        || type == EntityType.BOGGED
        || type == EntityType.ILLUSIONER) {
      return Items.BOW;
    }
    if (type == EntityType.PILLAGER) {
      return Items.CROSSBOW;
    }
    if (type == EntityType.ALLAY) {
      return Items.DIAMOND;
    }
    if (type == EntityType.FOX) {
      return Items.EMERALD;
    }
    if (type == EntityType.PIGLIN
        || type == EntityType.PIGLIN_BRUTE
        || type == EntityType.ZOMBIFIED_PIGLIN) {
      return Items.GOLDEN_SWORD;
    }
    return Items.IRON_SWORD;
  }

  static void check(final TestContext context, final boolean holds, final String promise) {
    if (!holds) {
      LOGGER.warn("Per-mob control check failed: {}", promise);
    }
    context.assertTrue(holds, promise);
  }
}
