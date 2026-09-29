package com.grahambartley.notenougharrows.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CourierArrowPredicatesTest {

  @Test
  void aLoadedArrowSelectsTheLoadedModel() {
    assertEquals(1f, CourierArrowPredicates.loadedness(true));
  }

  @Test
  void anEmptyArrowKeepsTheEmptyModel() {
    assertEquals(0f, CourierArrowPredicates.loadedness(false));
  }

  @Test
  void namesThePredicateTheItemModelOverridesOn() {
    assertEquals(NotEnoughArrows.MOD_ID + ":loaded", CourierArrowPredicates.LOADED.toString());
  }

  @Test
  void theCourierItemModelSwapsToTheLoadedModelAtTheLoadedValue() throws IOException {
    final JsonObject override = courierModel().getAsJsonArray("overrides").get(0).getAsJsonObject();

    assertEquals(
        CourierArrowPredicates.IS_LOADED,
        override
            .getAsJsonObject("predicate")
            .get(CourierArrowPredicates.LOADED.toString())
            .getAsFloat());
    assertEquals(
        NotEnoughArrows.MOD_ID + ":item/courier_arrow_loaded", override.get("model").getAsString());
  }

  private static JsonObject courierModel() throws IOException {
    try (InputStream model =
        CourierArrowPredicatesTest.class.getResourceAsStream(
            "/assets/" + NotEnoughArrows.MOD_ID + "/models/item/courier_arrow.json")) {
      assertNotNull(model, "The courier arrow's item model should be on the classpath");
      return JsonParser.parseReader(new InputStreamReader(model, StandardCharsets.UTF_8))
          .getAsJsonObject();
    }
  }
}
