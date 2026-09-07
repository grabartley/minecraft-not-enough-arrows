package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.config.UtilityArrowConfig;
import com.grahambartley.morearrows.entity.WindArrowEntity;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.grahambartley.morearrows.wind.WindBurstService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class WindBurstServiceGameTest implements FabricGameTest {
  private static final String BATCH = "wind-burst";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final Vec3d CENTRE = new Vec3d(3.5, 2.5, 3.5);
  private static final BlockPos NEARBY_STAND = new BlockPos(4, 2, 3);
  private static final BlockPos DISTANT_STAND = new BlockPos(6, 2, 6);
  private static final BlockPos GATE = new BlockPos(3, 2, 3);
  private static final Vec3d GATE_FACE = new Vec3d(3.0, 2.5, 3.5);
  private static final float RADIUS = 4.0f;
  private static final float STRENGTH = 2.0f;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityInsideTheRadiusIsPushedAway(TestContext context) {
    final ArmorStandEntity bystander = standAt(context, NEARBY_STAND);

    final List<Entity> displaced = burst(context, null);

    context.assertTrue(displaced.contains(bystander), "A bystander in range should be displaced");
    context.assertTrue(
        bystander.getVelocity().x > 0.0,
        "A bystander east of the burst should be pushed further east");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityBeyondTheRadiusIsLeftAlone(TestContext context) {
    final ArmorStandEntity distant = standAt(context, DISTANT_STAND);

    final List<Entity> displaced = burst(context, null);

    context.assertFalse(
        displaced.contains(distant), "A bystander out of range should not be displaced");
    context.assertEquals(
        distant.getVelocity(), Vec3d.ZERO, "A bystander out of range should not be moved");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theShooterIsNeverPushedByTheirOwnBurst(TestContext context) {
    final ArmorStandEntity shooter = standAt(context, NEARBY_STAND);
    final WindArrowEntity arrow = context.spawnEntity(ModArrows.WIND_ARROW.entityType(), CENTRE);
    arrow.setOwner(shooter);

    final List<Entity> displaced = burst(context, arrow);

    context.assertFalse(
        displaced.contains(shooter), "The shooter should never be displaced by their own burst");
    context.assertEquals(
        shooter.getVelocity(), Vec3d.ZERO, "The shooter should not be moved by their own burst");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aWindActivatedBlockRespondsAsItDoesToAWindCharge(TestContext context) {
    context.setBlockState(GATE, Blocks.OAK_FENCE_GATE);

    WindBurstService.burst(
        context.getWorld(), context.getAbsolute(GATE_FACE), null, RADIUS, STRENGTH);

    context.checkBlockState(
        GATE,
        state -> state.get(Properties.OPEN),
        () -> "A wind burst should open a fence gate the way a wind charge does");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRadiusOfZeroDisplacesNothing(TestContext context) {
    final ArmorStandEntity bystander = standAt(context, NEARBY_STAND);

    final List<Entity> displaced =
        WindBurstService.burst(
            context.getWorld(), context.getAbsolute(CENTRE), null, 0.0f, STRENGTH);

    context.assertTrue(displaced.isEmpty(), "A zero radius should displace nothing");
    context.assertEquals(
        bystander.getVelocity(), Vec3d.ZERO, "A zero radius should leave a bystander still");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesTheBurst(TestContext context) {
    final ArmorStandEntity bystander = standAt(context, NEARBY_STAND);
    final MoreArrowsConfig previous = ServerConfigService.get();
    final List<Entity> displaced;
    try {
      ServerConfigHolder.set(configWithBurst(0.5f, STRENGTH));
      displaced = WindBurstService.burst(context.getWorld(), context.getAbsolute(CENTRE), null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertTrue(
        displaced.isEmpty(),
        "A configured radius too small to reach the bystander displaces nobody");
    context.assertEquals(
        bystander.getVelocity(), Vec3d.ZERO, "The live config radius should decide who moves");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerShooterIsSparedTheBurstsOwnExplosionKnockback(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, new BlockPos(3, 2, 3));
    final WindArrowEntity arrow = context.spawnEntity(ModArrows.WIND_ARROW.entityType(), CENTRE);
    arrow.setOwner(shooter);
    shooter.setVelocity(Vec3d.ZERO);
    shooter.velocityModified = false;

    final List<Entity> displaced = burst(context, arrow);

    context.assertFalse(
        displaced.contains(shooter), "The shooting player should never be displaced");
    context.assertEquals(
        shooter.getVelocity(),
        Vec3d.ZERO,
        "A player standing on their own burst should not be launched by its explosion");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerBystanderIsStillLaunchedByTheBurst(TestContext context) {
    final ServerPlayerEntity bystander = MockPlayerSupport.playerAt(context, new BlockPos(3, 2, 3));
    bystander.setVelocity(Vec3d.ZERO);

    burst(context, null);

    context.assertFalse(
        bystander.getVelocity().equals(Vec3d.ZERO),
        "A player who did not fire the arrow should be shoved by the burst");
    context.complete();
  }

  private static List<Entity> burst(final TestContext context, final Entity source) {
    return WindBurstService.burst(
        context.getWorld(), context.getAbsolute(CENTRE), source, RADIUS, STRENGTH);
  }

  private static ArmorStandEntity standAt(final TestContext context, final BlockPos relativePos) {
    final ArmorStandEntity stand = context.spawnEntity(EntityType.ARMOR_STAND, relativePos);
    stand.setVelocity(Vec3d.ZERO);
    return stand;
  }

  private static MoreArrowsConfig configWithBurst(final float radius, final float strength) {
    final UtilityArrowConfig utility = UtilityArrowConfig.defaults();
    return MoreArrowsConfig.defaults()
        .withUtility(utility.withWindBurstRadius(radius).withWindPushStrength(strength));
  }
}
