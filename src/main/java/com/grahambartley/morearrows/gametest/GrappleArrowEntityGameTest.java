package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.entity.BaseArrowEntity;
import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.grapple.GrappleSession;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class GrappleArrowEntityGameTest implements FabricGameTest {
  private static final String TEMPLATE = "more-arrows:fire_pad";

  private static final BlockPos SHOOTER_STAND = new BlockPos(0, 3, 0);
  private static final BlockPos WALL_BASE = new BlockPos(6, 3, 0);
  private static final int WALL_HEIGHT = 3;
  private static final float EASTWARD_YAW = 270.0f;
  private static final float LEVEL_PITCH = 0.0f;
  private static final float BOW_SPEED = 3.0f;
  private static final int LANDING_TICK = 20;
  private static final float BEYOND_VANILLA_LEASH = 15.0f;
  private static final double FAR_BEYOND_A_LEAD = 24.0;
  private static final int LETTING_GO_TICKS = 15;
  private static final int STRETCH_TICKS = 4;

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void anArrowFiredFromABowGrapplesTheShooterToTheBlockItLandsIn(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleSession session =
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid());

          context.assertTrue(
              session != null,
              "An arrow fired from a bow into a block should grapple the player who fired it");
          final BlockPos wall = context.getAbsolutePos(WALL_BASE);
          context.assertEquals(
              session.anchor().getX(), wall.getX(), "Column the grapple took hold in");
          context.assertEquals(
              session.anchor().getZ(), wall.getZ(), "Row the grapple took hold in");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void anArrowFiredWithNoPlayerBehindItGrapplesNobody(TestContext context) {
    raiseWall(context);
    final PigEntity shooter = context.spawnEntity(EntityType.PIG, SHOOTER_STAND);
    shooter.setAiDisabled(true);
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);
    fireFromBowHeldBy(context, shooter);

    context.runAtTick(
        LANDING_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) == null,
              "An arrow with no player behind it should pull nothing");
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), shooter.getUuid()) == null,
              "An arrow with no player behind it should take no anchor");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void anArrowPullingAPlayerHoldsItsLineToThatPlayer(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);

          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          context.assertTrue(
              landed.isLeashed(),
              "An arrow that is pulling somebody should be leashed to them so vanilla draws the line");
          context.assertTrue(
              landed.getLeashHolder() == shooter,
              "The planted arrow should hold its line to the player it is hauling");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 80)
  public void anArrowWhoseGrappleHasEndedHoldsNoLine(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK, () -> GrappleService.release(context.getWorld(), shooter.getUuid()));
    context.runAtTick(
        LANDING_TICK + 10,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);

          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          context.assertFalse(
              landed.isLeashed(),
              "An arrow whose grapple ended should leave no line hanging off it");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 80)
  public void anArrowThatSurvivesAReloadPicksItsLineBackUp(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final GrappleArrowEntity[] reloadedArrow = {null};
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);
          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          final NbtCompound saved = new NbtCompound();
          landed.writeCustomDataToNbt(saved);

          final GrappleArrowEntity reloaded =
              context.spawnEntity(ModArrows.GRAPPLE_ARROW.entityType(), SHOOTER_STAND.up());
          reloaded.setOwner(shooter);
          reloaded.readCustomDataFromNbt(saved);

          context.assertFalse(
              reloaded.isLeashed(),
              "A freshly loaded arrow holds no line until the server says so");
          reloadedArrow[0] = reloaded;
        });
    context.runAtTick(
        LANDING_TICK + 5,
        () -> {
          final GrappleArrowEntity reloaded = reloadedArrow[0];
          context.assertTrue(reloaded != null, "The reloaded arrow should have been spawned");
          context.assertTrue(
              reloaded.isLeashed(),
              "An arrow that remembered its anchor should take its line back up");
          context.assertTrue(
              reloaded.getLeashHolder() == shooter,
              "A reloaded arrow should hold its line to the player still being pulled");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void anArrowThatRememberedNoAnchorNeverTakesUpALine(TestContext context) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    final GrappleArrowEntity reloaded =
        context.spawnEntity(ModArrows.GRAPPLE_ARROW.entityType(), SHOOTER_STAND.up());
    reloaded.setOwner(shooter);
    reloaded.readCustomDataFromNbt(new NbtCompound());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          context.assertFalse(
              reloaded.isLeashed(),
              "An arrow with no anchor behind it should never take up a line");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void aGrappleLineIsNeverBrokenByVanillaLeashDistance(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);
          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          context.assertFalse(
              landed.beforeLeashTick(shooter, BEYOND_VANILLA_LEASH),
              "A grapple line stretched past a lead's limit should not be handed to vanilla's"
                  + " leash tick, which would snap it and drop a lead");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 80)
  public void aGrappleLineWhoseHolderDiesNeverLeavesALeadBehind(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);
          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          context.assertTrue(landed.isLeashed(), "The arrow should be holding its line");
          shooter.kill();
        });
    context.runAtTick(
        LANDING_TICK + LETTING_GO_TICKS,
        () -> {
          assertNoLeadWasDropped(context);
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 80)
  public void aGrappleLineStretchedPastALeadsReachIsNeitherBrokenNorFelt(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.GRAPPLE_ARROW.item());

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);
          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          landed.setVelocity(Vec3d.ZERO);
          MockPlayerSupport.moveTo(
              context,
              shooter,
              Vec3d.ofBottomCenter(SHOOTER_STAND).add(0.0, FAR_BEYOND_A_LEAD, 0.0));
        });
    context.runAtTick(
        LANDING_TICK + STRETCH_TICKS,
        () -> {
          final GrappleArrowEntity landed = plantedArrow(context);
          context.assertTrue(landed != null, "The fired arrow should still be in the world");
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) != null,
              "The grapple should still be pulling when its line is checked");
          context.assertTrue(
              landed.isLeashed(),
              "A grapple line stretched past a lead's reach should not be snapped by vanilla");
          context.assertTrue(
              landed.getVelocity().lengthSquared() == 0.0,
              "A planted arrow should never be tugged about by leash elasticity, velocity was "
                  + landed.getVelocity());
          assertNoLeadWasDropped(context);
          context.complete();
        });
  }

  private static void assertNoLeadWasDropped(final TestContext context) {
    context.assertTrue(
        context
            .getWorld()
            .getEntitiesByClass(
                ItemEntity.class,
                context.getTestBox(),
                dropped -> dropped.getStack().isOf(Items.LEAD))
            .isEmpty(),
        "A grapple line is not a lead and must never drop one");
  }

  private static GrappleArrowEntity plantedArrow(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(GrappleArrowEntity.class, context.getTestBox(), arrow -> true)
        .stream()
        .findFirst()
        .orElse(null);
  }

  private static void raiseWall(final TestContext context) {
    for (int height = 0; height < WALL_HEIGHT; height++) {
      context.setBlockState(WALL_BASE.up(height), Blocks.STONE);
    }
  }

  private static void fireFromBowHeldBy(final TestContext context, final PigEntity shooter) {
    final BaseArrowEntity arrow =
        (BaseArrowEntity)
            ModArrows.GRAPPLE_ARROW
                .item()
                .createArrow(
                    context.getWorld(),
                    new ItemStack(ModArrows.GRAPPLE_ARROW.item()),
                    shooter,
                    new ItemStack(Items.BOW));
    arrow.setVelocity(shooter, shooter.getPitch(), shooter.getYaw(), 0.0f, BOW_SPEED, 0.0f);
    context.getWorld().spawnEntity(arrow);
  }
}
