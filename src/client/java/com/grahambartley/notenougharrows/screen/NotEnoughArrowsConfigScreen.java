package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.client.state.ClientState;
import com.grahambartley.notenougharrows.client.state.ClientStateService;
import com.grahambartley.notenougharrows.config.ClientConfigHolder;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.option.ConfigSection;
import com.grahambartley.notenougharrows.config.option.ServerConfigOptions;
import com.grahambartley.notenougharrows.network.ServerConfigUpdateSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class NotEnoughArrowsConfigScreen extends Screen {
  public static final String TITLE_KEY = "config.not-enough-arrows.title";
  public static final String RESET_KEY = "config.not-enough-arrows.reset";
  public static final String SAVE_FAILED_KEY = "config.not-enough-arrows.save.failed";
  public static final String DONE_KEY = "gui.done";
  public static final String CANCEL_KEY = "gui.cancel";

  private static final int HEADER_HEIGHT = 33;
  private static final int NOTICE_HEADER_HEIGHT = 48;
  private static final int BUTTON_WIDTH = 100;
  private static final int BUTTON_HEIGHT = 20;
  private static final int FOOTER_SPACING = 8;
  private static final int HEADER_SPACING = 4;

  private final Screen parent;
  private final ServerConfigAccess access;
  private final ConfigDraft<NotEnoughArrowsConfig> serverDraft;
  private final ConfigDraft<ClientState> clientDraft;
  private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

  private ConfigOptionListWidget list;

  public NotEnoughArrowsConfigScreen(
      final Screen parent,
      final ServerConfigAccess access,
      final NotEnoughArrowsConfig serverConfig,
      final ClientState clientState) {
    super(Text.translatable(TITLE_KEY));
    this.parent = parent;
    this.access = access;
    this.serverDraft = new ConfigDraft<>(serverConfig);
    this.clientDraft = new ConfigDraft<>(clientState);
  }

  public static NotEnoughArrowsConfigScreen create(final Screen parent) {
    return new NotEnoughArrowsConfigScreen(
        parent,
        ServerConfigAccess.of(MinecraftClient.getInstance()),
        ClientConfigHolder.get(),
        ClientStateService.get());
  }

  @Override
  protected void init() {
    layout.setHeaderHeight(access.editable() ? HEADER_HEIGHT : NOTICE_HEADER_HEIGHT);
    layout.addHeader(buildHeader());

    list =
        layout.addBody(
            new ConfigOptionListWidget(
                client, width, layout.getContentHeight(), layout.getHeaderHeight()));
    for (final ConfigSection<NotEnoughArrowsConfig> section : ServerConfigOptions.sections()) {
      list.addSection(section, serverDraft, access.editable());
    }
    list.addSection(ClientStateOptions.section(), clientDraft, true);

    layout.addFooter(buildFooter());
    layout.forEachChild(this::addDrawableChild);
    initTabNavigation();
  }

  private DirectionalLayoutWidget buildHeader() {
    final DirectionalLayoutWidget header =
        DirectionalLayoutWidget.vertical().spacing(HEADER_SPACING);
    header.getMainPositioner().alignHorizontalCenter();
    header.add(new TextWidget(title, textRenderer));
    access
        .messageKey()
        .ifPresent(
            key ->
                header.add(
                    new TextWidget(
                        Text.translatable(key).formatted(Formatting.YELLOW), textRenderer)));
    return header;
  }

  private DirectionalLayoutWidget buildFooter() {
    final DirectionalLayoutWidget footer =
        DirectionalLayoutWidget.horizontal().spacing(FOOTER_SPACING);
    footer.add(button(Text.translatable(RESET_KEY), this::resetToDefaults));
    footer.add(button(Text.translatable(CANCEL_KEY), this::close));
    footer.add(button(Text.translatable(DONE_KEY), this::saveAndClose));
    return footer;
  }

  private ButtonWidget button(final Text label, final Runnable action) {
    return ButtonWidget.builder(label, ignored -> action.run())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .build();
  }

  private void resetToDefaults() {
    if (access.editable()) {
      serverDraft.set(NotEnoughArrowsConfig.defaults());
    }
    clientDraft.set(ClientState.defaults());
    clearAndInit();
  }

  private void saveAndClose() {
    if (clientDraft.isDirty() && !ClientStateService.update(clientDraft.current())) {
      warn();
      return;
    }
    if (access.editable()
        && serverDraft.isDirty()
        && !ServerConfigUpdateSender.send(serverDraft.current())) {
      warn();
      return;
    }
    close();
  }

  private void warn() {
    if (client != null && client.player != null) {
      client.player.sendMessage(Text.translatable(SAVE_FAILED_KEY), false);
    }
    close();
  }

  @Override
  protected void initTabNavigation() {
    layout.refreshPositions();
    if (list != null) {
      list.position(width, layout);
    }
  }

  @Override
  public void close() {
    if (client != null) {
      client.setScreen(parent);
    }
  }
}
