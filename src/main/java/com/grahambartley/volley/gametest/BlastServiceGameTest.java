package com.grahambartley.volley.gametest;

import com.grahambartley.volley.ModArrows;
import com.grahambartley.volley.blast.BlastService;
import com.grahambartley.volley.config.ExplosiveArrowConfig;
import com.grahambartley.volley.config.ExplosiveTierConfig;
import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.entity.ExplosiveArrowEntity;
import com.grahambartley.volley.entity.GunpowderArrowEntity;
import com.grahambartley.volley.fuse.FuseService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BlastServiceGameTest implements FabricGameTest {
  private static final String BATCH = "explosive";
  private static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  private static final BlockPos CENTRE = new BlockPos(3, 4, 3);
  private static final BlockPos NEARBY_BLOCK = new BlockPos(3, 3, 3);
  private static final float POWER = 4.0f;
  private static final int DETONATE_NOW = 0;

  @BeforeBatch(batchId = BATCH)
  public void forgetFusesBeforeBatch(ServerWorld world) {
    FuseService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aBlastLeavesTerrainAloneWhenTerrainDamageIsOff(TestContext context) {
    context.setBlockState(NEARBY_BLOCK, Blocks.DIRT);
    detonateWith(context, configWith(false, false));

    context.expectBlock(Blocks.DIRT, NEARBY_BLOCK);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aBlastBreaksTerrainWhenTerrainDamageIsOn(TestContext context) {
    context.setBlockState(NEARBY_BLOCK, Blocks.DIRT);
    detonateWith(context, configWith(true, false));

    context.expectBlock(Blocks.AIR, NEARBY_BLOCK);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aBlastLeavesEntitiesUnhurtWhenEntityDamageIsOff(TestContext context) {
    final ArmorStandEntity bystander =
        context.spawnEntity(EntityType.ARMOR_STAND, new BlockPos(3, 3, 4));
    final float before = bystander.getHealth();

    detonateWith(context, configWith(false, false));

    context.assertEquals(
        bystander.getHealth(), before, "A blast with entity damage off should hurt nobody");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aDetonatedArrowIsConsumed(TestContext context) {
    detonateWith(context, configWith(false, false));

    context.dontExpectEntity(ModArrows.GUNPOWDER_ARROW.entityType());
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPowerOfZeroDetonatesWithoutAnExplosion(TestContext context) {
    context.setBlockState(NEARBY_BLOCK, Blocks.DIRT);
    detonateWith(context, configWithGunpowderPower(0.0f, true));

    context.expectBlock(Blocks.DIRT, NEARBY_BLOCK);
    context.dontExpectEntity(ModArrows.GUNPOWDER_ARROW.entityType());
    context.complete();
  }

  private static void detonateWith(final TestContext context, final VolleyConfig config) {
    final ExplosiveArrowEntity arrow = spawnArrow(context);
    ConfigOverrides.during(
        config,
        () -> BlastService.arm(context.getWorld(), arrow, arrow.tier(), null, DETONATE_NOW));
  }

  private static GunpowderArrowEntity spawnArrow(final TestContext context) {
    return context.spawnEntity(ModArrows.GUNPOWDER_ARROW.entityType(), CENTRE);
  }

  private static VolleyConfig configWith(
      final boolean damageTerrain, final boolean damageEntities) {
    return VolleyConfig.defaults()
        .withExplosive(
            ExplosiveArrowConfig.defaults()
                .withGunpowder(new ExplosiveTierConfig(0, POWER))
                .withDamageTerrain(damageTerrain)
                .withDamageEntities(damageEntities));
  }

  private static VolleyConfig configWithGunpowderPower(
      final float power, final boolean damageTerrain) {
    return VolleyConfig.defaults()
        .withExplosive(
            ExplosiveArrowConfig.defaults()
                .withGunpowder(new ExplosiveTierConfig(0, power))
                .withDamageTerrain(damageTerrain));
  }
}
