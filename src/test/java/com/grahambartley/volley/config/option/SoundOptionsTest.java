package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SoundOptionsTest {

  private static Stream<ConfigOption<VolleyConfig>> options() {
    return SoundOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of("sound.volume"), SoundOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("sound", SoundOptions.section().id());
    assertEquals(SoundOptions.options(), SoundOptions.section().options());
  }

  @Test
  void theVolumeWritesIntoTheSoundFamily() {
    final FloatOption<VolleyConfig> volume =
        (FloatOption<VolleyConfig>) SoundOptions.options().getFirst();

    assertEquals(0.5f, volume.write(VolleyConfig.defaults(), 0.5f).sound().volume());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionChangesThatOptionsValue(final ConfigOption<VolleyConfig> option) {
    final VolleyConfig before = VolleyConfig.defaults();
    final VolleyConfig after = OptionMutations.toDifferentValue(option, before);

    assertNotEquals(option.displayValue(before), option.displayValue(after));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionLeavesEveryOtherSettingAlone(final ConfigOption<VolleyConfig> option) {
    final VolleyConfig before = VolleyConfig.defaults();
    final VolleyConfig after = OptionMutations.toDifferentValue(option, before);

    for (final ConfigOption<VolleyConfig> other : ServerConfigOptions.all()) {
      if (!other.id().equals(option.id())) {
        assertEquals(other.displayValue(before), other.displayValue(after), other.id());
      }
    }
  }
}
