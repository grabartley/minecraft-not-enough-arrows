package com.grahambartley.morearrows.compat.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.morearrows.MoreArrows;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InfoKeysTest {

  @ParameterizedTest
  @CsvSource({
    "tnt_arrow, info.more-arrows.tnt_arrow",
    "rope_arrow, info.more-arrows.rope_arrow",
    "wind_arrow, info.more-arrows.wind_arrow"
  })
  void namesADescriptionKeyPerModItem(final String path, final String expected) {
    assertEquals(expected, InfoKeys.description(Identifier.of(MoreArrows.MOD_ID, path)));
  }

  @Test
  void keepsTheNamespaceSoItemsFromAnotherModCannotCollide() {
    assertEquals("info.minecraft.arrow", InfoKeys.description(Identifier.ofVanilla("arrow")));
    assertNotEquals(
        InfoKeys.description(Identifier.ofVanilla("arrow")),
        InfoKeys.description(Identifier.of(MoreArrows.MOD_ID, "arrow")));
  }

  @Test
  void namesTheSharedFiringKeyUnderTheModNamespace() {
    assertEquals("info.more-arrows.shared.firing", InfoKeys.FIRING_KEY);
  }

  @Test
  void rejectsANullItemId() {
    assertThrows(NullPointerException.class, () -> InfoKeys.description(null));
  }
}
