package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TauntArrowEntity extends AreaControlArrowEntity {

  public TauntArrowEntity(
      final EntityType<? extends TauntArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public TauntArrowEntity(
      final EntityType<? extends TauntArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected boolean resolveAt(
      final ServerWorld world, final Vec3d center, @Nullable final LivingEntity struck) {
    if (ControlHoldService.taunt(
            world,
            center,
            struck,
            shooter().orElse(null),
            ServerConfigService.get().control().targeting())
        == 0) {
      return false;
    }
    ModSoundPlayer.playFrom(this, ModSounds.TAUNT_ARROW_IMPACT);
    return true;
  }
}
