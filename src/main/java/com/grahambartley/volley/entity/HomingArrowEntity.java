package com.grahambartley.volley.entity;

import com.grahambartley.volley.combat.HomingSteering;
import com.grahambartley.volley.combat.HomingTargets;
import com.grahambartley.volley.config.HomingArrowConfig;
import com.grahambartley.volley.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HomingArrowEntity extends BaseArrowEntity {

  public HomingArrowEntity(
      final EntityType<? extends HomingArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public HomingArrowEntity(
      final EntityType<? extends HomingArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (inGround) {
      return;
    }

    final HomingArrowConfig homing = ServerConfigService.get().combat().homing();
    final Vec3d velocity = getVelocity();
    HomingTargets.ahead(world, this, velocity, homing)
        .ifPresent(
            target -> {
              final Vec3d toTarget = HomingTargets.aimPointOf(target).subtract(getPos());
              setVelocity(HomingSteering.steer(velocity, toTarget, homing.turnRate()));
              velocityModified = true;
            });
  }
}
