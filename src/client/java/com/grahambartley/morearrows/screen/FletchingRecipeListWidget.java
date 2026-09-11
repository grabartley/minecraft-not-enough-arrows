package com.grahambartley.morearrows.screen;

import com.grahambartley.morearrows.fletching.FletchingStationScreenHandler;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;

public final class FletchingRecipeListWidget {
  private static final int ITEM_INSET_Y = 1;

  private final FletchingStationScreenHandler handler;
  private final TextRenderer textRenderer;
  private final RegistryWrapper.WrapperLookup registries;
  private final IntConsumer onSelect;
  private final FletchingListScroll scroll = new FletchingListScroll();

  public FletchingRecipeListWidget(
      final FletchingStationScreenHandler handler,
      final TextRenderer textRenderer,
      final RegistryWrapper.WrapperLookup registries,
      final IntConsumer onSelect) {
    this.handler = handler;
    this.textRenderer = textRenderer;
    this.registries = registries;
    this.onSelect = onSelect;
  }

  public void render(
      final DrawContext context,
      final int left,
      final int top,
      final int mouseX,
      final int mouseY) {
    final List<RecipeEntry<FletchingRecipe>> recipes = recipesAfterSyncingScroll();
    final int count = recipes.size();
    final float amount = scroll.amount(count);

    final int listLeft = left + FletchingListGeometry.LIST_X;
    final int listTop = top + FletchingListGeometry.LIST_Y;
    final int first = FletchingListGeometry.topRow(count, amount);
    final int hovered = rowAt(count, amount, listLeft, listTop, mouseX, mouseY);
    final int selected = handler.getSelectedRecipe();

    for (int row = 0; row < FletchingListGeometry.visibleRows(count); row++) {
      final int index = first + row;
      if (index >= count) {
        break;
      }
      drawRow(context, recipes.get(index), index, selected, hovered, listLeft, listTop, row);
    }

    drawScroller(context, left, top, count, amount);
  }

  public void renderTooltip(
      final DrawContext context,
      final int left,
      final int top,
      final int mouseX,
      final int mouseY) {
    final List<RecipeEntry<FletchingRecipe>> recipes = recipesAfterSyncingScroll();
    final int hovered =
        rowAt(
            recipes.size(),
            scroll.amount(recipes.size()),
            left + FletchingListGeometry.LIST_X,
            top + FletchingListGeometry.LIST_Y,
            mouseX,
            mouseY);
    if (hovered == FletchingListGeometry.NO_ROW) {
      return;
    }
    context.drawItemTooltip(textRenderer, result(recipes.get(hovered)), mouseX, mouseY);
  }

  public boolean mouseClicked(
      final double mouseX, final double mouseY, final int left, final int top) {
    final int count = recipesAfterSyncingScroll().size();
    final int clicked =
        rowAt(
            count,
            scroll.amount(count),
            left + FletchingListGeometry.LIST_X,
            top + FletchingListGeometry.LIST_Y,
            mouseX,
            mouseY);
    if (clicked != FletchingListGeometry.NO_ROW) {
      scroll.endDrag();
      onSelect.accept(clicked);
      return true;
    }

    if (FletchingListGeometry.withinTrack(
        mouseX - (left + FletchingListGeometry.TRACK_X),
        mouseY - (top + FletchingListGeometry.TRACK_Y),
        count)) {
      return scroll.startDrag(count, mouseY, top + FletchingListGeometry.TRACK_Y);
    }

    scroll.endDrag();
    return false;
  }

  public boolean mouseDragged(final double mouseY, final int top) {
    final int count = recipesAfterSyncingScroll().size();
    return scroll.drag(count, mouseY, top + FletchingListGeometry.TRACK_Y);
  }

  public void mouseReleased() {
    scroll.endDrag();
  }

  public boolean mouseScrolled(final double verticalAmount) {
    return scroll.wheel(recipesAfterSyncingScroll().size(), verticalAmount);
  }

  private void drawRow(
      final DrawContext context,
      final RecipeEntry<FletchingRecipe> recipe,
      final int index,
      final int selected,
      final int hovered,
      final int listLeft,
      final int listTop,
      final int row) {
    final int rowY = listTop + row * FletchingListGeometry.ROW_HEIGHT;
    context.drawTexture(
        FletchingStationTextures.SHEET,
        listLeft,
        rowY,
        FletchingStationTextures.rowU(index, selected, hovered),
        FletchingStationTextures.ROW_V,
        FletchingListGeometry.ROW_WIDTH,
        FletchingListGeometry.ROW_HEIGHT);

    final ItemStack stack = result(recipe);
    context.drawItem(stack, listLeft, rowY + ITEM_INSET_Y);
    context.drawItemInSlot(textRenderer, stack, listLeft, rowY + ITEM_INSET_Y);
  }

  private void drawScroller(
      final DrawContext context,
      final int left,
      final int top,
      final int count,
      final float amount) {
    context.drawTexture(
        FletchingStationTextures.SHEET,
        left + FletchingListGeometry.TRACK_X,
        top + FletchingListGeometry.TRACK_Y + FletchingListGeometry.scrollerOffsetY(amount),
        FletchingStationTextures.scrollerU(FletchingListGeometry.scrollable(count)),
        FletchingStationTextures.SCROLLER_V,
        FletchingListGeometry.SCROLLER_WIDTH,
        FletchingListGeometry.SCROLLER_HEIGHT);
  }

  private ItemStack result(final RecipeEntry<FletchingRecipe> recipe) {
    return recipe.value().getResult(registries);
  }

  private int rowAt(
      final int count,
      final float amount,
      final int listLeft,
      final int listTop,
      final double mouseX,
      final double mouseY) {
    return FletchingListGeometry.rowAtOffset(count, amount, mouseX - listLeft, mouseY - listTop);
  }

  private List<RecipeEntry<FletchingRecipe>> recipesAfterSyncingScroll() {
    final List<RecipeEntry<FletchingRecipe>> recipes = handler.getAvailableRecipes();
    scroll.follow(recipes);
    return recipes;
  }
}
