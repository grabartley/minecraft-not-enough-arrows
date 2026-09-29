package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.disguise.DisguiseService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class PolymorphArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "polymorph-arrow";
  private static final String DISABLED_BATCH = "polymorph-arrow-disabled";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchThePolymorphOffBeforeBatch(ServerWorld world) {
    ChaosTestSupport.useChaos(chaos -> chaos.withPolymorph(chaos.polymorph().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPolymorphArrowDisguisesAHostileMobWithoutHurtingIt(TestContext context) {
    final MobEntity zombie = target(context, EntityType.ZOMBIE);
    final float health = zombie.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.POLYMORPH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(DisguiseService.isDisguised(zombie), "The zombie is disguised");
          context.assertEquals(health, zombie.getHealth(), "The zombie's health");
          context.assertTrue(
              FiringRangeSupport.arrowWasSpent(context), "The polymorph arrow is spent");
          DisguiseService.revert(context.getWorld(), zombie);
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPolymorphArrowDoesNothingAtAllToAVillager(TestContext context) {
    assertUntouched(context, target(context, EntityType.VILLAGER));
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPolymorphArrowDoesNothingAtAllToATamedWolf(TestContext context) {
    final WolfEntity wolf = (WolfEntity) target(context, EntityType.WOLF);
    wolf.setOwner(context.createMockPlayer(GameMode.SURVIVAL));
    assertUntouched(context, wolf);
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPolymorphArrowDoesNothingAtAllToABoss(TestContext context) {
    assertUntouched(context, target(context, EntityType.WITHER));
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPolymorphArrowDoesNothingAtAllToAPlayer(TestContext context) {
    context.setBlockState(TARGET_STAND.down(), Blocks.STONE);
    final ServerPlayerEntity struck = ChaosTestSupport.survivalPlayerAt(context, TARGET_STAND);
    assertUntouched(context, struck);
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledPolymorphArrowHitsLikeAnArrowAndChangesNothing(TestContext context) {
    final MobEntity zombie = target(context, EntityType.ZOMBIE);
    final float health = zombie.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.POLYMORPH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertFalse(DisguiseService.isDisguised(zombie), "The zombie is unchanged");
          context.assertTrue(zombie.getHealth() < health, "A switched off arrow hurts");
          context.complete();
        });
  }

  private static void assertUntouched(final TestContext context, final LivingEntity struck) {
    final float health = struck.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.POLYMORPH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertFalse(DisguiseService.isDisguised(struck), "Nothing is disguised");
          context.assertEquals(health, struck.getHealth(), "Health");
          context.complete();
        });
  }

  private static MobEntity target(
      final TestContext context, final EntityType<? extends MobEntity> type) {
    context.setBlockState(TARGET_STAND.down(), Blocks.STONE);
    final MobEntity mob = context.spawnMob(type, TARGET_STAND);
    mob.setAiDisabled(true);
    mob.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    mob.setVelocity(Vec3d.ZERO);
    return mob;
  }
}
