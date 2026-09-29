package com.grahambartley.notenougharrows.arrow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class ArrowRecipeAudit {

  public record ArrowRecipe(
      Identifier recipe, Identifier result, Set<Identifier> base, Set<Identifier> centre) {

    public ArrowRecipe {
      Objects.requireNonNull(recipe, "recipe");
      Objects.requireNonNull(result, "result");
      base = Set.copyOf(Objects.requireNonNull(base, "base"));
      centre = Set.copyOf(Objects.requireNonNull(centre, "centre"));
    }

    private boolean collidesWith(final ArrowRecipe other) {
      return overlap(base, other.base) && overlap(centre, other.centre);
    }

    private static boolean overlap(final Set<Identifier> first, final Set<Identifier> second) {
      return first.stream().anyMatch(second::contains);
    }
  }

  public record Collision(ArrowRecipe first, ArrowRecipe second) {

    public Collision {
      Objects.requireNonNull(first, "first");
      Objects.requireNonNull(second, "second");
    }

    @Override
    public String toString() {
      return first.result()
          + " ("
          + first.recipe()
          + ") and "
          + second.result()
          + " ("
          + second.recipe()
          + ") share a centre over the same base";
    }
  }

  private ArrowRecipeAudit() {}

  public static List<Collision> sharedCentres(final Collection<ArrowRecipe> recipes) {
    Objects.requireNonNull(recipes, "recipes");

    final List<ArrowRecipe> seen = new ArrayList<>(recipes.size());
    final List<Collision> collisions = new ArrayList<>();
    for (final ArrowRecipe recipe : recipes) {
      seen.stream()
          .filter(earlier -> earlier.collidesWith(recipe))
          .forEach(earlier -> collisions.add(new Collision(earlier, recipe)));
      seen.add(recipe);
    }
    return List.copyOf(collisions);
  }
}
