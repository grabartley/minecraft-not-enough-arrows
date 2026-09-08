package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.IncendiaryArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.incendiary.IncendiaryService;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class IncendiaryServiceGameTest implements FabricGameTest {
  private static final String BATCH = "incendiary";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final Vec3d CENTRE = new Vec3d(3.5, 3.0, 3.5);
  private static final BlockPos NEARBY_STAND = new BlockPos(4, 3, 3);
  private static final BlockPos DISTANT_STAND = new BlockPos(6, 3, 6);
  private static final int RADIUS = 3;
  private static final int IGNITE_SECONDS = 5;
  private static final int FIRE_PATCH_TICKS = 200;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityInsideTheRadiusCatchesFire(TestContext context) {
    final CowEntity cow = cowAt(context, NEARBY_STAND);

    final List<Entity> burned = ignite(context, RADIUS, IGNITE_SECONDS, false);

    context.assertTrue(burned.contains(cow), "A cow in range should be set alight");
    context.assertTrue(cow.isOnFire(), "A cow in range should be burning");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityBeyondTheRadiusIsLeftAlone(TestContext context) {
    final CowEntity cow = cowAt(context, DISTANT_STAND);

    final List<Entity> burned = ignite(context, RADIUS, IGNITE_SECONDS, false);

    context.assertFalse(burned.contains(cow), "A cow out of range should not be set alight");
    context.assertFalse(cow.isOnFire(), "A cow out of range should not be burning");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void fireIsLaidOnTheSurfaceWhenBlockIgnitionIsOn(TestContext context) {
    ignite(context, RADIUS, IGNITE_SECONDS, true);

    context.expectBlock(Blocks.FIRE, new BlockPos(3, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void noFireIsLaidWhenBlockIgnitionIsOff(TestContext context) {
    final CowEntity cow = cowAt(context, NEARBY_STAND);

    final List<Entity> burned = ignite(context, RADIUS, IGNITE_SECONDS, false);

    context.expectBlock(Blocks.AIR, new BlockPos(3, 3, 3));
    context.assertTrue(
        burned.contains(cow), "Entities should still burn when block ignition is off");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRadiusOfZeroBurnsNothingAtAll(TestContext context) {
    final CowEntity cow = cowAt(context, NEARBY_STAND);

    final List<Entity> burned = ignite(context, 0, IGNITE_SECONDS, true);

    context.assertTrue(burned.isEmpty(), "A zero radius should burn nothing");
    context.assertFalse(cow.isOnFire(), "A zero radius should leave a cow unburnt");
    context.expectBlock(Blocks.AIR, new BlockPos(3, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anIgniteTimeOfZeroLaysFireWithoutBurningEntities(TestContext context) {
    final CowEntity cow = cowAt(context, NEARBY_STAND);

    final List<Entity> burned = ignite(context, RADIUS, 0, true);

    context.assertTrue(burned.isEmpty(), "A zero ignite time should burn no entities");
    context.assertFalse(cow.isOnFire(), "A zero ignite time should leave a cow unburnt");
    context.expectBlock(Blocks.FIRE, new BlockPos(3, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesTheBurn(TestContext context) {
    final CowEntity cow = cowAt(context, NEARBY_STAND);
    final MoreArrowsConfig previous = ServerConfigService.get();
    final List<Entity> burned;
    try {
      ServerConfigHolder.set(
          MoreArrowsConfig.defaults()
              .withExplosive(
                  ExplosiveArrowConfig.defaults()
                      .withIncendiary(
                          IncendiaryArrowConfig.defaults()
                              .withBurnRadius(0)
                              .withIgnitesBlocks(false))));
      burned =
          IncendiaryService.ignite(context.getWorld(), context.getAbsolute(CENTRE), null, null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertTrue(burned.isEmpty(), "A configured radius of zero should burn nothing");
    context.assertFalse(cow.isOnFire(), "A configured radius of zero should leave a cow unburnt");
    context.complete();
  }

  private static List<Entity> ignite(
      final TestContext context,
      final int radius,
      final int igniteSeconds,
      final boolean ignitesBlocks) {
    return IncendiaryService.ignite(
        context.getWorld(),
        context.getAbsolute(CENTRE),
        null,
        null,
        radius,
        igniteSeconds,
        ignitesBlocks,
        FIRE_PATCH_TICKS);
  }

  private static CowEntity cowAt(final TestContext context, final BlockPos relativePos) {
    final CowEntity cow = context.spawnEntity(EntityType.COW, relativePos);
    cow.setAiDisabled(true);
    return cow;
  }
}
