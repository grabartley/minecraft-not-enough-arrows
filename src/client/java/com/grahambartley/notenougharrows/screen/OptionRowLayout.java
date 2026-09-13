package com.grahambartley.notenougharrows.screen;

public record OptionRowLayout(
    int textX,
    int labelWidth,
    int descriptionWidth,
    int labelY,
    int descriptionY,
    int controlX,
    int controlY) {

  public static final int LABEL_OFFSET = 3;
  public static final int DESCRIPTION_OFFSET = 20;
  public static final int CONTROL_OFFSET = 1;
  public static final int TEXT_GAP = 6;
  public static final int MIN_TEXT_WIDTH = 1;

  public static OptionRowLayout of(
      final int x, final int y, final int entryWidth, final int controlWidth) {
    final int reserved = controlWidth <= 0 ? 0 : controlWidth + TEXT_GAP;
    return new OptionRowLayout(
        x,
        Math.max(MIN_TEXT_WIDTH, entryWidth - reserved),
        Math.max(MIN_TEXT_WIDTH, entryWidth),
        y + LABEL_OFFSET,
        y + DESCRIPTION_OFFSET,
        x + entryWidth - controlWidth,
        y + CONTROL_OFFSET);
  }
}
