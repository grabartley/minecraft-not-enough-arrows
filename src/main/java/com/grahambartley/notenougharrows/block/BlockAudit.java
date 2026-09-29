package com.grahambartley.notenougharrows.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class BlockAudit {

  public record BlockFacts(
      Identifier block,
      boolean hasItemForm,
      boolean craftable,
      boolean dropsItems,
      Set<BlockLifetime> lifetimes,
      boolean standsUnsupported) {

    public BlockFacts {
      Objects.requireNonNull(block, "block");
      lifetimes = Set.copyOf(Objects.requireNonNull(lifetimes, "lifetimes"));
    }
  }

  private BlockAudit() {}

  public static List<String> violations(final Collection<BlockFacts> blocks) {
    Objects.requireNonNull(blocks, "blocks");

    final List<String> violations = new ArrayList<>();
    for (final BlockFacts facts : blocks) {
      if (facts.hasItemForm()) {
        violations.add(facts.block() + " has an item form");
      }
      if (facts.craftable()) {
        violations.add(facts.block() + " has a recipe");
      }
      if (facts.dropsItems()) {
        violations.add(facts.block() + " drops items when broken");
      }
      if (facts.lifetimes().isEmpty()) {
        violations.add(facts.block() + " neither expires nor answers for its own support");
      }
      if (facts.lifetimes().contains(BlockLifetime.FALLS_WITHOUT_SUPPORT)
          && facts.standsUnsupported()) {
        violations.add(facts.block() + " claims to fall without support but stands in open air");
      }
    }
    return List.copyOf(violations);
  }
}
