package com.grahambartley.notenougharrows.arrow;

import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import com.grahambartley.notenougharrows.item.BaseArrowItem;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
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
    return register(
        definition, (settings, spawnFactory) -> itemFor(definition, settings, spawnFactory));
  }

  public <E extends BaseArrowEntity> RegisteredArrow<E> register(
      final ArrowDefinition<E> definition,
      final BiFunction<Item.Settings, ArrowEntityFactory, BaseArrowItem> itemFactory) {
    Objects.requireNonNull(itemFactory, "itemFactory");
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
            Registries.ITEM, id, itemFactory.apply(new Item.Settings(), definition.spawnFactory()));

    DispenserBlock.registerProjectileBehavior(item);

    final RegisteredArrow<E> registration = new RegisteredArrow<>(id, entityType, item);
    registrationsByPath.put(definition.path(), registration);
    return registration;
  }

  private static BaseArrowItem itemFor(
      final ArrowDefinition<?> definition,
      final Item.Settings settings,
      final ArrowEntityFactory spawnFactory) {
    return definition
        .palette()
        .<BaseArrowItem>map(palette -> new TintedArrowItem(settings, spawnFactory, palette))
        .orElseGet(() -> new BaseArrowItem(settings, spawnFactory));
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
