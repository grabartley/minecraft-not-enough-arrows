package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import org.junit.jupiter.api.Test;

class SoundOptionsTest {

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
}
