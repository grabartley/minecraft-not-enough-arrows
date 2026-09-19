package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.config.StatusArrowConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HasteArrowEntity extends StatusArrowEntity {

  public HasteArrowEntity(
      final EntityType<? extends HasteArrowEntity> entityType, final World world) {
    super(entityType, world);
    setDamage(SUPPORT_DAMAGE);
  }

  public HasteArrowEntity(
      final EntityType<? extends HasteArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
    setDamage(SUPPORT_DAMAGE);
  }

  @Override
  protected RegistryEntry<StatusEffect> effect() {
    return StatusEffects.HASTE;
  }

  @Override
  protected int durationTicks(final StatusArrowConfig status) {
    return status.hasteDurationTicks();
  }
}
