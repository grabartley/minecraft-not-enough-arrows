package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.ChargingPlayerPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class MobSteering {
  public static final float SQUID_STROKE = 0.2f;
  public static final double OFF_COURSE = 3.0;
  public static final double RABBIT_HOP_SPEED = 2.2;
  public static final double FINAL_APPROACH = 3.0;

  private MobSteering() {}

  public static boolean moveTo(final MobEntity mob, final Vec3d destination, final double speed) {
    rouse(mob);
    if (mob instanceof SlimeEntity slime
        && slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl hop) {
      SlimeSteering.steer(slime.getUuid(), slime.getWorld().getTime());
      hop.look(yawToward(slime.getPos(), destination), true);
      hop.move(speed);
      return true;
    }
    if (mob instanceof PhantomEntity phantom) {
      phantom.targetPosition = destination;
      return true;
    }
    if (mob instanceof BatEntity bat) {
      BatFlight.steer(bat, openAirToward(bat, destination));
      return true;
    }
    if (mob instanceof SquidEntity squid) {
      final Vec3d stroke = destination.subtract(squid.getPos()).normalize().multiply(SQUID_STROKE);
      squid.setSwimmingVector((float) stroke.x, (float) stroke.y, (float) stroke.z);
      return true;
    }
    if (mob instanceof EnderDragonEntity dragon) {
      chargeToward(dragon, destination);
      return true;
    }
    if (mob instanceof GhastEntity ghast) {
      final Vec3d waypoint =
          GhastCourse.plan(
                  ghast.getPos(), destination, ghast.getHeight(), GhastCourse.fitsAt(ghast))
              .orElse(destination);
      ghast.getMoveControl().moveTo(waypoint.x, waypoint.y, waypoint.z, speed);
      return true;
    }
    if (mob instanceof VexEntity) {
      mob.getMoveControl().moveTo(destination.x, destination.y, destination.z, speed);
      return true;
    }
    if (mob.getNavigation()
        .startMovingTo(destination.x, destination.y, destination.z, paceFor(mob, speed))) {
      skipNodesUnderfoot(mob);
      turnSlowSwimmerToward(mob, destination);
      walkTheBrainTo(mob, destination, speed);
      keepPace(mob, speed);
      return true;
    }
    if (movesThroughOpenSpace(mob)) {
      mob.getMoveControl().moveTo(destination.x, destination.y, destination.z, speed);
      return true;
    }
    if (mob.getNavigation() instanceof SpiderNavigation climbing
        && ClimbingGrip.shouldLetGo(mob.isClimbing(), mob.getY(), destination.y)) {
      climbing.targetPos = null;
    }
    return false;
  }

  private static void skipNodesUnderfoot(final MobEntity mob) {
    final Path path = mob.getNavigation().getCurrentPath();
    if (path == null || plansItsOwnWalks(mob)) {
      return;
    }
    final int ahead =
        PathStart.firstNodeAhead(
            path::getNodePos, path.getCurrentNodeIndex(), path.getLength(), mob.getBoundingBox());
    while (path.getCurrentNodeIndex() < ahead) {
      path.next();
    }
  }

  private static void turnSlowSwimmerToward(final MobEntity mob, final Vec3d destination) {
    if (mob.getMoveControl() instanceof AquaticMoveControl && mob.isTouchingWater()) {
      final float yaw = yawToward(mob.getPos(), destination);
      mob.setYaw(yaw);
      mob.setBodyYaw(yaw);
      mob.setHeadYaw(yaw);
    }
  }

  private static boolean movesThroughOpenSpace(final MobEntity mob) {
    return mob instanceof BlazeEntity
        || mob.getNavigation() instanceof BirdNavigation
        || mob.getNavigation() instanceof SwimNavigation;
  }

  public static boolean canPlanFromHere(final MobEntity mob) {
    return mob.isAiDisabled()
        || mob.isOnGround()
        || mob.isTouchingWater()
        || mob.isInLava()
        || mob.hasVehicle()
        || steersItself(mob)
        || movesThroughOpenSpace(mob);
  }

  public static boolean isUnderway(final MobEntity mob) {
    return steersItself(mob) || !mob.getNavigation().isIdle();
  }

  public static void halt(final MobEntity mob) {
    mob.getNavigation().stop();
    if (plansItsOwnWalks(mob)) {
      mob.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }
    if (mob instanceof EnderDragonEntity dragon
        && dragon.getPhaseManager().getCurrent().getType() != PhaseType.HOVER) {
      dragon.getPhaseManager().setPhase(PhaseType.HOVER);
    }
  }

  public static boolean hasWanderedOffCourse(
      final MobEntity mob, @Nullable final Vec3d destination) {
    if (destination == null || steersItself(mob)) {
      return false;
    }
    final BlockPos heading = mob.getNavigation().getTargetPos();
    return heading == null || heading.getSquaredDistance(destination) > OFF_COURSE * OFF_COURSE;
  }

  public static void closeTheLastGap(
      final MobEntity mob, final Vec3d destination, final double speed) {
    if (!steersItself(mob)
        && mob.getNavigation().isIdle()
        && mob.squaredDistanceTo(destination) < FINAL_APPROACH * FINAL_APPROACH) {
      mob.getMoveControl().moveTo(destination.x, destination.y, destination.z, speed);
    }
  }

  public static void keepPace(final MobEntity mob, final double speed) {
    if (mob instanceof RabbitEntity rabbit && !mob.getNavigation().isIdle()) {
      rabbit.setSpeed(paceFor(mob, speed));
    }
  }

  private static double paceFor(final MobEntity mob, final double speed) {
    return mob instanceof RabbitEntity ? Math.max(speed, RABBIT_HOP_SPEED) : speed;
  }

  public static void rouse(final MobEntity mob) {
    if (mob instanceof FoxEntity fox) {
      fox.stopActions();
    }
    if (mob instanceof PandaEntity panda) {
      panda.setSitting(false);
      panda.setLyingOnBack(false);
      panda.setPlaying(false);
    }
  }

  private static BlockPos openAirToward(final MobEntity mob, final Vec3d destination) {
    final Vec3d from = mob.getPos();
    final Vec3d step = destination.subtract(from);
    final double length = step.length();
    BlockPos open = mob.getBlockPos();
    for (double reached = 1.0; reached <= length; reached += 1.0) {
      final BlockPos next = BlockPos.ofFloored(from.add(step.multiply(reached / length)));
      if (!mob.getWorld().isAir(next)) {
        break;
      }
      open = next;
    }
    return open;
  }

  private static void walkTheBrainTo(
      final MobEntity mob, final Vec3d destination, final double speed) {
    if (plansItsOwnWalks(mob)) {
      mob.getBrain()
          .remember(MemoryModuleType.WALK_TARGET, new WalkTarget(destination, (float) speed, 0));
    }
  }

  private static boolean plansItsOwnWalks(final MobEntity mob) {
    return mob.getBrain()
        .isMemoryInState(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED);
  }

  public static boolean fliesAtRandom(final MobEntity mob) {
    return mob instanceof BatEntity;
  }

  public static boolean steersItself(final MobEntity mob) {
    return mob instanceof SlimeEntity
        || mob instanceof PhantomEntity
        || mob instanceof BatEntity
        || mob instanceof SquidEntity
        || mob instanceof EnderDragonEntity
        || mob instanceof GhastEntity
        || mob instanceof VexEntity;
  }

  public static float yawToward(final Vec3d from, final Vec3d to) {
    return (float) (MathHelper.atan2(to.z - from.z, to.x - from.x) * MathHelper.DEGREES_PER_RADIAN)
        - 90.0f;
  }

  private static void chargeToward(final EnderDragonEntity dragon, final Vec3d destination) {
    final PhaseType<ChargingPlayerPhase> charging = PhaseType.CHARGING_PLAYER;
    if (dragon.getPhaseManager().getCurrent().getType() != charging) {
      dragon.getPhaseManager().setPhase(charging);
    }
    dragon.getPhaseManager().create(charging).setPathTarget(destination);
  }
}
