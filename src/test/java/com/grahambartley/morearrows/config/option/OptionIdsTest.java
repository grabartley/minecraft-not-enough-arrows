package com.grahambartley.morearrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class OptionIdsTest {

  @Test
  void returnsTheIdItWasGiven() {
    assertEquals("explosive.beepVolume", OptionIds.require("explosive.beepVolume"));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", " ", "\t"})
  void rejectsAMissingId(final String id) {
    assertThrows(IllegalArgumentException.class, () -> OptionIds.require(id));
  }
}
