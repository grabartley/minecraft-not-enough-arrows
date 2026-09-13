package com.grahambartley.notenougharrows.screen;

public record OptionRowLayout(
    int textX,
    int labelWidth,
    int descriptionWidth,
    int labelY,
    int descriptionY,
    int controlX,
    int controlY) {

  public static final int ROW_HEIGHT = 38;
  public static final int ROW_WIDTH = 340;
  public static final int EDGE_MARGIN = 20;
  public static final int CONTROL_WIDTH = 100;
  public static final int CONTROL_HEIGHT = 20;
  public static final int LABEL_OFFSET = 2;
  public static final int DESCRIPTION_OFFSET = 13;
  public static final int DESCRIPTION_LINE_HEIGHT = 10;
  public static final int MAX_DESCRIPTION_LINES = 2;
  public static final int CONTROL_OFFSET = 2;
  public static final int TEXT_GAP = 6;
  public static final int MIN_TEXT_WIDTH = 1;
  public static final int MIN_ROW_WIDTH = 1;

  public static int rowWidth(final int available) {
    return Math.max(MIN_ROW_WIDTH, Math.min(ROW_WIDTH, available - EDGE_MARGIN));
  }

  public static OptionRowLayout of(
      final int x, final int y, final int entryWidth, final int controlWidth) {
    final int reserved = controlWidth <= 0 ? 0 : controlWidth + TEXT_GAP;
    final int textWidth = Math.max(MIN_TEXT_WIDTH, entryWidth - reserved);
    return new OptionRowLayout(
        x,
        textWidth,
        textWidth,
        y + LABEL_OFFSET,
        y + DESCRIPTION_OFFSET,
        x + entryWidth - controlWidth,
        y + CONTROL_OFFSET);
  }

  public int descriptionLineY(final int line) {
    return descriptionY + line * DESCRIPTION_LINE_HEIGHT;
  }
}
