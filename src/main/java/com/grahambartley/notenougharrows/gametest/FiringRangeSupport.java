package com.grahambartley.notenougharrows.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class FiringRangeSupport {
  static final String TEMPLATE = "not-enough-arrows:fire_pad";
  static final BlockPos SHOOTER_STAND = new BlockPos(1, 2, 3);
  static final BlockPos BACKSTOP = new BlockPos(6, 3, 3);
  static final BlockPos IMPACT_FACE = new BlockPos(5, 3, 3);
  static final int LANDING_TICK = 15;
  static final long DISPENSER_TRIGGER_TICKS = 2L;

  private FiringRangeSupport() {}

  static <E extends Entity> E firedArrow(final TestContext context, final Class<E> type) {
    return context.getWorld().getEntitiesByClass(type, context.getTestBox(), arrow -> true).stream()
        .findFirst()
        .orElse(null);
  }

  static void dispenseEast(
      final TestContext context, final BlockPos relativePos, final Item arrow) {
    context.setBlockState(
        relativePos,
        Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, Direction.EAST));
    final DispenserBlockEntity dispenser = context.getBlockEntity(relativePos);
    dispenser.setStack(0, new ItemStack(arrow, 1));
    context.putAndRemoveRedstoneBlock(relativePos.up(), DISPENSER_TRIGGER_TICKS);
  }

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
