package com.grahambartley.volley.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

  public <R> R map(@Nullable final ServerWorld world, final Function<T, R> read, final R fallback) {
    final T value = in(world);
    return value == null ? fallback : read.apply(value);
  }

  public T of(final ServerWorld world) {
    return values.computeIfAbsent(world.getRegistryKey(), key -> create.get());
  }

  public List<T> all() {
    return List.copyOf(values.values());
  }

  @Nullable
  public T remove(final ServerWorld world) {
    return values.remove(world.getRegistryKey());
  }

  public void clear() {
    values.clear();
  }
}
