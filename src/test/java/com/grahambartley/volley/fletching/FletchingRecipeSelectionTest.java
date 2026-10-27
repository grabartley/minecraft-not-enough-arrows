package com.grahambartley.volley.fletching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingRecipeSelectionTest {

  @ParameterizedTest(name = "{0} after {1} -> {2}")
  @CsvSource({
    "'', a, -1",
    "a, b, 0",
    "a, , 0",
    "a|b|c, b, 1",
    "a|b|c, c, 2",
    "a|b|c, d, -1",
    "a|b|c, , -1"
  })
  void keepsThePreviousRecipeSelectedOnlyWhileItIsStillOnOffer(
      final String available, final String previous, final int expected) {
    assertEquals(
        expected,
        FletchingRecipeSelection.reselect(ids(available), previous == null ? null : id(previous)));
  }

  private static List<Identifier> ids(final String joined) {
    return joined.isEmpty()
        ? List.of()
        : Arrays.stream(joined.split("\\|")).map(FletchingRecipeSelectionTest::id).toList();
  }

  private static Identifier id(final String path) {
    return Identifier.of("volley", path);
  }
}
