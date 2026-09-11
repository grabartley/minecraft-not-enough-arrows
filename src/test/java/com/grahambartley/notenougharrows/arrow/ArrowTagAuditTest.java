package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArrowTagAuditTest {

  private static Identifier arrow(final String path) {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }

  @Test
  void reportsNothingWhenEveryRegisteredArrowIsTagged() {
    final List<Identifier> registered = List.of(arrow("tnt_arrow"), arrow("rope_arrow"));

    assertTrue(ArrowTagAudit.unfireable(registered, registered).isEmpty());
  }

  @Test
  void reportsEveryRegisteredArrowMissingFromTheTag() {
    final List<Identifier> registered =
        List.of(arrow("tnt_arrow"), arrow("rope_arrow"), arrow("wind_arrow"));

    assertEquals(
        List.of(arrow("rope_arrow"), arrow("wind_arrow")),
        ArrowTagAudit.unfireable(registered, List.of(arrow("tnt_arrow"))));
  }

  @Test
  void reportsEveryRegisteredArrowWhenTheTagIsEmpty() {
    final List<Identifier> registered = List.of(arrow("tnt_arrow"), arrow("rope_arrow"));

    assertEquals(registered, ArrowTagAudit.unfireable(registered, List.of()));
  }

  @Test
  void reportsNothingWhenNoArrowIsRegisteredYet() {
    assertTrue(ArrowTagAudit.unfireable(List.of(), List.of(arrow("tnt_arrow"))).isEmpty());
  }

  @Test
  void ignoresTaggedArrowsTheModDidNotRegister() {
    final List<Identifier> registered = List.of(arrow("tnt_arrow"));
    final List<Identifier> tagged =
        List.of(Identifier.ofVanilla("arrow"), Identifier.ofVanilla("spectral_arrow"));

    assertEquals(registered, ArrowTagAudit.unfireable(registered, tagged));
  }

  @Test
  void preservesRegistrationOrderSoTheReportReadsLikeTheCatalog() {
    final List<Identifier> registered =
        List.of(arrow("wind_arrow"), arrow("tnt_arrow"), arrow("rope_arrow"));

    assertEquals(registered, ArrowTagAudit.unfireable(registered, List.of()));
  }

  @Test
  void acceptsAnyCollectionShapeForTheTagSide() {
    final List<Identifier> registered = List.of(arrow("tnt_arrow"), arrow("rope_arrow"));

    assertEquals(
        List.of(arrow("rope_arrow")),
        ArrowTagAudit.unfireable(registered, Set.of(arrow("tnt_arrow"))));
  }

  @ParameterizedTest
  @CsvSource({"true, false", "false, true", "true, true"})
  void rejectsNullCollections(final boolean nullRegistered, final boolean nullTagged) {
    final List<Identifier> registered = nullRegistered ? null : List.of();
    final List<Identifier> tagged = nullTagged ? null : List.of();

    assertThrows(NullPointerException.class, () -> ArrowTagAudit.unfireable(registered, tagged));
  }
}
