package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RepelArrowEntity extends AreaControlArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 0.6f;

  public RepelArrowEntity(
      final EntityType<? extends RepelArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RepelArrowEntity(
      final EntityType<? extends RepelArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected boolean resolveAt(final ServerWorld world, final Vec3d center) {
    playSound(SoundEvents.BLOCK_SOUL_SAND_BREAK, IMPACT_VOLUME, IMPACT_PITCH);
    ControlHoldService.repel(world, center, ServerConfigService.get().control().targeting());
    return true;
  }
}
