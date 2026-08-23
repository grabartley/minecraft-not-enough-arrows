package com.grahambartley.morearrows.arrow;

import com.grahambartley.morearrows.entity.BaseArrowEntity;
import com.grahambartley.morearrows.item.BaseArrowItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ArrowRegistrar {
  private final ArrowCatalog catalog = new ArrowCatalog();
  private final Map<String, RegisteredArrow<?>> registrationsByPath = new HashMap<>();

  public <E extends BaseArrowEntity> RegisteredArrow<E> register(
      final ArrowDefinition<E> definition) {
    catalog.add(definition);

    final Identifier id = definition.id();
    final EntityType<E> entityType =
        Registry.register(
            Registries.ENTITY_TYPE,
            id,
            EntityType.Builder.create(definition.entityFactory(), SpawnGroup.MISC)
                .dimensions(definition.width(), definition.height())
                .maxTrackingRange(definition.maxTrackingRange())
                .trackingTickInterval(definition.trackingTickInterval())
                .build(id.toString()));
    final BaseArrowItem item =
        Registry.register(
            Registries.ITEM, id, new BaseArrowItem(new Item.Settings(), definition.spawnFactory()));

    DispenserBlock.registerProjectileBehavior(item);

    final RegisteredArrow<E> registration = new RegisteredArrow<>(id, entityType, item);
    registrationsByPath.put(definition.path(), registration);
    return registration;
  }

  public ArrowCatalog catalog() {
    return catalog;
  }

  public List<RegisteredArrow<?>> registrations() {
    return catalog.definitions().stream()
        .<RegisteredArrow<?>>map(definition -> registrationsByPath.get(definition.path()))
        .filter(Objects::nonNull)
        .toList();
  }
}
