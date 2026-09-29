package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.social.MagnetPull;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MagnetPullGameTest implements FabricGameTest {
  private static final String BATCH = "magnet-pull";
  private static final BlockPos SHOOTER_STAND = new BlockPos(4, 3, 8);
  private static final BlockPos IMPACT = new BlockPos(14, 3, 8);
  private static final int RADIUS = 4;
  private static final int PULL_TICKS = 60;
  private static final double CLOSE = 2.0;
  private static final double STILL = 0.1;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = PULL_TICKS + 20)
  public void pullsALooseItemAllTheWayToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final ItemEntity item = itemAt(context, IMPACT.north());

    MagnetPull.pull(context.getWorld(), impact(context), shooter, RADIUS);

    context.runAtTick(
        PULL_TICKS,
        () -> {
          context.assertTrue(
              item.isRemoved() || item.getPos().distanceTo(shooter.getPos()) < CLOSE,
              "The item should reach the shooter, ended "
                  + item.getPos().distanceTo(shooter.getPos())
                  + " away");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = PULL_TICKS + 20)
  public void pullsAnExperienceOrbToo(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final ExperienceOrbEntity orb = new ExperienceOrbEntity(context.getWorld(), 0, 0, 0, 5);
    place(context, orb, IMPACT.south());
    context.getWorld().spawnEntity(orb);

    final List<Entity> caught =
        MagnetPull.pull(context.getWorld(), impact(context), shooter, RADIUS);

    context.assertTrue(caught.contains(orb), "The orb is caught");
    context.runAtTick(
        PULL_TICKS,
        () -> {
          context.assertTrue(
              orb.isRemoved() || orb.getPos().distanceTo(shooter.getPos()) < CLOSE,
              "The orb should reach the shooter");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void neverMovesACreatureAVehicleOrAnArrow(TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    final CowEntity cow = context.spawnEntity(EntityType.COW, IMPACT.north());
    cow.setAiDisabled(true);
    final BoatEntity boat = context.spawnEntity(EntityType.BOAT, IMPACT.south());
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());
    arrow.setNoGravity(true);
    arrow.setVelocity(Vec3d.ZERO);

    final List<Entity> caught =
        MagnetPull.pull(context.getWorld(), impact(context), shooter, RADIUS);

    context.assertTrue(caught.isEmpty(), "Nothing pullable, caught " + caught);
    context.runAtTick(
        20,
        () -> {
          for (final Entity untouched : List.of(cow, boat, arrow)) {
            context.assertTrue(
                untouched.getVelocity().horizontalLength() < STILL,
                untouched.getType() + " should not be pulled");
          }
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = PULL_TICKS + 20)
  public void anItemTheShooterCannotPickUpSettlesAtTheirFeetRatherThanFlyingPast(
      TestContext context) {
    final ServerPlayerEntity shooter = shooter(context);
    AgricultureTestSupport.fillInventory(shooter);
    final ItemEntity item = itemAt(context, IMPACT.north());

    MagnetPull.pull(context.getWorld(), impact(context), shooter, RADIUS);

    context.runAtTick(
        PULL_TICKS,
        () -> {
          context.assertFalse(item.isRemoved(), "A full inventory leaves the item on the ground");
          context.assertTrue(
              item.getPos().distanceTo(shooter.getPos()) < CLOSE,
              "at the shooter's feet, not " + item.getPos().distanceTo(shooter.getPos()) + " away");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void withNoShooterNothingIsPulled(TestContext context) {
    final ItemEntity item = itemAt(context, IMPACT.north());

    context.assertTrue(
        MagnetPull.pull(context.getWorld(), impact(context), null, RADIUS).isEmpty(),
        "No shooter, no pull");
    context.assertFalse(MagnetPull.isPulling(context.getWorld(), item.getUuid()), "Not pulled");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aZeroRadiusPullsNothing(TestContext context) {
    itemAt(context, IMPACT);

    context.assertTrue(
        MagnetPull.pull(context.getWorld(), impact(context), shooter(context), 0).isEmpty(),
        "A zero radius");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void leavesAnItemOutsideTheRadius(TestContext context) {
    final ItemEntity far = itemAt(context, IMPACT.east(RADIUS + 2));
    final ItemEntity near = itemAt(context, IMPACT.east(RADIUS - 1));

    final List<Entity> caught =
        MagnetPull.pull(context.getWorld(), impact(context), shooter(context), RADIUS);

    context.assertTrue(caught.contains(near), "The item inside the radius");
    context.assertFalse(caught.contains(far), "The item outside it");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void stopsPullingAnItemThatIsGone(TestContext context) {
    final ItemEntity item = itemAt(context, IMPACT.north());
    MagnetPull.pull(context.getWorld(), impact(context), shooter(context), RADIUS);
    item.discard();

    context.runAtTick(
        2,
        () -> {
          context.assertFalse(
              MagnetPull.isPulling(context.getWorld(), item.getUuid()), "A gone item is dropped");
          context.complete();
        });
  }

  private static ServerPlayerEntity shooter(final TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, SHOOTER_STAND);
    shooter.getInventory().clear();
    return shooter;
  }

  private static ItemEntity itemAt(final TestContext context, final BlockPos at) {
    final ItemEntity item =
        new ItemEntity(context.getWorld(), 0, 0, 0, new ItemStack(Items.DIAMOND, 3), 0, 0, 0);
    place(context, item, at);
    context.getWorld().spawnEntity(item);
    return item;
  }

  private static void place(final TestContext context, final Entity entity, final BlockPos at) {
    final Vec3d pos = Vec3d.ofBottomCenter(context.getAbsolutePos(at));
    entity.setPosition(pos);
  }

  private static Vec3d impact(final TestContext context) {
    return Vec3d.ofBottomCenter(context.getAbsolutePos(IMPACT));
  }
}
