package com.grahambartley.morearrows.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class UtilityArrowTestSupport {
  static final String TEMPLATE = "more-arrows:fire_pad";
  static final BlockPos SHOOTER_STAND = new BlockPos(1, 2, 3);
  static final BlockPos BACKSTOP = new BlockPos(6, 3, 3);
  static final BlockPos IMPACT_FACE = new BlockPos(5, 3, 3);
  static final int LANDING_TICK = 15;

  private UtilityArrowTestSupport() {}

  static void raiseBackstop(final TestContext context) {
    context.setBlockState(BACKSTOP, Blocks.STONE);
    context.setBlockState(BACKSTOP.up(), Blocks.STONE);
    context.setBlockState(BACKSTOP.down(), Blocks.STONE);
  }

  static ArmorStandEntity standOnPedestalAt(final TestContext context, final BlockPos relativePos) {
    context.setBlockState(relativePos.down(), Blocks.STONE);
    final ArmorStandEntity stand = context.spawnEntity(EntityType.ARMOR_STAND, relativePos);
    stand.setVelocity(Vec3d.ZERO);
    return stand;
  }

  static CowEntity liveTargetOnPedestalAt(final TestContext context, final BlockPos relativePos) {
    context.setBlockState(relativePos.down(), Blocks.STONE);
    final CowEntity target = context.spawnEntity(EntityType.COW, relativePos);
    target.setAiDisabled(true);
    target.setVelocity(Vec3d.ZERO);
    return target;
  }
}
