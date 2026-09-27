package com.grahambartley.notenougharrows.gametest;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;

final class MobRoster {

  enum Moves {
    GROUND,
    AIR,
    WATER,
    NOWHERE
  }

  record Mob(
      EntityType<? extends MobEntity> type, Moves moves, boolean fights, boolean holdsWeapon) {
    String name() {
      return EntityType.getId(type).getPath();
    }

    boolean movesAround() {
      return moves != Moves.NOWHERE;
    }
  }

  private static Mob mob(
      final EntityType<? extends MobEntity> type,
      final Moves moves,
      final boolean fights,
      final boolean holdsWeapon) {
    return new Mob(type, moves, fights, holdsWeapon);
  }

  static final List<Mob> EVERY_MOB =
      List.of(
          mob(EntityType.ALLAY, Moves.AIR, false, true),
          mob(EntityType.ARMADILLO, Moves.GROUND, false, false),
          mob(EntityType.AXOLOTL, Moves.WATER, true, false),
          mob(EntityType.BAT, Moves.AIR, false, false),
          mob(EntityType.BEE, Moves.AIR, true, false),
          mob(EntityType.BLAZE, Moves.AIR, true, false),
          mob(EntityType.BOGGED, Moves.GROUND, true, true),
          mob(EntityType.BREEZE, Moves.GROUND, true, false),
          mob(EntityType.CAMEL, Moves.GROUND, false, false),
          mob(EntityType.CAT, Moves.GROUND, true, false),
          mob(EntityType.CAVE_SPIDER, Moves.GROUND, true, false),
          mob(EntityType.CHICKEN, Moves.GROUND, false, false),
          mob(EntityType.COD, Moves.WATER, false, false),
          mob(EntityType.COW, Moves.GROUND, false, false),
          mob(EntityType.CREEPER, Moves.GROUND, true, false),
          mob(EntityType.DOLPHIN, Moves.WATER, true, false),
          mob(EntityType.DONKEY, Moves.GROUND, false, false),
          mob(EntityType.DROWNED, Moves.GROUND, true, true),
          mob(EntityType.ELDER_GUARDIAN, Moves.WATER, true, false),
          mob(EntityType.ENDER_DRAGON, Moves.AIR, true, false),
          mob(EntityType.ENDERMAN, Moves.GROUND, true, false),
          mob(EntityType.ENDERMITE, Moves.GROUND, true, false),
          mob(EntityType.EVOKER, Moves.GROUND, true, false),
          mob(EntityType.FOX, Moves.GROUND, true, true),
          mob(EntityType.FROG, Moves.GROUND, false, false),
          mob(EntityType.GHAST, Moves.AIR, true, false),
          mob(EntityType.GIANT, Moves.GROUND, false, false),
          mob(EntityType.GLOW_SQUID, Moves.WATER, false, false),
          mob(EntityType.GOAT, Moves.GROUND, false, false),
          mob(EntityType.GUARDIAN, Moves.WATER, true, false),
          mob(EntityType.HOGLIN, Moves.GROUND, true, false),
          mob(EntityType.HORSE, Moves.GROUND, false, false),
          mob(EntityType.HUSK, Moves.GROUND, true, true),
          mob(EntityType.ILLUSIONER, Moves.GROUND, true, true),
          mob(EntityType.IRON_GOLEM, Moves.GROUND, true, false),
          mob(EntityType.LLAMA, Moves.GROUND, true, false),
          mob(EntityType.MAGMA_CUBE, Moves.GROUND, true, false),
          mob(EntityType.MOOSHROOM, Moves.GROUND, false, false),
          mob(EntityType.MULE, Moves.GROUND, false, false),
          mob(EntityType.OCELOT, Moves.GROUND, true, false),
          mob(EntityType.PANDA, Moves.GROUND, true, false),
          mob(EntityType.PARROT, Moves.AIR, false, false),
          mob(EntityType.PHANTOM, Moves.AIR, true, false),
          mob(EntityType.PIG, Moves.GROUND, false, false),
          mob(EntityType.PIGLIN, Moves.GROUND, true, true),
          mob(EntityType.PIGLIN_BRUTE, Moves.GROUND, true, true),
          mob(EntityType.PILLAGER, Moves.GROUND, true, true),
          mob(EntityType.POLAR_BEAR, Moves.GROUND, true, false),
          mob(EntityType.PUFFERFISH, Moves.WATER, false, false),
          mob(EntityType.RABBIT, Moves.GROUND, false, false),
          mob(EntityType.RAVAGER, Moves.GROUND, true, false),
          mob(EntityType.SALMON, Moves.WATER, false, false),
          mob(EntityType.SHEEP, Moves.GROUND, false, false),
          mob(EntityType.SHULKER, Moves.NOWHERE, true, false),
          mob(EntityType.SILVERFISH, Moves.GROUND, true, false),
          mob(EntityType.SKELETON, Moves.GROUND, true, true),
          mob(EntityType.SKELETON_HORSE, Moves.GROUND, false, false),
          mob(EntityType.SLIME, Moves.GROUND, true, false),
          mob(EntityType.SNIFFER, Moves.GROUND, false, false),
          mob(EntityType.SNOW_GOLEM, Moves.GROUND, true, false),
          mob(EntityType.SPIDER, Moves.GROUND, true, false),
          mob(EntityType.SQUID, Moves.WATER, false, false),
          mob(EntityType.STRAY, Moves.GROUND, true, true),
          mob(EntityType.STRIDER, Moves.GROUND, false, false),
          mob(EntityType.TADPOLE, Moves.WATER, false, false),
          mob(EntityType.TRADER_LLAMA, Moves.GROUND, true, false),
          mob(EntityType.TROPICAL_FISH, Moves.WATER, false, false),
          mob(EntityType.TURTLE, Moves.GROUND, false, false),
          mob(EntityType.VEX, Moves.AIR, true, true),
          mob(EntityType.VILLAGER, Moves.GROUND, false, false),
          mob(EntityType.VINDICATOR, Moves.GROUND, true, true),
          mob(EntityType.WANDERING_TRADER, Moves.GROUND, false, false),
          mob(EntityType.WARDEN, Moves.GROUND, true, false),
          mob(EntityType.WITCH, Moves.GROUND, true, false),
          mob(EntityType.WITHER, Moves.AIR, true, false),
          mob(EntityType.WITHER_SKELETON, Moves.GROUND, true, true),
          mob(EntityType.WOLF, Moves.GROUND, true, false),
          mob(EntityType.ZOGLIN, Moves.GROUND, true, false),
          mob(EntityType.ZOMBIE, Moves.GROUND, true, true),
          mob(EntityType.ZOMBIE_HORSE, Moves.GROUND, false, false),
          mob(EntityType.ZOMBIE_VILLAGER, Moves.GROUND, true, true),
          mob(EntityType.ZOMBIFIED_PIGLIN, Moves.GROUND, true, true));

  private MobRoster() {}

  static boolean refusesEveryStatusEffect(final Mob mob) {
    return mob.type() == EntityType.WITHER || mob.type() == EntityType.ENDER_DRAGON;
  }

  static boolean isBoss(final Mob mob) {
    return mob.type() == EntityType.ENDER_DRAGON
        || mob.type() == EntityType.WITHER
        || mob.type() == EntityType.WARDEN;
  }
}
