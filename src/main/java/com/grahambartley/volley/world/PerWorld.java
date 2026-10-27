package com.grahambartley.volley.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class PerWorld<T> {
  private final Map<RegistryKey<World>, T> values = new HashMap<>();
  private final Supplier<T> create;

  public PerWorld(final Supplier<T> create) {
    this.create = create;
  }

  @Nullable
  public T in(@Nullable final ServerWorld world) {
    return world == null ? null : values.get(world.getRegistryKey());
  }

  public T of(final ServerWorld world) {
    return values.computeIfAbsent(world.getRegistryKey(), key -> create.get());
  }

  public Collection<T> all() {
    return values.values();
  }

  public void clear() {
    values.clear();
  }
}
