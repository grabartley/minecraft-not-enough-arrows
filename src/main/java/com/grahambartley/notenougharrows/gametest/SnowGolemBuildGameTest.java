package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.social.SnowGolemBuild;
import com.grahambartley.notenougharrows.social.SnowGolemMelt;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class SnowGolemBuildGameTest implements FabricGameTest {
  private static final String BATCH = "snow-golem-build";
  private static final BlockPos OPEN_GROUND = new BlockPos(8, 3, 8);
  private static final BlockPos ZOMBIE_STAND = new BlockPos(14, 3, 8);
  private static final int LIFETIME = 1200;
  private static final int NOTICE_TICKS = 100;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void buildsAnOrdinarySnowGolemWithNoPumpkinToShearOff(TestContext context) {
    final SnowGolemEntity golem = build(context).orElseThrow();

    context.assertTrue(golem.isAlive(), "The golem should stand");
    context.assertFalse(
        golem.isShearable(),
        "with no pumpkin, so a twelve-arrow craft cannot shear back twelve pumpkins");
    context.assertFalse(golem.isPersistent(), "and nothing protecting it from despawn rules");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void schedulesTheGolemToMeltAfterItsLifetime(TestContext context) {
    final long now = context.getWorld().getTime();
    final SnowGolemEntity golem = build(context).orElseThrow();

    context.assertEquals(
        now + LIFETIME, SnowGolemMelt.meltsAt(golem).orElseThrow(), "When the golem melts");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesAGolemWithNoRoomToStand(TestContext context) {
    context.setBlockState(OPEN_GROUND.up(), Blocks.STONE);

    context.assertTrue(build(context).isEmpty(), "No golem under a one-block ceiling");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void refusesAGolemForAShooterWhoMayNotBuild(TestContext context) {
    final PlayerEntity visitor = MockPlayerSupport.mortalPlayerAt(context, OPEN_GROUND.west(2));
    visitor.getAbilities().allowModifyWorld = false;

    context.assertFalse(
        SnowGolemBuild.allows(context.getWorld(), at(context, OPEN_GROUND), visitor),
        "A shooter who could not build a golem by hand may not shoot one either");
    context.complete();
  }

  @GameTest(
      templateName = MobArena.TEMPLATE,
      batchId = BATCH,
      tickLimit = NOTICE_TICKS + 20,
      maxAttempts = 3,
      requiredSuccesses = 1)
  public void theBuiltGolemFightsAHostileMobLikeAnyGolem(TestContext context) {
    final SnowGolemEntity golem = build(context).orElseThrow();
    final ZombieEntity zombie = context.spawnEntity(EntityType.ZOMBIE, ZOMBIE_STAND);
    zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    zombie.setPersistent();

    context.runAtTick(
        NOTICE_TICKS,
        () -> {
          context.assertTrue(
              golem.getTarget() == zombie,
              "A golem with its own AI should target the zombie, targeted " + golem.getTarget());
          context.complete();
        });
  }

  private static Optional<SnowGolemEntity> build(final TestContext context) {
    return SnowGolemBuild.build(context.getWorld(), at(context, OPEN_GROUND), null, LIFETIME);
  }

  private static Vec3d at(final TestContext context, final BlockPos relative) {
    return Vec3d.ofBottomCenter(context.getAbsolutePos(relative));
  }
}
