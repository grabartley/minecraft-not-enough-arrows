package com.grahambartley.volley.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IdentifierNamesTest {

  @ParameterizedTest
  @CsvSource({
    "minecraft:stone, minecraft:stone",
    "stone, minecraft:stone",
    "'  Minecraft:STONE ', minecraft:stone",
    "volley:rope_arrow, volley:rope_arrow",
    "coal_ore, minecraft:coal_ore"
  })
  void normalizesToTheFullLowercaseIdentifier(final String raw, final String expected) {
    assertEquals(Optional.of(expected), IdentifierNames.normalize(raw));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "bad id!", "a:b:c", "minecraft:stone block"})
  void rejectsWhatIsNotAnIdentifier(final String raw) {
    assertEquals(Optional.empty(), IdentifierNames.normalize(raw));
  }
}
