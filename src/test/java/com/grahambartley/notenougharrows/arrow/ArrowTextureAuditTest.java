package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ArrowTextureAuditTest {

  private static Identifier arrow(final String path) {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }

  private static Map<Identifier, Optional<String>> digests(final String... arrowThenDigest) {
    final Map<Identifier, Optional<String>> digests = new LinkedHashMap<>();
    for (int i = 0; i < arrowThenDigest.length; i += 2) {
      digests.put(arrow(arrowThenDigest[i]), Optional.ofNullable(arrowThenDigest[i + 1]));
    }
    return digests;
  }

  @Test
  void passesArrowsThatEachHaveTheirOwnTexture() {
    final Map<Identifier, Optional<String>> digests =
        digests("drill_arrow", "aa", "drain_arrow", "bb");

    assertTrue(ArrowTextureAudit.missing(digests).isEmpty());
    assertTrue(ArrowTextureAudit.shared(digests).isEmpty());
  }

  @Test
  void failsAnArrowWithNoTexture() {
    assertEquals(
        List.of(arrow("web_arrow")),
        ArrowTextureAudit.missing(digests("drill_arrow", "aa", "web_arrow", null)));
  }

  @Test
  void failsTwoArrowsSharingOneTexture() {
    assertEquals(
        List.of(arrow("drill_arrow"), arrow("pillar_arrow")),
        ArrowTextureAudit.shared(
            digests("drill_arrow", "aa", "drain_arrow", "bb", "pillar_arrow", "aa")));
  }

  @Test
  void namesEveryArrowInAGroupOfThreeOnce() {
    assertEquals(
        List.of(arrow("a_arrow"), arrow("b_arrow"), arrow("c_arrow")),
        ArrowTextureAudit.shared(digests("a_arrow", "aa", "b_arrow", "aa", "c_arrow", "aa")));
  }

  @Test
  void twoMissingTexturesAreNotReportedAsShared() {
    assertTrue(ArrowTextureAudit.shared(digests("a_arrow", null, "b_arrow", null)).isEmpty());
  }

  @Test
  void rejectsNullInputs() {
    assertThrows(NullPointerException.class, () -> ArrowTextureAudit.missing(null));
    assertThrows(NullPointerException.class, () -> ArrowTextureAudit.shared(null));
  }
}
