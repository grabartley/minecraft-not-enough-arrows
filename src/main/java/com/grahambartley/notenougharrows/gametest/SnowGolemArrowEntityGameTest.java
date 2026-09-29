package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.SnowGolemArrowEntity;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class SnowGolemArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "snow-golem-arrow";
  private static final String SHORT_LIFETIME_BATCH = "snow-golem-arrow-short-lifetime";
  private static final int SHORT_LIFETIME = 20;
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos CEILING = new BlockPos(8, 8, 8);

  @BeforeBatch(batchId = SHORT_LIFETIME_BATCH)
  public void shortenTheLifetimeBeforeBatch(ServerWorld world) {
    SocialTestSupport.useSocial(
        social -> social.withSnowGolem(social.snowGolem().withLifetimeTicks(SHORT_LIFETIME)));
  }

  @AfterBatch(batchId = SHORT_LIFETIME_BATCH)
  public void restoreDefaultsAfterBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aSnowGolemArrowBuildsOneGolemInFrontOfTheWallAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    fire(context);

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(1, golems(context).size(), "Golems built");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, SnowGolemArrowEntity.class) == null,
              "A snow golem arrow that built its golem is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void withNoRoomForAGolemNothingIsBuiltAndTheArrowCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE.up(), Blocks.STONE);
    fire(context);

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(golems(context).isEmpty(), "No golem where one could not stand");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, SnowGolemArrowEntity.class) != null,
              "The refused arrow stays in the wall to be picked up");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aSnowGolemArrowBuildsItsGolemAtACreatureWithoutHurtingIt(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    fire(context);

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(1, golems(context).size(), "Golems built");
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          context.complete();
        });
  }

  @GameTest(
      templateName = MobArena.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void anArrowThatHitsACeilingBuildsTheGolemBeneathIt(TestContext context) {
    context.setBlockState(CEILING, Blocks.STONE);
    final Vec3d start = context.getAbsolute(Vec3d.ofCenter(CEILING.down(4)));
    final SnowGolemArrowEntity arrow =
        new SnowGolemArrowEntity(
            SocialArrows.SNOW_GOLEM_ARROW.entityType(),
            context.getWorld(),
            start.x,
            start.y,
            start.z,
            new ItemStack(SocialArrows.SNOW_GOLEM_ARROW.item()),
            null);
    arrow.setVelocity(0.0, 1.5, 0.0);
    context.getWorld().spawnEntity(arrow);

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(1, golems(context).size(), "Golems built under the ceiling");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aDispensedSnowGolemArrowBuildsAGolemToo(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, SocialArrows.SNOW_GOLEM_ARROW.item());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(1, golems(context).size(), "Golems built");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = SHORT_LIFETIME_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void theGolemMeltsWhenTheConfiguredLifetimeEnds(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    fire(context);

    context.runAtTick(
        SHORT_LIFETIME / 2,
        () -> context.assertEquals(1, golems(context).size(), "The golem stands at first"));
    context.runAtTick(
        SocialTestSupport.SETTLED_TICK + SHORT_LIFETIME,
        () -> {
          context.assertTrue(golems(context).isEmpty(), "No golem outlives its lifetime");
          context.complete();
        });
  }

  private static void fire(final TestContext context) {
    SocialTestSupport.fireEast(context, new ItemStack(SocialArrows.SNOW_GOLEM_ARROW.item(), 8));
  }

  private static List<SnowGolemEntity> golems(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(SnowGolemEntity.class, context.getTestBox(), SnowGolemEntity::isAlive);
  }
}
