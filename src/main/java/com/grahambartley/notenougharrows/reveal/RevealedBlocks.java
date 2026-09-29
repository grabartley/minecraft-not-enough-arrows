package com.grahambartley.notenougharrows.reveal;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class RevealedBlocks {

  private RevealedBlocks() {}

  public static Predicate<BlockState> of(final List<String> ids) {
    final Set<Block> blocks = resolve(ids);
    return state -> state != null && blocks.contains(state.getBlock());
  }

  static Set<Block> resolve(final List<String> ids) {
    if (ids == null) {
      return Set.of();
    }
    return ids.stream()
        .map(Identifier::tryParse)
        .filter(Objects::nonNull)
        .filter(Registries.BLOCK::containsId)
        .map(Registries.BLOCK::get)
        .collect(Collectors.toUnmodifiableSet());
  }
}
