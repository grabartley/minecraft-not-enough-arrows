package com.grahambartley.notenougharrows.compat.emi;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.compat.info.InfoEntry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class EmiInfoRecipes {
  public static final String ID_PREFIX = "info/";

  private final InfoRecipeFactory factory;

  public EmiInfoRecipes(final InfoRecipeFactory factory) {
    this.factory = Objects.requireNonNull(factory, "factory");
  }

  public static EmiInfoRecipes viaItemRegistry() {
    return new EmiInfoRecipes(EmiInfoRecipes::infoRecipe);
  }

  public static Identifier idFor(final InfoEntry entry) {
    Objects.requireNonNull(entry, "entry");
    return Identifier.of(NotEnoughArrows.MOD_ID, ID_PREFIX + entry.itemIds().getFirst().getPath());
  }

  public List<EmiRecipe> from(final Collection<InfoEntry> entries) {
    Objects.requireNonNull(entries, "entries");
    return entries.stream().map(this::from).toList();
  }

  public EmiRecipe from(final InfoEntry entry) {
    Objects.requireNonNull(entry, "entry");
    return factory.create(entry.itemIds(), entry.texts(), idFor(entry));
  }

  private static EmiRecipe infoRecipe(
      final List<Identifier> itemIds, final List<Text> text, final Identifier id) {
    final List<EmiIngredient> stacks =
        itemIds.stream()
            .<EmiIngredient>map(itemId -> EmiStack.of(Registries.ITEM.get(itemId)))
            .toList();
    return new EmiInfoRecipe(stacks, text, id);
  }

  @FunctionalInterface
  public interface InfoRecipeFactory {
    EmiRecipe create(List<Identifier> itemIds, List<Text> text, Identifier id);
  }
}
