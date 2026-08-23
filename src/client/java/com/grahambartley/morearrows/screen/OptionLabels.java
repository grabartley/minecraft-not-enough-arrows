package com.grahambartley.morearrows.screen;

import com.grahambartley.morearrows.config.option.ConfigOption;
import com.grahambartley.morearrows.config.option.ConfigSection;
import net.minecraft.text.Text;

public final class OptionLabels {
  public static final String OPTION_PREFIX = "config.more-arrows.option.";
  public static final String SECTION_PREFIX = "config.more-arrows.section.";
  public static final String ROW_KEY = "config.more-arrows.entry";
  public static final String IDENTIFIER_LIST_HINT_KEY = "config.more-arrows.identifier_list.hint";

  private OptionLabels() {}

  public static String optionKey(final ConfigOption<?> option) {
    return OPTION_PREFIX + option.id();
  }

  public static String sectionKey(final ConfigSection<?> section) {
    return SECTION_PREFIX + section.id();
  }

  public static Text option(final ConfigOption<?> option) {
    return Text.translatable(optionKey(option));
  }

  public static Text section(final ConfigSection<?> section) {
    return Text.translatable(sectionKey(section));
  }
}
