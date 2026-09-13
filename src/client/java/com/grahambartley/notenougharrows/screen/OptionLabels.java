package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.option.ConfigOption;
import com.grahambartley.notenougharrows.config.option.ConfigSection;
import net.minecraft.text.Text;

public final class OptionLabels {
  public static final String OPTION_PREFIX = "config.not-enough-arrows.option.";
  public static final String SECTION_PREFIX = "config.not-enough-arrows.section.";
  public static final String DESCRIPTION_SUFFIX = ".description";
  public static final String IDENTIFIER_LIST_HINT_KEY =
      "config.not-enough-arrows.identifier_list.hint";

  private OptionLabels() {}

  public static String optionKey(final ConfigOption<?> option) {
    return OPTION_PREFIX + option.id();
  }

  public static String optionDescriptionKey(final ConfigOption<?> option) {
    return optionKey(option) + DESCRIPTION_SUFFIX;
  }

  public static String sectionKey(final ConfigSection<?> section) {
    return SECTION_PREFIX + section.id();
  }

  public static String sectionDescriptionKey(final ConfigSection<?> section) {
    return sectionKey(section) + DESCRIPTION_SUFFIX;
  }

  public static Text option(final ConfigOption<?> option) {
    return Text.translatable(optionKey(option));
  }

  public static Text optionDescription(final ConfigOption<?> option) {
    return Text.translatable(optionDescriptionKey(option));
  }

  public static Text section(final ConfigSection<?> section) {
    return Text.translatable(sectionKey(section));
  }

  public static Text sectionDescription(final ConfigSection<?> section) {
    return Text.translatable(sectionDescriptionKey(section));
  }
}
