package com.grahambartley.notenougharrows.item;

import com.grahambartley.notenougharrows.ModDataComponents;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.arrow.ArrowEntityFactory;
import com.grahambartley.notenougharrows.tint.TintChoice;
import com.grahambartley.notenougharrows.tint.TintPalette;
import java.util.List;
import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TintedArrowItem extends BaseArrowItem {
  public static final String NAME_KEY = "item." + NotEnoughArrows.MOD_ID + ".tinted_arrow";

  private final TintPalette palette;

  public TintedArrowItem(
      final Item.Settings settings,
      final ArrowEntityFactory arrowFactory,
      final TintPalette palette) {
    super(
        settings.component(
            ModDataComponents.ARROW_CHOICE,
            Objects.requireNonNull(palette, "palette").fallback().asComponent()),
        arrowFactory);
    this.palette = palette;
  }

  public TintPalette palette() {
    return palette;
  }

  public TintChoice choiceOf(final ItemStack stack) {
    return palette.resolve(stack.get(ModDataComponents.ARROW_CHOICE));
  }

  public ItemStack stackOf(final TintChoice choice) {
    final ItemStack stack = new ItemStack(this);
    stack.set(ModDataComponents.ARROW_CHOICE, choice.asComponent());
    return stack;
  }

  @Override
  public List<ItemStack> variants() {
    return palette.choices().stream().map(this::stackOf).toList();
  }

  @Override
  public Text getName(final ItemStack stack) {
    return Text.translatable(
        NAME_KEY, Text.translatable(choiceOf(stack).labelKey()), super.getName(stack));
  }
}
