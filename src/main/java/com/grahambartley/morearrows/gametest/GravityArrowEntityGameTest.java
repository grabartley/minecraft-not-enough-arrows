package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.entity.GravityArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class GravityArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "gravity-arrow";

  @GameTest(templateName = PhysicsArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowFiredFromABowDropsTheBlockItStrikes(TestContext context) {
    fireAt(context, Blocks.STONE);

    context.runAtTick(
        PhysicsArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, PhysicsArrowTestSupport.TARGET_BLOCK);
          context.complete();
        });
  }

  @GameTest(templateName = PhysicsArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void aDroppedBlockRePlacesItselfWhereItLands(TestContext context) {
    fireAt(context, Blocks.STONE);

    context.runAtTick(
        PhysicsArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.STONE, PhysicsArrowTestSupport.LANDING_BLOCK);
          context.complete();
        });
  }

  @GameTest(templateName = PhysicsArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowIsSpentByTheBlockItDrops(TestContext context) {
    fireAt(context, Blocks.STONE);

    context.runAtTick(
        PhysicsArrowTestSupport.SETTLED_TICK,
        () -> {
          context.dontExpectEntity(ModArrows.GRAVITY_ARROW.entityType());
          context.complete();
        });
  }

  @GameTest(templateName = PhysicsArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void anArrowThatCannotMoveWhatItStrikesEmbedsInsteadOfVanishing(TestContext context) {
    fireAt(context, Blocks.BEDROCK);

    context.runAtTick(
        PhysicsArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.BEDROCK, PhysicsArrowTestSupport.TARGET_BLOCK);
          context.assertTrue(
              PhysicsArrowTestSupport.firedArrow(context, GravityArrowEntity.class) != null,
              "An arrow that moved nothing should embed and stay recoverable");
          context.complete();
        });
  }

  private static void fireAt(final TestContext context, final Block target) {
    context.setBlockState(PhysicsArrowTestSupport.LANDING_BLOCK, Blocks.AIR);
    context.setBlockState(PhysicsArrowTestSupport.TARGET_BLOCK, target);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, PhysicsArrowTestSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAVITY_ARROW.item());
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);
  }
}
