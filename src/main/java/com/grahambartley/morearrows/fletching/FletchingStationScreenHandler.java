package com.grahambartley.morearrows.fletching;

import com.grahambartley.morearrows.ModRecipes;
import com.grahambartley.morearrows.ModScreenHandlers;
import com.grahambartley.morearrows.recipe.FletchingRecipe;
import com.grahambartley.morearrows.recipe.FletchingRecipeInput;
import com.grahambartley.morearrows.recipe.FletchingWithdrawal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class FletchingStationScreenHandler extends ScreenHandler {
  public static final int NO_SELECTION = -1;

  private final ScreenHandlerContext context;
  private final World world;
  private final CraftingResultInventory result = new CraftingResultInventory();
  private final SimpleInventory input = new SimpleInventory(FletchingStationSlots.INPUT_COUNT);
  private final Property selectedRecipe = Property.create();

  private List<RecipeEntry<FletchingRecipe>> availableRecipes = List.of();
  private boolean withdrawing;

  public FletchingStationScreenHandler(final int syncId, final PlayerInventory playerInventory) {
    this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
  }

  public FletchingStationScreenHandler(
      final int syncId, final PlayerInventory playerInventory, final ScreenHandlerContext context) {
    super(ModScreenHandlers.FLETCHING_STATION, syncId);
    this.context = context;
    this.world = playerInventory.player.getWorld();
    this.selectedRecipe.set(NO_SELECTION);
    this.input.addListener(changed -> onInputChanged());

    addInputSlots();
    addResultSlot(playerInventory.player);
    addPlayerSlots(playerInventory);
    addProperty(selectedRecipe);
  }

  public int getSelectedRecipe() {
    return selectedRecipe.get();
  }

  public List<RecipeEntry<FletchingRecipe>> getAvailableRecipes() {
    return availableRecipes;
  }

  @Override
  public boolean canUse(final PlayerEntity player) {
    return canUse(context, player, Blocks.FLETCHING_TABLE);
  }

  @Override
  public boolean onButtonClick(final PlayerEntity player, final int id) {
    if (id < 0 || id >= availableRecipes.size()) {
      return false;
    }
    selectedRecipe.set(id);
    populateResult();
    return true;
  }

  @Override
  public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
    return slot.inventory != result && super.canInsertIntoSlot(stack, slot);
  }

  @Override
  public ItemStack quickMove(final PlayerEntity player, final int index) {
    final Slot slot = slots.get(index);
    if (!slot.hasStack()) {
      return ItemStack.EMPTY;
    }

    final ItemStack slotStack = slot.getStack();
    final ItemStack original = slotStack.copy();
    if (!moveOut(index, slot, slotStack, original)) {
      return ItemStack.EMPTY;
    }

    if (slotStack.isEmpty()) {
      slot.setStack(ItemStack.EMPTY);
    } else {
      slot.markDirty();
    }
    if (slotStack.getCount() == original.getCount()) {
      return ItemStack.EMPTY;
    }

    slot.onTakeItem(player, slotStack);
    if (FletchingStationSlots.isResult(index)) {
      player.dropItem(slotStack, false);
    }
    sendContentUpdates();
    return original;
  }

  @Override
  public void onClosed(final PlayerEntity player) {
    super.onClosed(player);
    result.removeStack(0);
    context.run((closedWorld, pos) -> dropInventory(player, input));
  }

  private boolean moveOut(
      final int index, final Slot slot, final ItemStack slotStack, final ItemStack original) {
    if (FletchingStationSlots.isResult(index)) {
      if (!insertItem(
          slotStack,
          FletchingStationSlots.FIRST_PLAYER_SLOT,
          FletchingStationSlots.TOTAL_SLOTS,
          true)) {
        return false;
      }
      slot.onQuickTransfer(slotStack, original);
      return true;
    }
    if (FletchingStationSlots.isInput(index)) {
      return insertItem(
          slotStack,
          FletchingStationSlots.FIRST_PLAYER_SLOT,
          FletchingStationSlots.TOTAL_SLOTS,
          false);
    }
    if (insertItem(
        slotStack,
        FletchingStationSlots.FIRST_INPUT_SLOT,
        FletchingStationSlots.RESULT_SLOT,
        false)) {
      return true;
    }
    return index < FletchingStationSlots.FIRST_HOTBAR_SLOT
        ? insertItem(
            slotStack,
            FletchingStationSlots.FIRST_HOTBAR_SLOT,
            FletchingStationSlots.TOTAL_SLOTS,
            false)
        : insertItem(
            slotStack,
            FletchingStationSlots.FIRST_PLAYER_SLOT,
            FletchingStationSlots.FIRST_HOTBAR_SLOT,
            false);
  }

  private void addInputSlots() {
    for (int index = 0; index < FletchingStationSlots.INPUT_COUNT; index++) {
      addSlot(
          new Slot(
              input,
              index,
              FletchingStationLayout.inputX(index),
              FletchingStationLayout.inputY(index)));
    }
  }

  private void addResultSlot(final PlayerEntity player) {
    addSlot(
        new FletchingStationResultSlot(
            player,
            result,
            0,
            FletchingStationLayout.RESULT_X,
            FletchingStationLayout.RESULT_Y,
            this::onResultTaken));
  }

  private void addPlayerSlots(final PlayerInventory playerInventory) {
    for (int index = 0; index < FletchingStationSlots.PLAYER_SLOT_COUNT; index++) {
      final int inventoryIndex =
          index < FletchingStationSlots.PLAYER_MAIN_COUNT
              ? index + FletchingStationSlots.HOTBAR_COUNT
              : index - FletchingStationSlots.PLAYER_MAIN_COUNT;
      addSlot(
          new Slot(
              playerInventory,
              inventoryIndex,
              FletchingStationLayout.playerX(index),
              FletchingStationLayout.playerY(index)));
    }
  }

  private void onInputChanged() {
    if (withdrawing) {
      return;
    }
    updateAvailableRecipes();
    populateResult();
  }

  private void updateAvailableRecipes() {
    final Identifier previous = selectedRecipeId();
    availableRecipes =
        input.isEmpty()
            ? List.of()
            : world.getRecipeManager().getAllMatches(ModRecipes.FLETCHING, recipeInput(), world);
    selectedRecipe.set(reselect(previous));
  }

  private int reselect(final Identifier previous) {
    if (availableRecipes.isEmpty()) {
      return NO_SELECTION;
    }
    if (availableRecipes.size() == 1) {
      return 0;
    }
    for (int index = 0; index < availableRecipes.size(); index++) {
      if (availableRecipes.get(index).id().equals(previous)) {
        return index;
      }
    }
    return NO_SELECTION;
  }

  private Identifier selectedRecipeId() {
    final RecipeEntry<FletchingRecipe> entry = selectedEntry();
    return entry == null ? null : entry.id();
  }

  private RecipeEntry<FletchingRecipe> selectedEntry() {
    final int index = selectedRecipe.get();
    return index < 0 || index >= availableRecipes.size() ? null : availableRecipes.get(index);
  }

  private void populateResult() {
    final RecipeEntry<FletchingRecipe> entry = selectedEntry();
    if (entry == null) {
      result.setLastRecipe(null);
      result.setStack(0, ItemStack.EMPTY);
    } else {
      result.setLastRecipe(entry);
      result.setStack(0, entry.value().craft(recipeInput(), world.getRegistryManager()));
    }
    sendContentUpdates();
  }

  private void onResultTaken() {
    final RecipeEntry<FletchingRecipe> entry = selectedEntry();
    if (entry == null) {
      return;
    }
    final List<FletchingWithdrawal> plan = FletchingWithdrawal.plan(entry.value(), recipeInput());
    if (plan.isEmpty()) {
      return;
    }
    withdraw(plan);
  }

  private void withdraw(final List<FletchingWithdrawal> plan) {
    withdrawing = true;
    try {
      for (final FletchingWithdrawal draw : plan) {
        input.removeStack(draw.slot(), draw.count());
      }
    } finally {
      withdrawing = false;
    }
    onInputChanged();
  }

  private FletchingRecipeInput recipeInput() {
    final List<ItemStack> stacks = new ArrayList<>(input.size());
    for (int slot = 0; slot < input.size(); slot++) {
      stacks.add(input.getStack(slot));
    }
    return new FletchingRecipeInput(stacks);
  }
}
