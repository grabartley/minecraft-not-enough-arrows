package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;

public final class FrostImmunity {
  private static final EquipmentSlot[] WORN =
      new EquipmentSlot[] {
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET,
        EquipmentSlot.BODY
      };

  private FrostImmunity() {}

  public static boolean shrugsOff(final LivingEntity target) {
    if (target.isSpectator() || target.getType().isIn(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
      return true;
    }
    for (final EquipmentSlot slot : WORN) {
      if (target.getEquippedStack(slot).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)) {
        return true;
      }
    }
    return false;
  }

  public static boolean needsHelpToFeelIt(final LivingEntity target) {
    return !shrugsOff(target) && !target.canFreeze();
  }

  public static float freezeDamage(final LivingEntity target) {
    return target.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)
        ? FrostGrip.EXTRA_FREEZE_DAMAGE
        : FrostGrip.FREEZE_DAMAGE;
  }
}
