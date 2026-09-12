package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.fletching.FletchingStationScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class FletchingStationScreen extends HandledScreen<FletchingStationScreenHandler> {
  private FletchingRecipeListWidget recipeList;

  public FletchingStationScreen(
      final FletchingStationScreenHandler handler,
      final PlayerInventory inventory,
      final Text title) {
    super(handler, inventory, title);
    backgroundWidth = FletchingStationTextures.PANEL_WIDTH;
    backgroundHeight = FletchingStationTextures.PANEL_HEIGHT;
    playerInventoryTitleY = backgroundHeight - 94;
  }

  @Override
  protected void init() {
    super.init();
    if (recipeList == null) {
      recipeList =
          new FletchingRecipeListWidget(
              handler, textRenderer, client.world.getRegistryManager(), this::select);
    }
  }

  @Override
  public void render(
      final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    super.render(context, mouseX, mouseY, delta);
    drawMouseoverTooltip(context, mouseX, mouseY);
  }

  @Override
  protected void drawBackground(
      final DrawContext context, final float delta, final int mouseX, final int mouseY) {
    context.drawTexture(
        FletchingStationTextures.SHEET,
        x,
        y,
        FletchingStationTextures.PANEL_U,
        FletchingStationTextures.PANEL_V,
        FletchingStationTextures.PANEL_WIDTH,
        FletchingStationTextures.PANEL_HEIGHT);
    recipeList.render(context, x, y, mouseX, mouseY);
  }

  @Override
  protected void drawMouseoverTooltip(
      final DrawContext context, final int mouseX, final int mouseY) {
    super.drawMouseoverTooltip(context, mouseX, mouseY);
    recipeList.renderTooltip(context, x, y, mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
    return recipeList.mouseClicked(mouseX, mouseY, x, y)
        || super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(
      final double mouseX,
      final double mouseY,
      final int button,
      final double deltaX,
      final double deltaY) {
    return recipeList.mouseDragged(mouseY, y)
        || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
    recipeList.mouseReleased();
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(
      final double mouseX,
      final double mouseY,
      final double horizontalAmount,
      final double verticalAmount) {
    return recipeList.mouseScrolled(verticalAmount)
        || super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  private void select(final int index) {
    if (client == null || client.player == null || client.interactionManager == null) {
      return;
    }
    if (!handler.onButtonClick(client.player, index)) {
      return;
    }
    client
        .getSoundManager()
        .play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
    client.interactionManager.clickButton(handler.syncId, index);
  }
}
