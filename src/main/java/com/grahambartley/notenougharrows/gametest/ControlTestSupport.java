package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.SmokeCloudService;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ControlTestSupport {

  private ControlTestSupport() {}

  static void forgetEveryHoldAndCloud() {
    ControlHoldService.forget();
    SmokeCloudService.forget();
  }

  static ZombieEntity stillZombieAt(final TestContext context, final BlockPos relativePos) {
    context.setBlockState(relativePos.down(), Blocks.STONE);
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, relativePos);
    zombie.setAiDisabled(true);
    zombie.setVelocity(Vec3d.ZERO);
    return zombie;
  }

  static ZombieEntity engagedZombieAt(
      final TestContext context, final BlockPos relativePos, final BlockPos preyPos) {
    final ZombieEntity zombie = stillZombieAt(context, relativePos);
    zombie.setTarget(FiringRangeSupport.liveTargetOnPedestalAt(context, preyPos));
    return zombie;
  }

  static CowEntity stillCowAt(final TestContext context, final BlockPos relativePos) {
    return FiringRangeSupport.liveTargetOnPedestalAt(context, relativePos);
  }
}
