package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.disguise.DisguiseEligibility;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class DisguiseEligibilityGameTest implements FabricGameTest {
  private static final String BATCH = "disguise-eligibility";
  private static final BlockPos STAND = new BlockPos(8, 3, 8);

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void everyOrdinaryHostileMobCanBeDisguised(TestContext context) {
    for (final EntityType<? extends MobEntity> type :
        List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CREEPER,
            EntityType.SPIDER,
            EntityType.SLIME,
            EntityType.HOGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.GHAST,
            EntityType.ELDER_GUARDIAN)) {
      context.assertTrue(
          DisguiseEligibility.canDisguise(still(context, type)),
          EntityType.getId(type) + " should be disguisable");
    }
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void noBossCanBeDisguised(TestContext context) {
    for (final EntityType<? extends MobEntity> type :
        List.of(EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WARDEN)) {
      context.assertFalse(
          DisguiseEligibility.canDisguise(still(context, type)),
          EntityType.getId(type) + " is a boss and is never disguised");
    }
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void noPlayerVillagerPetOrAnimalCanBeDisguised(TestContext context) {
    final ServerPlayerEntity player = ChaosTestSupport.survivalPlayerAt(context, STAND.west(3));
    final WolfEntity pet = (WolfEntity) still(context, EntityType.WOLF);
    pet.setOwner(player);

    context.assertFalse(DisguiseEligibility.canDisguise(player), "A player");
    context.assertFalse(DisguiseEligibility.canDisguise(pet), "A tamed wolf");
    context.assertFalse(
        DisguiseEligibility.canDisguise(still(context, EntityType.VILLAGER)), "A villager");
    context.assertFalse(
        DisguiseEligibility.canDisguise(still(context, EntityType.WANDERING_TRADER)),
        "A wandering trader");
    final VexEntity summoned = (VexEntity) still(context, EntityType.VEX);
    summoned.setOwner(still(context, EntityType.EVOKER));
    context.assertFalse(DisguiseEligibility.canDisguise(summoned), "A vex its evoker owns");
    context.assertFalse(
        DisguiseEligibility.canDisguise(still(context, EntityType.COW)), "A cow is not hostile");
    context.assertFalse(
        DisguiseEligibility.canDisguise(still(context, EntityType.IRON_GOLEM)),
        "An iron golem is not hostile");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDeadMobCannotBeDisguised(TestContext context) {
    final MobEntity zombie = still(context, EntityType.ZOMBIE);
    zombie.kill();

    context.assertFalse(DisguiseEligibility.canDisguise(zombie), "A dead zombie");
    context.complete();
  }

  private static MobEntity still(
      final TestContext context, final EntityType<? extends MobEntity> type) {
    final MobEntity mob = context.spawnMob(type, STAND);
    mob.setAiDisabled(true);
    return mob;
  }
}
