package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grahambartley.notenougharrows.config.option.ConfigOption;
import com.grahambartley.notenougharrows.config.option.ConfigSection;
import com.grahambartley.notenougharrows.config.option.ServerConfigOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OptionLabelsTest {
  private static final String LANG_PATH = "/assets/not-enough-arrows/lang/en_us.json";
  private static final int WIDEST_GLYPH_PIXELS = 6;
  private static final int MAX_DESCRIPTION_CHARACTERS =
      OptionRowLayout.ROW_WIDTH / WIDEST_GLYPH_PIXELS;
  private static final int MAX_LABEL_CHARACTERS =
      (OptionRowLayout.ROW_WIDTH - OptionRowLayout.CONTROL_WIDTH - OptionRowLayout.TEXT_GAP)
          / WIDEST_GLYPH_PIXELS;
  private static final JsonObject ENGLISH = english();
  private static final Set<String> ENGLISH_KEYS = Set.copyOf(ENGLISH.keySet());

  private static Stream<ConfigOption<?>> options() {
    return Stream.concat(ServerConfigOptions.all().stream(), ClientStateOptions.options().stream());
  }

  private static Stream<ConfigSection<?>> sections() {
    return Stream.concat(
        ServerConfigOptions.sections().stream(), Stream.of(ClientStateOptions.section()));
  }

  @Test
  void buildsAnOptionKeyFromItsId() {
    final ConfigOption<?> option = ServerConfigOptions.all().get(0);

    assertEquals(OptionLabels.OPTION_PREFIX + option.id(), OptionLabels.optionKey(option));
  }

  @Test
  void buildsASectionKeyFromItsId() {
    final ConfigSection<?> section = ServerConfigOptions.sections().get(0);

    assertEquals(OptionLabels.SECTION_PREFIX + section.id(), OptionLabels.sectionKey(section));
  }

  @ParameterizedTest
  @MethodSource("options")
  void hangsAnOptionDescriptionOffItsOwnKey(final ConfigOption<?> option) {
    assertEquals(
        OptionLabels.optionKey(option) + OptionLabels.DESCRIPTION_SUFFIX,
        OptionLabels.optionDescriptionKey(option));
    assertEquals(OptionLabels.optionKey(option), key(OptionLabels.option(option)));
    assertEquals(
        OptionLabels.optionDescriptionKey(option), key(OptionLabels.optionDescription(option)));
  }

  @ParameterizedTest
  @MethodSource("sections")
  void hangsASectionDescriptionOffItsOwnKey(final ConfigSection<?> section) {
    assertEquals(
        OptionLabels.sectionKey(section) + OptionLabels.DESCRIPTION_SUFFIX,
        OptionLabels.sectionDescriptionKey(section));
    assertEquals(OptionLabels.sectionKey(section), key(OptionLabels.section(section)));
    assertEquals(
        OptionLabels.sectionDescriptionKey(section), key(OptionLabels.sectionDescription(section)));
  }

  @ParameterizedTest
  @MethodSource("options")
  void shipsEnglishForEveryOptionItLabels(final ConfigOption<?> option) {
    assertTrue(
        ENGLISH_KEYS.contains(OptionLabels.optionKey(option)),
        "Missing English for " + OptionLabels.optionKey(option));
    assertTrue(
        ENGLISH_KEYS.contains(OptionLabels.optionDescriptionKey(option)),
        "Missing English for " + OptionLabels.optionDescriptionKey(option));
  }

  @ParameterizedTest
  @MethodSource("sections")
  void shipsEnglishForEverySectionItLabels(final ConfigSection<?> section) {
    assertTrue(
        ENGLISH_KEYS.contains(OptionLabels.sectionKey(section)),
        "Missing English for " + OptionLabels.sectionKey(section));
    assertTrue(
        ENGLISH_KEYS.contains(OptionLabels.sectionDescriptionKey(section)),
        "Missing English for " + OptionLabels.sectionDescriptionKey(section));
  }

  @Test
  void leavesNoOrphanedOptionOrSectionKeysBehind() {
    final Set<String> expected =
        Stream.concat(
                options()
                    .flatMap(
                        option ->
                            Stream.of(
                                OptionLabels.optionKey(option),
                                OptionLabels.optionDescriptionKey(option))),
                sections()
                    .flatMap(
                        section ->
                            Stream.of(
                                OptionLabels.sectionKey(section),
                                OptionLabels.sectionDescriptionKey(section))))
            .collect(Collectors.toSet());
    final Set<String> shipped =
        ENGLISH_KEYS.stream()
            .filter(
                key ->
                    key.startsWith(OptionLabels.OPTION_PREFIX)
                        || key.startsWith(OptionLabels.SECTION_PREFIX))
            .collect(Collectors.toSet());

    assertEquals(expected, shipped);
  }

  @ParameterizedTest
  @MethodSource("options")
  void keepsAnOptionLabelClearOfTheControlBesideIt(final ConfigOption<?> option) {
    assertFits(
        OptionLabels.optionKey(option),
        OptionWidgets.fitsBesideItsLabel(option)
            ? MAX_LABEL_CHARACTERS
            : MAX_DESCRIPTION_CHARACTERS);
  }

  @ParameterizedTest
  @MethodSource("sections")
  void keepsASectionHeadingInsideTheRowItIsDrawnOn(final ConfigSection<?> section) {
    assertWithinTheRow(OptionLabels.sectionKey(section));
  }

  @ParameterizedTest
  @MethodSource("options")
  void keepsAnOptionDescriptionInsideTheRowItIsDrawnOn(final ConfigOption<?> option) {
    assertWithinTheRow(OptionLabels.optionDescriptionKey(option));
  }

  @ParameterizedTest
  @MethodSource("sections")
  void keepsASectionDescriptionInsideTheRowItIsDrawnOn(final ConfigSection<?> section) {
    assertWithinTheRow(OptionLabels.sectionDescriptionKey(section));
  }

  private static void assertWithinTheRow(final String key) {
    assertFits(key, MAX_DESCRIPTION_CHARACTERS);
  }

  private static void assertFits(final String key, final int budget) {
    final String english = ENGLISH.get(key).getAsString();

    assertTrue(
        english.length() <= budget,
        key
            + " is "
            + english.length()
            + " characters, which overflows the "
            + budget
            + " its row leaves it and is silently trimmed on screen");
  }

  private static String key(final Text text) {
    assertInstanceOf(TranslatableTextContent.class, text.getContent());
    return ((TranslatableTextContent) text.getContent()).getKey();
  }

  private static JsonObject english() {
    try (InputStream stream = OptionLabelsTest.class.getResourceAsStream(LANG_PATH)) {
      if (stream == null) {
        throw new IllegalStateException("Could not find " + LANG_PATH + " on the test classpath");
      }
      return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
          .getAsJsonObject();
    } catch (final IOException exception) {
      throw new IllegalStateException("Could not read " + LANG_PATH, exception);
    }
  }
}
