package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.BoomerangArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BoomerangArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "boomerang-arrow";
  private static final String DISABLED_BATCH = "boomerang-arrow-disabled";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int A_QUIVER = 8;
  private static final int SETTLED = 60;
  private static final int LIMIT = 80;

  private static final Item BOOMERANG = ChaosArrows.BOOMERANG_ARROW.item();

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchTheBoomerangOffBeforeBatch(ServerWorld world) {
    ChaosTestSupport.useChaos(chaos -> chaos.withBoomerang(chaos.boomerang().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aBoomerangArrowComesBackIntoTheShootersInventory(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter = fireInSurvival(context);

    final boolean[] turned = {false};
    onceReturning(context, () -> turned[0] = true);
    context.runAtTick(
        SETTLED,
        () -> {
          context.assertTrue(turned[0], "It should have turned back");
          context.assertEquals(A_QUIVER, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
          context.assertEquals(0, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class) == null,
              "The boomerang should be home");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aBoomerangArrowThatHitsACreatureHurtsItAndStillComesBack(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    final ServerPlayerEntity shooter = fireInSurvival(context);

    context.runAtTick(
        SETTLED,
        () -> {
          context.assertTrue(cow.getHealth() < health, "The cow should be hurt");
          context.assertEquals(A_QUIVER, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
          context.assertEquals(0, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aBoomerangArrowDropsAtTheFeetOfAShooterWithAFullInventory(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter = fireInSurvival(context);
    context.runAtTick(2, () -> AgricultureTestSupport.fillInventory(shooter));

    context.runAtTick(
        SETTLED,
        () -> {
          context.assertEquals(1, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
          context.assertEquals(0, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
          context.assertTrue(
              nearestDropTo(context, shooter) < 2.0, "It should drop at the shooter's feet");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aBoomerangArrowWhoseShooterDiedMidFlightDropsExactlyOnce(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter = fireInSurvival(context);
    onceReturning(context, shooter::kill);

    context.runAtTick(
        SETTLED,
        () -> {
          context.assertEquals(
              A_QUIVER,
              ChaosTestSupport.droppedNearby(context, BOOMERANG)
                  + ChaosTestSupport.countOf(shooter, BOOMERANG),
              "Every boomerang arrow is accounted for exactly once");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class) == null,
              "The boomerang should have come down");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aBoomerangArrowWhoseShooterLeftMidFlightDropsWhereItIs(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter = fireInSurvival(context);
    onceReturning(context, () -> shooter.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER));

    context.runAtTick(
        SETTLED,
        () -> {
          context.assertEquals(1, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
          context.assertEquals(
              A_QUIVER - 1, ChaosTestSupport.countOf(shooter, BOOMERANG), "Left with them");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aCreativeShootersBoomerangReturnsWithoutMakingAnArrow(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, new ItemStack(BOOMERANG, A_QUIVER));

    context.runAtTick(
        SETTLED,
        () -> {
          context.assertEquals(A_QUIVER, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
          context.assertEquals(0, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class) == null,
              "The boomerang should be gone");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = LIMIT)
  public void aReturningBoomerangKeepsReturningAfterASaveAndLoad(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    fireInSurvival(context);

    onceReturning(
        context,
        () -> {
          final BoomerangArrowEntity flying =
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class);
          final BoomerangArrowEntity reloaded =
              ChaosArrows.BOOMERANG_ARROW.entityType().create(context.getWorld());
          reloaded.readNbt(flying.writeNbt(new NbtCompound()));

          context.assertTrue(reloaded.isReturning(), "It should still be on its way back");
          context.assertTrue(reloaded.isNoClip(), "and still pass through what is in the way");
          context.assertTrue(reloaded.hasNoGravity(), "without falling");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedBoomerangArrowHasNobodyToReturnToAndEmbeds(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(context, TerrainArrowTestSupport.DISPENSER_STAND, BOOMERANG);

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final BoomerangArrowEntity arrow =
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class);
          context.assertTrue(arrow != null && !arrow.isReturning(), "It should stay in the wall");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledBoomerangArrowEmbedsAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    fireInSurvival(context);

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final BoomerangArrowEntity arrow =
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class);
          context.assertTrue(arrow != null && !arrow.isReturning(), "It should stay in the wall");
          context.complete();
        });
  }

  private static ServerPlayerEntity fireInSurvival(final TestContext context) {
    final ServerPlayerEntity shooter =
        ChaosTestSupport.survivalPlayerAt(context, FiringRangeSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, new ItemStack(BOOMERANG, A_QUIVER));
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);
    return shooter;
  }

  private static void onceReturning(final TestContext context, final Runnable action) {
    final boolean[] done = {false};
    context.runAtEveryTick(
        () -> {
          final BoomerangArrowEntity arrow =
              FiringRangeSupport.firedArrow(context, BoomerangArrowEntity.class);
          if (!done[0] && arrow != null && arrow.isReturning()) {
            done[0] = true;
            action.run();
          }
        });
  }

  private static double nearestDropTo(final TestContext context, final ServerPlayerEntity shooter) {
    return context
        .getWorld()
        .getEntitiesByClass(
            ItemEntity.class, context.getTestBox(), drop -> drop.getStack().isOf(BOOMERANG))
        .stream()
        .mapToDouble(drop -> drop.getPos().subtract(shooter.getPos()).horizontalLength())
        .min()
        .orElse(Double.MAX_VALUE);
  }
}
