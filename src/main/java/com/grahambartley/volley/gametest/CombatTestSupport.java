package com.grahambartley.volley.gametest;

import com.grahambartley.volley.entity.BaseArrowEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class CombatTestSupport {
  static final String LONG_RANGE = "volley:combat_range";
  static final BlockPos LONG_RANGE_SHOOTER_STAND = new BlockPos(1, 2, 3);
  static final BlockPos LONG_RANGE_FAR_STAND = new BlockPos(20, 3, 5);
  static final int MID_FLIGHT_TICK = 4;

  private CombatTestSupport() {}

  static <E extends BaseArrowEntity> E driftingFrom(
      final TestContext context,
      final EntityType<E> arrowType,
      final BlockPos relativePos,
      final Vec3d velocity) {
    final E arrow = context.spawnEntity(arrowType, relativePos);
    arrow.setVelocity(velocity);
    return arrow;
  }

  static boolean hasCosmeticLightningLeftFire(final TestContext context) {
    final boolean[] found = {false};
    context.forEachRelativePos(
        pos -> {
          if (context.getBlockState(pos).isOf(Blocks.FIRE)) {
            found[0] = true;
          }
        });
    return found[0];
  }

  static void fireDownTheLongRange(final TestContext context, final net.minecraft.item.Item arrow) {
    MockPlayerSupport.fireEastFromBow(
        context, MockPlayerSupport.playerAt(context, LONG_RANGE_SHOOTER_STAND), arrow);
  }
}
