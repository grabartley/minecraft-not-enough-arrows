package com.grahambartley.notenougharrows.world;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public final class SpawnedDrops {

  private SpawnedDrops() {}

  public static List<ItemEntity> during(
      final ServerWorld world, final Box around, final Runnable action) {
    final Set<ItemEntity> before = Collections.newSetFromMap(new IdentityHashMap<>());
    before.addAll(itemsIn(world, around));
    action.run();
    return itemsIn(world, around).stream().filter(item -> !before.contains(item)).toList();
  }

  private static List<ItemEntity> itemsIn(final ServerWorld world, final Box around) {
    return world.getEntitiesByClass(ItemEntity.class, around, ItemEntity::isAlive);
  }
}
