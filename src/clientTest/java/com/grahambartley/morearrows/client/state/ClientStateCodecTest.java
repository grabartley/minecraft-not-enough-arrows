package com.grahambartley.morearrows.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ClientStateCodecTest {

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\n"})
  void decodesNothingAsDefaults(final String json) {
    assertEquals(ClientState.defaults(), ClientStateCodec.decode(json));
  }

  @Test
  void survivesARoundTripThroughTheEncodedForm() {
    final ClientState state = new ClientState(false, true, 1.75f);

    assertEquals(state, ClientStateCodec.decode(ClientStateCodec.encode(state)));
  }

  @Test
  void readsAHandEditedDocument() {
    assertEquals(
        new ClientState(false, false, 0.5f),
        ClientStateCodec.decode(
            "{\"showCountdownRing\": false, \"playCountdownSound\": false,"
                + " \"countdownRingScale\": 0.5}"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"{ not json", "[1, 2, 3]", "\"a string\"", "12", "null"})
  void refusesContentsThatAreNotAJsonObject(final String json) {
    assertThrows(JsonParseException.class, () -> ClientStateCodec.decode(json));
  }

  @Test
  void encodesPrettyPrintedJsonSoPlayersCanEditItByHand() {
    assertTrue(ClientStateCodec.encode(ClientState.defaults()).contains("\n"));
  }
}
