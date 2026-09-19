package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.config.StatusArrowConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RustArrowEntity extends StatusArrowEntity {
  public RustArrowEntity(
      final EntityType<? extends RustArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RustArrowEntity(
      final EntityType<? extends RustArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected RegistryEntry<StatusEffect> effect() {
    return StatusEffects.MINING_FATIGUE;
  }

  @Override
  protected int durationTicks(final StatusArrowConfig status) {
    return status.rustDurationTicks();
  }
}
