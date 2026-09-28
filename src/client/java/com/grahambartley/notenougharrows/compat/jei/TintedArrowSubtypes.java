package com.grahambartley.notenougharrows.compat.jei;

import com.grahambartley.notenougharrows.item.TintedArrowItem;
import java.util.Objects;
import java.util.function.Function;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;

public final class TintedArrowSubtypes<S> implements ISubtypeInterpreter<S> {
  private final Function<S, String> choiceKey;

  public TintedArrowSubtypes(final Function<S, String> choiceKey) {
    this.choiceKey = Objects.requireNonNull(choiceKey, "choiceKey");
  }

  public static TintedArrowSubtypes<ItemStack> of(final TintedArrowItem item) {
    Objects.requireNonNull(item, "item");
    return new TintedArrowSubtypes<>(stack -> item.choiceOf(stack).key());
  }

  @Override
  public Object getSubtypeData(final S stack, final UidContext context) {
    return choiceKey.apply(stack);
  }

  @Override
  @SuppressWarnings("removal")
  public String getLegacyStringSubtypeInfo(final S stack, final UidContext context) {
    return choiceKey.apply(stack);
  }
}
