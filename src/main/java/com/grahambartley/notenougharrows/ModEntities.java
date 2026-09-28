package com.grahambartley.notenougharrows;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
  public static final Identifier SHOCK_BOLT_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "shock_bolt");

  public static final EntityType<LightningEntity> SHOCK_BOLT =
      Registry.register(
          Registries.ENTITY_TYPE,
          SHOCK_BOLT_ID,
          EntityType.Builder.create(LightningEntity::new, SpawnGroup.MISC)
              .disableSaving()
              .dimensions(0.0f, 0.0f)
              .maxTrackingRange(16)
              .trackingTickInterval(Integer.MAX_VALUE)
              .build(SHOCK_BOLT_ID.toString()));

  private ModEntities() {}

  public static void register() {
    NotEnoughArrows.LOGGER.info("Registered entity type {}", SHOCK_BOLT_ID);
  }
}
